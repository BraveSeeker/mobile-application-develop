package com.example.weatherforcast.slice;

import com.example.weatherforcast.ResourceTable;
import com.example.weatherforcast.constant.Constant;
import com.example.weatherforcast.utils.HttpUtil;
import com.example.weatherforcast.utils.JsonUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;

import java.util.List;
import java.util.Map;

import static com.example.weatherforcast.slice.MainAbilitySlice.LABEL_LOG;
import static com.example.weatherforcast.slice.MainAbilitySlice.getIconByCode;

public class StickCityListSlice extends AbilitySlice {
    private Button goBack;
    private String[] cityNames;
    private String[] cityPinYins;
    private DirectionalLayout stickCityListLayout;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_stickcitylist);
        cityNames = intent.getStringArrayParam("cityNames");
        cityPinYins = intent.getStringArrayParam("cityPinYins");
        HiLog.debug(LABEL_LOG, "in StickCityListSlice: cityNames.size() = %{public}d, cityNames = %{public}s, cityPinYin = %{public}s", cityNames.length, cityNames, cityPinYins);
        initComponent();
        initUI();
    }

    private void initComponent() {
        goBack = findComponentById(ResourceTable.Id_stick_goBack);
        stickCityListLayout = findComponentById(ResourceTable.Id_stickCityList_layout);
        stickCityListLayout.removeAllComponents();
        goBack.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Intent intent1 = new Intent();
                intent1.setParam("city_pinyin", "");
                intent1.setParam("city_name", "");
                setResult(intent1);
                terminate();
            }
        });

    }

    private void initUI() {
        TaskDispatcher dispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
        LayoutScatter layoutScatter = LayoutScatter.getInstance(this.getContext());
        Text noCity = findComponentById(ResourceTable.Id_stick_noCity);
        for (int i = 0; i < cityPinYins.length; i++) {
            noCity.setVisibility(Component.VISIBLE);
            if (cityPinYins[i] != null) {
                noCity.setVisibility(Component.INVISIBLE);
            } else continue;
            int finalI = i;
            DirectionalLayout dailyItem = (DirectionalLayout) layoutScatter.parse(ResourceTable.Layout_city_list_item, null, false);
            stickCityListLayout.addComponent(dailyItem);
            dailyItem.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    Intent intent1 = new Intent();
                    intent1.setParam("city_pinyin", cityPinYins[finalI]);
                    intent1.setParam("city_name", cityNames[finalI]);
                    setResult(intent1);
//                // 关闭页面，返回上一页
                    terminate();
                }

            });
            // 查询未来天气

            dispatcher.asyncDispatch(() -> {
                String forecastUrl = String.format(Constant.FORECAST_URL, cityPinYins[finalI], 5);
                String forecastResult = HttpUtil.sendGetRequest(getContext(), forecastUrl);
                List<Map<String, String>> forecastMap = JsonUtils.parseDailyWeatherJsonToList(forecastResult);
                if (forecastMap.size() != 0)
                    getUITaskDispatcher().asyncDispatch(() -> {
                        HiLog.debug(LABEL_LOG, "In stickList: forecastMap = " + forecastMap.toString());
                        updateCityListUI(dailyItem, forecastMap.get(0));
                    });
                else HiLog.debug(LABEL_LOG, "In stickList: search failed");
            });

            dispatcher.asyncDispatch(() -> {
                String todayUrl = String.format(Constant.TODAY_URL, cityPinYins[finalI]);
                String todayResult = HttpUtil.sendGetRequest(getContext(), todayUrl);
                Map<String, String> todayMap = JsonUtils.parseCurrentWeatherJsonToMap(todayResult);
                if (todayMap.size() != 0)
                    getUITaskDispatcher().asyncDispatch(() -> {
                        updateTodayUI(dailyItem, todayMap);
                    });
            });

            HiLog.debug(LABEL_LOG, "城市列表：添加第 %{public}d 个页面成功", finalI);
        }

    }

    private void updateTodayUI(DirectionalLayout dailyItem, Map<String, String> map) {
        Text item_city = dailyItem.findComponentById(ResourceTable.Id_cityList_city);
        Text nowTemp = dailyItem.findComponentById(ResourceTable.Id_cityList_nowTemp);
        Image item_image = dailyItem.findComponentById(ResourceTable.Id_cityList_icon);

        nowTemp.setText(map.get("temperature") + "℃");
        item_city.setText(map.get("name"));
        item_image.setPixelMap(getIconByCode(map.get("code")));
    }

    private void updateCityListUI(DirectionalLayout dailyItem, Map<String, String> map) {
        Text item_tempRange = dailyItem.findComponentById(ResourceTable.Id_cityList_tempRange);

        item_tempRange.setText(String.format(" %s℃~%s℃", map.get("low"), map.get("high")));

    }


}
