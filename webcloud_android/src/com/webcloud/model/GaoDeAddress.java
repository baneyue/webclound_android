package com.webcloud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 高德地址信息。
 * 这个地址bean中的address是：省+城市名+区名+poiname，如：安徽省合肥市朝阳区财富广场
 * 城市名包含市如：北京市
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-3]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class GaoDeAddress implements Parcelable{
    @Override
    public String toString() {
        return "GaoDeAddress [latitude=" + latitude + ", longtitude=" + longtitude + ", cityname=" + cityname
            + ", province=" + province + ", distinct=" + distinct + ", street=" + street + ", township=" + township
            + ", neighborhood=" + neighborhood + ", building=" + building + ", adcode=" + adcode + ", address="
            + address + ", poiname=" + poiname + "]";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistinct() {
        return distinct;
    }

    public void setDistinct(String distinct) {
        this.distinct = distinct;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPoiname() {
        return poiname;
    }

    public void setPoiname(String poiname) {
        this.poiname = poiname;
    }

    private double latitude;
    private double longtitude;
    private String cityname;
    private String province;//省
    private String distinct;//区
    private String street;//街道
    private String township;//乡镇
    private String neighborhood;//社区
    private String building;//建筑物名称
    private String adcode;//区域编码
    private String address;
    private String poiname;
    public GaoDeAddress() {
        super();
    }
    
    
    
    public GaoDeAddress(String cityname, String address, String poiname, double latitude, double longtitude) {
        super();
        this.cityname = cityname;
        this.address = address;
        this.poiname = poiname;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    
    //两个活动之间传递ArrayList型非字符串型集合数据时，必须重写下面的方法
    public static final Parcelable.Creator<GaoDeAddress> CREATOR = new Creator<GaoDeAddress>() {
        
        @Override
        public GaoDeAddress createFromParcel(Parcel source) {
            GaoDeAddress vo = new GaoDeAddress();
            vo.adcode = source.readString();
            vo.address = source.readString();
            vo.building = source.readString();
            vo.cityname = source.readString();
            vo.distinct = source.readString();
            vo.neighborhood = source.readString();
            vo.poiname = source.readString();
            vo.province = source.readString();
            vo.street = source.readString();
            vo.township = source.readString();
            vo.latitude = source.readDouble();
            vo.longtitude = source.readDouble();
            return vo;
        }
        
        @Override
        public GaoDeAddress[] newArray(int size) {
            return new GaoDeAddress[size];
        }
        
    };
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(adcode);
        dest.writeString(address);
        dest.writeString(building);
        dest.writeString(cityname);
        dest.writeString(distinct);
        dest.writeString(neighborhood);
        dest.writeString(poiname);
        dest.writeString(province);
        dest.writeString(street);
        dest.writeString(township);
        dest.writeDouble(latitude);
        dest.writeDouble(longtitude);
    }
    
    
}
