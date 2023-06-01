package com.example.weatherforcast.constant;

/**
 * 常量
 */
public class Constant {

    // 今日天气
    public static final String TODAY_URL = "https://api.seniverse.com/v3/weather/now.json?key=SM1jkVpoja_DPR9gF&location=%s&language=zh-Hans&unit=c";

    // 未来天气
    public static final String FORECAST_URL = "https://api.seniverse.com/v3/weather/daily.json?key=SM1jkVpoja_DPR9gF&location=%s&language=zh-Hans&unit=c&start=0&days=%d";

    // 生活指数
    public static final String LIFEINDEX_URL = "https://api.seniverse.com/v3/life/suggestion.json?key=SM1jkVpoja_DPR9gF&location=%s&language=zh-Hans";

    // 数据库
    public static final String DB_ALIAS = "RecordBean";

    // 本地数据库文件名
    public static final String DB_NAME = "RecordBean.db";


}
