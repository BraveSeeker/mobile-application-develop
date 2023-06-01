package com.example.weatherforcast.utils;//package weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析json
 */
public class JsonUtils {

    /**
     * 解析当前的天气预报-->Map集合
     *
     * @param json
     * @return
     */
    public static Map<String, String> parseCurrentWeatherJsonToMap(String json) {


        Map<String, String> map = new HashMap<>();
        if (json == null || "".equals(json)) {
            return map;
        }
        try {
            JSONObject jSONObject = new JSONObject(json);

            JSONObject result = jSONObject.getJSONArray("results").getJSONObject(0);

            JSONObject location = result.getJSONObject("location");
            JSONObject now = result.getJSONObject("now");

            map.put("name", location.optString("name")); // 城市
            map.put("path", location.optString("path")); // 城市完整地址
            map.put("country", location.optString("country")); // 国家

            map.put("text", now.optString("text")); // 天气
            map.put("code", now.optString("code")); // 天气图标代码
            map.put("temperature", now.optString("temperature")); // 温度

            map.put("last_update", result.optString("last_update")); // 更新时间
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 解析未来3天
     *
     * @param json
     * @return
     */
    public static List<Map<String, String>> parseDailyWeatherJsonToList(String json) {
        List<Map<String, String>> list = new ArrayList<>();
        if (json == null || "".equals(json)) {
            return list;
        }
        try {
            JSONObject result = new JSONObject(json).getJSONArray("results").getJSONObject(0);
            JSONArray daily = result.getJSONArray("daily");
            for (int i = 0; i < daily.length(); i++) {
                JSONObject dailyData = daily.getJSONObject(i);
                Map<String, String> map = new HashMap<>();

                map.put("date", dailyData.optString("date"));
                map.put("code_day", dailyData.optString("code_day"));
                map.put("rainfall", dailyData.optString("rainfall"));
                map.put("text_night", dailyData.optString("text_night"));
                map.put("wind_direction", dailyData.optString("wind_direction"));
                map.put("high", dailyData.optString("high"));
                map.put("precip", dailyData.optString("precip"));
                map.put("low", dailyData.optString("low"));
                map.put("wind_scale", dailyData.optString("wind_scale"));
                map.put("text_day", dailyData.optString("text_day"));
                map.put("wind_direction_degree", dailyData.optString("wind_direction_degree"));
                map.put("humidity", dailyData.optString("humidity"));
                map.put("wind_speed", dailyData.optString("wind_speed"));
                map.put("code_night", dailyData.optString("code_night"));

                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 解析当前的生活指数-->Map集合
     *
     * @param json
     * @return
     */
    public static Map<String, String> parseLifeSuggestionJsonToMap(String json) {

        Map<String, String> map = new HashMap<>();
        if (json == null || "".equals(json)) {
            return map;
        }
        try {
            JSONObject jSONObject = new JSONObject(json);

            JSONObject result = jSONObject.getJSONArray("results").getJSONObject(0);
            JSONObject suggestion = result.getJSONObject("suggestion");

            JSONObject car_washing = suggestion.getJSONObject("car_washing");
            JSONObject dressing = suggestion.getJSONObject("dressing");
            JSONObject flu = suggestion.getJSONObject("flu");
            JSONObject sport = suggestion.getJSONObject("sport");
            JSONObject travel = suggestion.getJSONObject("travel");
            JSONObject uv = suggestion.getJSONObject("uv");

            map.put("car_washing", car_washing.optString("brief")); // 洗车
            map.put("dressing", dressing.optString("brief")); // 穿着
            map.put("flu", flu.optString("brief")); // 流感
            map.put("sport", sport.optString("brief")); // 运动
            map.put("travel", travel.optString("brief")); // 旅行
            map.put("uv", uv.optString("brief")); // 紫外线


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }


    public static void main(String[] args) {
        String current_weather = "{\"results\":[{\"location\":{\"id\":\"WM7B0X53DZW2\",\"name\":\"重庆\",\"country\":\"CN\",\"path\":\"重庆,重庆,中国\",\"timezone\":\"Asia/Shanghai\",\"timezone_offset\":\"+08:00\"},\"now\":{\"text\":\"阴\",\"code\":\"9\",\"temperature\":\"12\"},\"last_update\":\"2022-11-29T23:32:53+08:00\"}]}\n";
        System.out.println(current_weather);
        Map<String, String> ans = parseCurrentWeatherJsonToMap(current_weather);
        System.out.println(ans);

    }
}

