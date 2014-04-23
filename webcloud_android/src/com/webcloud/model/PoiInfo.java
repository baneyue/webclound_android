package com.webcloud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.core.LatLonPoint;

public class PoiInfo implements Parcelable {
    
    public String getPanoid() {
        return panoid;
    }
    
    public void setPanoid(String panoid) {
        this.panoid = panoid;
    }
    
    public float getHeading() {
        return heading;
    }
    
    public void setHeading(float heading) {
        this.heading = heading;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public Object getObject() {
        return object;
    }
    
    public void setObject(Object object) {
        this.object = object;
    }
    
    public int type;//poi类型
    
    public Object object;//其他数据信息
    
    public PoiInfo() {
        super();
    }
    
    public PoiInfo(LatLonPoint point, String name, String address, String iconUrl) {
        super();
        this.point = point;
        this.name = name;
        this.address = address;
        this.iconUrl = iconUrl;
    }
    
    public LatLonPoint getPoint() {
        return point;
    }
    
    public void setPoint(LatLonPoint point) {
        this.point = point;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public LatLonPoint point;
    
    public String name;
    
    public String address;
    
    public String iconUrl;
    
    public String panoid;
    
    public float heading;
    
    public float pitch;
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    //两个活动之间传递ArrayList型非字符串型集合数据时，必须重写下面的方法
    public static final Parcelable.Creator<PoiInfo> CREATOR = new Creator<PoiInfo>() {
        
        @Override
        public PoiInfo createFromParcel(Parcel source) {
            PoiInfo vo = new PoiInfo();
            vo.type = source.readInt();
            vo.name = source.readString();
            vo.address = source.readString();
            vo.iconUrl = source.readString();
            //vo.point = source.readParcelable(Config.class.getClassLoader());
            vo.point = new LatLonPoint(source.readDouble(), source.readDouble());
            vo.panoid = source.readString();
            vo.heading = source.readFloat();
            vo.pitch = source.readFloat();
            /*try {
                String obj = source.readString();
                if (!TextUtils.isEmpty(obj)) {
                    
                    JsonFriend<BusinessPoiModel> bp = new JsonFriend<BusinessPoiModel>(BusinessPoiModel.class);
                    BusinessPoiModel bpm = bp.parseObject(obj);
                    if (bpm != null) {
                        vo.object = bpm;
                    } else {
                        JsonFriend<LocatObjsVo> lo = new JsonFriend<LocatObjsVo>(LocatObjsVo.class);
                        LocatObjsVo lom = lo.parseObject(obj);
                        if (lom != null) {
                            vo.object = lom;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            return vo;
        }
        
        @Override
        public PoiInfo[] newArray(int size) {
            return new PoiInfo[size];
        }
        
    };
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(iconUrl);
        //dest.writeParcelable(point,flags);
        dest.writeDouble(point != null ? point.getLatitude() : 0);
        dest.writeDouble(point != null ? point.getLongitude() : 0);
        dest.writeString(panoid);
        dest.writeFloat(heading);
        dest.writeFloat(pitch);
        dest.writeString(JSON.toJSONString(object));
    }
}
