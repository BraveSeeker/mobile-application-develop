package com.example.weatherforcast.model;


import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.PrimaryKey;


@Entity(tableName = "record")
public class RecordBean extends OrmObject {

    @PrimaryKey(autoGenerate = true)
    int id;
    String cityName;
    String cityPinYin;
    String isPrefer;

    public RecordBean() {
        this.isPrefer = "false";
    }

    public RecordBean(String cityName, String cityPinYin) {
        this.cityName = cityName;
        this.cityPinYin = cityPinYin;
        this.isPrefer = "false";
    }


    @Override
    public String toString() {
        return "RecordBean{" +
                "id=" + id +
                ", cityName='" + cityName + '\'' +
                ", cityPinYin='" + cityPinYin + '\'' +
                ", isPrefer='" + isPrefer + '\'' +
                '}';
    }

    public String getIsPrefer() {
        return isPrefer;
    }

    public void setIsPrefer(String isPrefer) {
        this.isPrefer = isPrefer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityPinYin() {
        return cityPinYin;
    }

    public void setCityPinYin(String cityPinYin) {
        this.cityPinYin = cityPinYin;
    }
}
