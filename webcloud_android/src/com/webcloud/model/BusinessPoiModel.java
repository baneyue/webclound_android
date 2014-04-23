package com.webcloud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 商业poi信息。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-11-29]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class BusinessPoiModel implements Parcelable {
    public String getPanotype() {
        return panotype;
    }
    public void setPanotype(String panotype) {
        this.panotype = panotype;
    }
    public String getPanoid() {
        return panoid;
    }
    public void setPanoid(String panoid) {
        this.panoid = panoid;
    }
    public String getHeading() {
        return heading;
    }
    public void setHeading(String heading) {
        this.heading = heading;
    }
    public String getPitch() {
        return pitch;
    }
    public void setPitch(String pitch) {
        this.pitch = pitch;
    }
    public String getZoom() {
        return zoom;
    }
    public void setZoom(String zoom) {
        this.zoom = zoom;
    }
    public String getBestpaymark() {
        return bestpaymark;
    }
    public void setBestpaymark(String bestpaymark) {
        this.bestpaymark = bestpaymark;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((shop_address == null) ? 0 : shop_address.hashCode());
        result = prime * result + ((shop_range == null) ? 0 : shop_range.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof BusinessPoiModel))
            return false;
        BusinessPoiModel other = (BusinessPoiModel)obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "BusinessPoiModel [id=" + id + ", name=" + name + ", c_file_url=" + c_file_url + ", shop_address="
            + shop_address + ", c_Lat=" + c_Lat + ", c_Long=" + c_Long + ", c_contact_info=" + c_contact_info
            + ", busihour=" + busihour + ", c_class_type=" + c_class_type + ", shop_range=" + shop_range + "]";
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getC_file_url() {
        return c_file_url;
    }
    public void setC_file_url(String c_file_url) {
        this.c_file_url = c_file_url;
    }
    public String getShop_address() {
        return shop_address;
    }
    public void setShop_address(String shop_address) {
        this.shop_address = shop_address;
    }
    public String getC_Lat() {
        return c_Lat;
    }
    public void setC_Lat(String c_Lat) {
        this.c_Lat = c_Lat;
    }
    public String getC_Long() {
        return c_Long;
    }
    public void setC_Long(String c_Long) {
        this.c_Long = c_Long;
    }
    public String getC_contact_info() {
        return c_contact_info;
    }
    public void setC_contact_info(String c_contact_info) {
        this.c_contact_info = c_contact_info;
    }
    public String getBusihour() {
        return busihour;
    }
    public void setBusihour(String busihour) {
        this.busihour = busihour;
    }
    public String getC_class_type() {
        return c_class_type;
    }
    public void setC_class_type(String c_class_type) {
        this.c_class_type = c_class_type;
    }
    public String getShop_range() {
        return shop_range;
    }
    public void setShop_range(String shop_range) {
        this.shop_range = shop_range;
    }
    public String id;
    public String name;
    
    public String c_file_url;
    
    public String shop_address;
    
    public String c_Lat;
    
    public String c_Long;
    public String c_contact_info;
    public String busihour;
    public String c_class_type;
    public String shop_range;
    public String panotype;
    public String panoid;
    public String heading;
    public String pitch;
    public String zoom;
    public String bestpaymark;
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    //两个活动之间传递ArrayList型非字符串型集合数据时，必须重写下面的方法
    public static final Parcelable.Creator<BusinessPoiModel> CREATOR = new Creator<BusinessPoiModel>() {
        
        @Override
        public BusinessPoiModel createFromParcel(Parcel source) {
            BusinessPoiModel vo = new BusinessPoiModel();
            vo.busihour = source.readString();
            vo.c_class_type = source.readString();
            vo.c_contact_info = source.readString();
            vo.c_file_url = source.readString();
            vo.c_Lat = source.readString();
            vo.c_Long = source.readString();
            vo.id = source.readString();
            vo.name = source.readString();
            vo.shop_address = source.readString();
            vo.shop_range = source.readString();
            vo.panotype = source.readString();
            vo.panoid = source.readString();
            vo.bestpaymark = source.readString();
            vo.heading = source.readString();
            vo.pitch = source.readString();
            vo.zoom = source.readString();
            return vo;
        }
        
        @Override
        public BusinessPoiModel[] newArray(int size) {
            return new BusinessPoiModel[size];
        }
        
    };
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(busihour);
        dest.writeString(c_class_type);
        dest.writeString(c_contact_info);
        dest.writeString(c_file_url);
        dest.writeString(c_Lat);
        dest.writeString(c_Long);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(shop_address);
        dest.writeString(shop_range);
        dest.writeString(panotype);
        dest.writeString(panoid);
        dest.writeString(bestpaymark);
        dest.writeString(heading);
        dest.writeString(pitch);
        dest.writeString(zoom);
    }
    public BusinessPoiModel() {
        super();
    }
    public BusinessPoiModel(String id, String name, String c_file_url, String shop_address, String c_Lat,
        String c_Long, String c_contact_info, String busihour, String c_class_type, String shop_range, String panotype,
        String panoid, String heading, String pitch, String zoom, String bestpaymark) {
        super();
        this.id = id;
        this.name = name;
        this.c_file_url = c_file_url;
        this.shop_address = shop_address;
        this.c_Lat = c_Lat;
        this.c_Long = c_Long;
        this.c_contact_info = c_contact_info;
        this.busihour = busihour;
        this.c_class_type = c_class_type;
        this.shop_range = shop_range;
        this.panotype = panotype;
        this.panoid = panoid;
        this.heading = heading;
        this.pitch = pitch;
        this.zoom = zoom;
        this.bestpaymark = bestpaymark;
    }
    
    
}
