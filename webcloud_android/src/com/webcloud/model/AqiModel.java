package com.webcloud.model;

import java.io.Serializable;

/**
 * 空气质量指数。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-11-13]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class AqiModel implements Serializable {

    
    @Override
    public String toString() {
        return "AqiModel [aqi=" + aqi + ", area=" + area + ", co=" + co + ", co_24h=" + co_24h + ", no2=" + no2
            + ", no2_24h=" + no2_24h + ", o3=" + o3 + ", o3_24h=" + o3_24h + ", o3_8h=" + o3_8h + ", o3_8h_24h="
            + o3_8h_24h + ", pm10=" + pm10 + ", pm10_24h=" + pm10_24h + ", pm2_5=" + pm2_5 + ", pm2_5_24h=" + pm2_5_24h
            + ", quality=" + quality + ", so2=" + so2 + ", so2_24h=" + so2_24h + ", time_point=" + time_point + "]";
    }
    public int getAqi() {
        return aqi;
    }
    public void setAqi(int aqi) {
        this.aqi = aqi;
    }
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    public float getCo() {
        return co;
    }
    public void setCo(float co) {
        this.co = co;
    }
    public float getCo_24h() {
        return co_24h;
    }
    public void setCo_24h(float co_24h) {
        this.co_24h = co_24h;
    }
    public int getNo2() {
        return no2;
    }
    public void setNo2(int no2) {
        this.no2 = no2;
    }
    public int getNo2_24h() {
        return no2_24h;
    }
    public void setNo2_24h(int no2_24h) {
        this.no2_24h = no2_24h;
    }
    public int getO3() {
        return o3;
    }
    public void setO3(int o3) {
        this.o3 = o3;
    }
    public int getO3_24h() {
        return o3_24h;
    }
    public void setO3_24h(int o3_24h) {
        this.o3_24h = o3_24h;
    }
    public int getO3_8h() {
        return o3_8h;
    }
    public void setO3_8h(int o3_8h) {
        this.o3_8h = o3_8h;
    }
    public int getO3_8h_24h() {
        return o3_8h_24h;
    }
    public void setO3_8h_24h(int o3_8h_24h) {
        this.o3_8h_24h = o3_8h_24h;
    }
    public int getPm10() {
        return pm10;
    }
    public void setPm10(int pm10) {
        this.pm10 = pm10;
    }
    public int getPm10_24h() {
        return pm10_24h;
    }
    public void setPm10_24h(int pm10_24h) {
        this.pm10_24h = pm10_24h;
    }
    public int getPm2_5() {
        return pm2_5;
    }
    public void setPm2_5(int pm2_5) {
        this.pm2_5 = pm2_5;
    }
    public int getPm2_5_24h() {
        return pm2_5_24h;
    }
    public void setPm2_5_24h(int pm2_5_24h) {
        this.pm2_5_24h = pm2_5_24h;
    }
    public String getQuality() {
        return quality;
    }
    public void setQuality(String quality) {
        this.quality = quality;
    }
    public int getSo2() {
        return so2;
    }
    public void setSo2(int so2) {
        this.so2 = so2;
    }
    public int getSo2_24h() {
        return so2_24h;
    }
    public void setSo2_24h(int so2_24h) {
        this.so2_24h = so2_24h;
    }
    public String getTime_point() {
        return time_point;
    }
    public void setTime_point(String time_point) {
        this.time_point = time_point;
    }
    /***/
    private static final long serialVersionUID = 4491896664430684702L;

    int aqi;//: 95,
    String area;//: "合肥",
    float co;//: 0.871,
    float co_24h;//: 0.872,
    int no2;//: 44,
    int no2_24h;//: 33,
    int o3;//: 30,
    int o3_24h;//: 43,
    int o3_8h;//: 31,
    int o3_8h_24h;//: 35,
    int pm10;//: 96,
    int pm10_24h;//: 94,
    int pm2_5;//: 61,
    int pm2_5_24h;//: 71,
    String quality;//: "良",
    int so2;//: 25,
    int so2_24h;//: 22,
    String time_point;//: "2013-11-13T17:00:00Z"
    
}
