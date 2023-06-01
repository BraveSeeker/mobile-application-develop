package com.example.weatherforcast.constant;

/**
 * 常量
 */
public class Constant {
    String API_KEY = "your api key";
    // 今日天气
    public static final String TODAY_URL = String.format("https://api.seniverse.com/v3/weather/now.json?key={}&location=%s&language=zh-Hans&unit=c",API_KEY};

    // 未来天气
    public static final String FORECAST_URL = String.format("https://api.seniverse.com/v3/weather/daily.json?key={}&location=%s&language=zh-Hans&unit=c&start=0&days=%d",API_KEY};

    // 生活指数
    public static final String LIFEINDEX_URL = String.format("https://api.seniverse.com/v3/life/suggestion.json?key={}&location=%s&language=zh-Hans",API_KEY};

    // 数据库
    public static final String DB_ALIAS = "RecordBean";

    // 本地数据库文件名
    public static final String DB_NAME = "RecordBean.db";


}
