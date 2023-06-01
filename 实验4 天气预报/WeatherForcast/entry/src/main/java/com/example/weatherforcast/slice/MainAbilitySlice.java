package com.example.weatherforcast.slice;

import com.example.weatherforcast.ResourceTable;
import com.example.weatherforcast.constant.Constant;
import com.example.weatherforcast.model.RecordBean;
import com.example.weatherforcast.model.RecordDbStore;
import com.example.weatherforcast.provider.WeatherPageProvider;
import com.example.weatherforcast.utils.HttpUtil;
import com.example.weatherforcast.utils.JsonUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import jxl.Cell;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.weatherforcast.utils.QueryExcelUtil.get_city_response;

public class MainAbilitySlice extends AbilitySlice {
    public static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "XLC");
    private String[] detailItems = new String[]{"查询城市天气", "查看城市列表", "收藏"};

    private PageSlider pageSlider;
    private ArrayList<Component> dataItems;

    private List<RecordBean> weatherBeanList;
    private DatabaseHelper helper;
    private String[] dates = new String[]{"今天", "明天", "后天"};

    public static final int[] resources = new int[]{ResourceTable.Media_code_0, ResourceTable.Media_code_1, ResourceTable.Media_code_2, ResourceTable.Media_code_3, ResourceTable.Media_code_4,
            ResourceTable.Media_code_5, ResourceTable.Media_code_6, ResourceTable.Media_code_7, ResourceTable.Media_code_8, ResourceTable.Media_code_9,
            ResourceTable.Media_code_10, ResourceTable.Media_code_11, ResourceTable.Media_code_12, ResourceTable.Media_code_13, ResourceTable.Media_code_14,
            ResourceTable.Media_code_15, ResourceTable.Media_code_16, ResourceTable.Media_code_17, ResourceTable.Media_code_18, ResourceTable.Media_code_19,
            ResourceTable.Media_code_20, ResourceTable.Media_code_21, ResourceTable.Media_code_22, ResourceTable.Media_code_23, ResourceTable.Media_code_24,
            ResourceTable.Media_code_25, ResourceTable.Media_code_26, ResourceTable.Media_code_27, ResourceTable.Media_code_28, ResourceTable.Media_code_29,
            ResourceTable.Media_code_30, ResourceTable.Media_code_31, ResourceTable.Media_code_32, ResourceTable.Media_code_33, ResourceTable.Media_code_34,
            ResourceTable.Media_code_35, ResourceTable.Media_code_36, ResourceTable.Media_code_37, ResourceTable.Media_code_38, ResourceTable.Media_code_99
    };

    private int currentPageIndex = 0;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_page_slider);
        helper = new DatabaseHelper(this);
        initPageSlider();
        initButton();
        if (weatherBeanList.size() != 0) {
            if (Objects.equals(weatherBeanList.get(0).getIsPrefer(), "true"))
                detailItems[2] = "取消收藏";
        }
        HiLog.debug(LABEL_LOG, "数据个数：weatherBeanList.size() = " + weatherBeanList.size());
        HiLog.debug(LABEL_LOG, "数据：   weatherBeanList = " + weatherBeanList.toString());
        HiLog.debug(LABEL_LOG, "页面个数：dataItems.size() = " + dataItems.size());
    }

    @Override
    protected void onResult(int requestCode, Intent resultIntent) {
        super.onResult(requestCode, resultIntent);
        if (requestCode == 100) {
            String city_pinyin = resultIntent.getStringParam("city_pinyin");
            String city_name = resultIntent.getStringParam("city_name");
            if (city_pinyin != null && city_pinyin != "") {
                addCityPage(city_name, city_pinyin, false);
                for (int i = 0; i < weatherBeanList.size(); i++) {
                    if (Objects.equals(weatherBeanList.get(i).getCityName(), city_name)) {
                        pageSlider.setCurrentPage(i);
                        break;
                    }
                }
            }
        }
    }


    private void initButton() {
        Button button_showDetail = findComponentById(ResourceTable.Id_show_detail);

        button_showDetail.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                new XPopup.Builder(getContext())
                        .atView(component)  // 依附于所点击的Component，内部会自动判断在上方或者下方显示
                        .asAttachList(detailItems,
                                new int[]{},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if (position == 0) {
                                            new XPopup.Builder(getContext()).asInputConfirm("查询城市天气", "请输入城市名称。",
                                                            new OnInputConfirmListener() {
                                                                @Override
                                                                public void onConfirm(String text) {
                                                                    Vector<Cell[]> cities = get_city_response(getContext(), text);
                                                                    if (cities.size() == 0) showNoData();
                                                                    else {
                                                                        String[] responseCityList = new String[cities.size()];
                                                                        for (int i = 0; i < cities.size(); i++) {
                                                                            responseCityList[i] = cities.get(i)[2].getContents();
                                                                        }
                                                                        new XPopup.Builder(getContext())
                                                                                .isDarkTheme(false)
                                                                                .enableDrag(true)
                                                                                .asBottomList("请选择查询的城市", responseCityList,
                                                                                        new OnSelectListener() {
                                                                                            @Override
                                                                                            public void onSelect(int position, String text) {
                                                                                                addCityPage(cities.get(position)[2].getContents(), cities.get(position)[3].getContents(), false);
                                                                                                pageSlider.setCurrentPage(weatherBeanList.size() - 1);
                                                                                            }
                                                                                        })
                                                                                .show();
                                                                    }

                                                                }
                                                            })
                                                    .show();
                                        } else if (position == 2) {
                                            if (Objects.equals(text, "收藏")) {
//                                                delete(weatherBeanList.get(currentPageIndex).getCityName());
                                                detailItems[2] = "取消收藏";
                                                weatherBeanList.get(currentPageIndex).setIsPrefer("true");
                                                insert(weatherBeanList.get(currentPageIndex));
                                            } else {
                                                detailItems[2] = "收藏";
                                                weatherBeanList.get(currentPageIndex).setIsPrefer("false");
                                                delete(weatherBeanList.get(currentPageIndex).getCityName());
                                            }
                                            changeDetailItems();
                                        } else if (position == 1) {// 查看城市列表
                                            Intent intent = new Intent();

                                            String[] cityNames = new String[weatherBeanList.size()];
                                            String[] cityPinYins = new String[weatherBeanList.size()];
                                            for (int i = 0; i < weatherBeanList.size(); i++) {
                                                if (Objects.equals(weatherBeanList.get(i).getIsPrefer(), "true")) {
                                                    cityNames[i] = weatherBeanList.get(i).getCityName();
                                                    cityPinYins[i] = weatherBeanList.get(i).getCityPinYin();
                                                }
                                            }

                                            intent.setParam("cityNames", cityNames);
                                            intent.setParam("cityPinYins", cityPinYins);

                                            presentForResult(new StickCityListSlice(), intent, 100);
                                        }
                                    }
                                })
                        .show();


            }
        });


    }


    private void showNoData() {
        new ToastDialog(getContext())
                .setText("城市输入错误或者没有该城市的数据")
                .setAlignment(LayoutAlignment.CENTER)
                .show();
    }


    private void getDailyCityWeatherData(DirectionalLayout weatherPage, String city_pinyin) {

        if (city_pinyin != null || city_pinyin.length() != 0) {
            TaskDispatcher dispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
            // 查询未来天气
            dispatcher.asyncDispatch(() -> {
                // 格式化城市天气url：添加拼音和查询天数。由于没有开会员，最多只能显示3天的天气。
                String forecastUrl = String.format(Constant.FORECAST_URL, city_pinyin, 3);
                // 获取页面响应
                String forecastResult = HttpUtil.sendGetRequest(getContext(), forecastUrl);
                HiLog.debug(LABEL_LOG, forecastUrl);
                HiLog.debug(LABEL_LOG, "forecastResult = " + forecastResult);
                // 将得到的json数据转化成map，list中的每个map是一天的所有数据
                List<Map<String, String>> forecastMap = JsonUtils.parseDailyWeatherJsonToList(forecastResult);
                // 更新应用界面
                if (forecastMap.size() != 0)
                    getUITaskDispatcher().asyncDispatch(() -> {
                //  HiLog.debug(LABEL_LOG, forecastMap.toString());
                        updateForecastUI(weatherPage, forecastMap);
                    });
            });
            dispatcher.asyncDispatch(() -> {
                String todayUrl = String.format(Constant.TODAY_URL, city_pinyin);
                HiLog.debug(LABEL_LOG, todayUrl);
                String todayResult = HttpUtil.sendGetRequest(getContext(), todayUrl);
                HiLog.debug(LABEL_LOG, "todayResult = " + todayResult);
                Map<String, String> todayMap = JsonUtils.parseCurrentWeatherJsonToMap(todayResult);
                if (todayMap.size() != 0)
                    getUITaskDispatcher().asyncDispatch(() -> {
//                        HiLog.debug(LABEL_LOG, todayMap.toString());
                        updateTodayUI(weatherPage, todayMap);
                    });
            });
            dispatcher.asyncDispatch(() -> {
                String lifeIndexUrl = String.format(Constant.LIFEINDEX_URL, city_pinyin);
                HiLog.debug(LABEL_LOG, lifeIndexUrl);
                String lifeIndexResult = HttpUtil.sendGetRequest(getContext(), lifeIndexUrl);
                HiLog.debug(LABEL_LOG, "lifeIndexResult = " + lifeIndexResult);
//                获取数据完成
                Map<String, String> lifeIndexMap = JsonUtils.parseLifeSuggestionJsonToMap(lifeIndexResult);
                if (lifeIndexMap.size() != 0)
                    getUITaskDispatcher().asyncDispatch(() -> {
//                        HiLog.debug(LABEL_LOG, lifeIndexMap.toString());
                        updateLifeIndexUI(weatherPage, lifeIndexMap);
                    });
            });
        }
    }

    private void updateLifeIndexUI(DirectionalLayout weatherPage, Map<String, String> lifeIndexMap) {


        Text today_car_washing = weatherPage.findComponentById(ResourceTable.Id_today_car_washing);// 洗车建议
        Text today_dressing = weatherPage.findComponentById(ResourceTable.Id_today_dressing); // 穿着建议
        Text today_flu = weatherPage.findComponentById(ResourceTable.Id_today_flu);// 感冒
        Text today_sport = weatherPage.findComponentById(ResourceTable.Id_today_sport); // 运动建议
        Text today_travel = weatherPage.findComponentById(ResourceTable.Id_today_travel);// 旅游建议
        Text today_uv = weatherPage.findComponentById(ResourceTable.Id_today_uv);// 紫外线强度

        today_car_washing.setText(lifeIndexMap.get("car_washing"));
        today_dressing.setText(lifeIndexMap.get("dressing"));
        today_flu.setText(lifeIndexMap.get("flu"));
        today_sport.setText(lifeIndexMap.get("sport"));
        today_travel.setText(lifeIndexMap.get("travel"));
        today_uv.setText(lifeIndexMap.get("uv"));
    }

    private void updateTodayUI(DirectionalLayout weatherPage, Map<String, String> todayMap) {
        Text today_temperature = weatherPage.findComponentById(ResourceTable.Id_today_digital); // 当前温度
        TextField city = weatherPage.findComponentById(ResourceTable.Id_city); // 城市名称: 重庆

        today_temperature.setText(todayMap.get("temperature") + "℃");
        String[] temp = new String[]{todayMap.get("path").substring(todayMap.get("path").indexOf(",") + 1)};
        city.setText(temp[0]);
        String path = todayMap.get("path");

        int index = path.indexOf(",", path.indexOf(",") + 1);
        String output = path.substring(0, index);
        String path1 = output.substring(0, output.indexOf(","));
        String path2 = output.substring(output.indexOf(",") + 1, index);
        if (path1.equals(path2))
            city.setText(temp[0]);
        else
            city.setText(output);
    }

    private void updateForecastUI(DirectionalLayout weatherPage, List<Map<String, String>> weatherList) {
        LayoutScatter layoutScatter = LayoutScatter.getInstance(getContext());

        Text today_detail = weatherPage.findComponentById(ResourceTable.Id_today_detail_date);// 详细天气信息: 2022-11-25,星期五 6℃~11℃
        Image today_now_icon = weatherPage.findComponentById(ResourceTable.Id_today_icon);// 当前天气图标
        Text today_humidity = weatherPage.findComponentById(ResourceTable.Id_today_humidity); // 空气湿度
        Text today_wind_scale = weatherPage.findComponentById(ResourceTable.Id_today_wind_scale);// 风速
        Text today_wind_direction = weatherPage.findComponentById(ResourceTable.Id_today_wind_direction);  // 风向
        Text today_rain_precip = weatherPage.findComponentById(ResourceTable.Id_today_rain_precip);// 降雨概率
        Text today_rainfall = weatherPage.findComponentById(ResourceTable.Id_today_rainfall);// 降雨量

        DirectionalLayout container = weatherPage.findComponentById(ResourceTable.Id_directionLayout_itemList); // 未来天气

        // 白天图标
        if (LocalDateTime.now().getHour() >= 6 && LocalDateTime.now().getHour() < 18) {
            today_detail.setText(String.format("%s-%s-%s  %s℃~%s℃  %s", LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(), weatherList.get(0).get("low"), weatherList.get(0).get("high"), weatherList.get(0).get("text_day")));
            today_now_icon.setPixelMap(getIconByCode(weatherList.get(0).get("code_day")));
        } else {// 夜晚图标
            today_detail.setText(String.format("%s-%s-%s  %s℃~%s℃  %s", LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(), weatherList.get(0).get("low"), weatherList.get(0).get("high"), weatherList.get(0).get("text_night")));
            today_now_icon.setPixelMap(getIconByCode(weatherList.get(0).get("code_night")));
        }
        for (int i = 0; i < 3; i++) {
            DirectionalLayout dailyItem = (DirectionalLayout) layoutScatter.parse(ResourceTable.Layout_daily_weather_item, null, false);

            Image dailyImage = dailyItem.findComponentById(ResourceTable.Id_daily_icon);
            Text dailyWeather = dailyItem.findComponentById(ResourceTable.Id_daily_weather);
            Text daily_rain_potential = dailyItem.findComponentById(ResourceTable.Id_daily_rain_potential);
            Text daily_temp = dailyItem.findComponentById(ResourceTable.Id_daily_temp);
            Text daily_date = dailyItem.findComponentById(ResourceTable.Id_daily_date);

            if (LocalDateTime.now().getHour() >= 6 && LocalDateTime.now().getHour() < 18) {
                dailyImage.setPixelMap(getIconByCode(weatherList.get(i).get("code_day")));
                dailyWeather.setText(weatherList.get(i).get("text_day"));
            } else {
                dailyImage.setPixelMap(getIconByCode(weatherList.get(i).get("code_night")));
                dailyWeather.setText(weatherList.get(i).get("text_night"));
            }
            daily_date.setText(dates[i]);
            daily_rain_potential.setText(String.format("%.0f%%", Float.parseFloat(weatherList.get(i).get("precip")) * 100));
            daily_temp.setText(String.format("%s℃/%s℃", weatherList.get(i).get("high"), weatherList.get(i).get("low")));

            container.addComponent(dailyItem);
        }


        today_humidity.setText(weatherList.get(0).get("humidity") + "%");
        today_wind_scale.setText(weatherList.get(0).get("wind_scale"));
        today_wind_direction.setText(weatherList.get(0).get("wind_direction"));
        today_rain_precip.setText(String.format("%.0f%%", Float.parseFloat(weatherList.get(0).get("precip")) * 100));
        today_rainfall.setText(weatherList.get(0).get("rainfall"));

    }

    public static int getIconByCode(String code_day) {
        int code = Integer.parseInt(code_day);
        if (code == 99) return resources[resources.length - 1];
        else return resources[code];
    }


    // 插入到数据库
    private void insert(RecordBean bean) {

        OrmContext ormContext = helper.getOrmContext(Constant.DB_ALIAS, Constant.DB_NAME, RecordDbStore.class);
        bean.setId((int) System.currentTimeMillis());
        if (ormContext.insert(bean)) {
            ormContext.flush();
            ormContext.close();

            new ToastDialog(getContext())
                    .setText("收藏成功")
                    .setSize(800, 300)
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();
            HiLog.debug(LABEL_LOG, "in insert: 插入数据成功: %{public}s", bean.toString());
        } else {
            HiLog.debug(LABEL_LOG, "in insert: 插入数据失败");
        }
    }


    // 数据库查询
    private List<RecordBean> query(String cityName) {
        OrmContext ormContext = helper.getOrmContext(Constant.DB_ALIAS, Constant.DB_NAME, RecordDbStore.class);
        OrmPredicates query = ormContext.where(RecordBean.class);
        if (!Objects.equals(cityName, ""))
            query = query.equalTo("cityName", cityName);

        List<RecordBean> beanList = ormContext.query(query);
        ormContext.flush();
        ormContext.close();
        if (beanList.size() == 0) {
            HiLog.debug(LABEL_LOG, "数据库无数据");
            return new ArrayList<>();
        } else {
            HiLog.debug(LABEL_LOG, "数据库数据条数：%{public}d", beanList.size());
        }
        return beanList;
    }

    // 数据库查询
    private void delete(String cityName) {
        OrmContext ormContext = helper.getOrmContext(Constant.DB_ALIAS, Constant.DB_NAME, RecordDbStore.class);
        OrmPredicates query = ormContext.where(RecordBean.class);

        if (!Objects.equals(cityName, ""))
            query = query.equalTo("cityName", cityName);

        ormContext.delete(query);
        new ToastDialog(getContext())
                .setText("取消收藏成功")
                .setSize(800, 300)
                .setAlignment(LayoutAlignment.CENTER)
                .show();
        ormContext.flush();
        ormContext.close();
        HiLog.debug(LABEL_LOG, "in delete: 取消收藏成功");

    }

    private void initPageSlider() {
        pageSlider = findComponentById(ResourceTable.Id_page_slider);
        pageSlider.setReboundEffect(true);

        dataItems = new ArrayList<>();
        // 获取数据库的收藏城市
        weatherBeanList = new ArrayList<>();
        weatherBeanList.addAll(query(""));

        if (weatherBeanList.size() == 0) {
            addCityPage("成都", "chengdu", false);
            addCityPage("重庆", "chongqing", false);
            addCityPage("北京", "beijing", false);
        } else {
            for (RecordBean bean : weatherBeanList) {
                addCityPage(bean.getCityName(), bean.getCityPinYin(), true);
            }
        }

        HiLog.debug(LABEL_LOG, "初始化： weatherBeanList.size() = " + weatherBeanList.size());
        HiLog.debug(LABEL_LOG, "初始化： dataItems.size() = " + dataItems.size());


        pageSlider.setProvider(new WeatherPageProvider(dataItems, this));

        pageSlider.addPageChangedListener(new PageSlider.PageChangedListener() {
            @Override
            public void onPageSliding(int itemPos, float itemPosOffset, int itemPosPixles) {
            }

            @Override
            public void onPageSlideStateChanged(int state) {
            }

            @Override
            public void onPageChosen(int itemPos) {
                currentPageIndex = itemPos;
                changeDetailItems();
            }
        });

    }

    private void changeDetailItems() {

        if (Objects.equals(weatherBeanList.get(currentPageIndex).getIsPrefer(), "true")) {
            detailItems[2] = "取消收藏";
        } else {
            detailItems[2] = "收藏";
        }
        HiLog.debug(LABEL_LOG, "当前页面：%{public}d, 城市：%{public}s", currentPageIndex, weatherBeanList.get(currentPageIndex).getCityName());

    }


    private void addCityPage(String cityName, String cityPinYin, boolean isFromDB) {
        if (!isFromDB)
            weatherBeanList.add(new RecordBean(cityName, cityPinYin));
        LayoutScatter layoutScatter = LayoutScatter.getInstance(getContext());
        DirectionalLayout weatherPage = (DirectionalLayout) layoutScatter.parse(ResourceTable.Layout_weather_main_page, null, false);
        getDailyCityWeatherData(weatherPage, cityPinYin);
        dataItems.add(weatherPage);

        pageSlider.setProvider(new WeatherPageProvider(dataItems, this));
    }

}
