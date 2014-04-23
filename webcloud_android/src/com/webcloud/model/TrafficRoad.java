package com.webcloud.model;

import java.io.Serializable;

public class TrafficRoad implements Serializable {
    @Override
    public String toString() {
        return "" + road + ": " + trafficInfo + " ";
    }
    public String getRoad() {
        return road;
    }
    public void setRoad(String road) {
        this.road = road;
    }
    public String getTrafficInfo() {
        return trafficInfo;
    }
    public void setTrafficInfo(String trafficInfo) {
        this.trafficInfo = trafficInfo;
    }
    /***/
    private static final long serialVersionUID = -2400880210738098450L;
    String road;
    String trafficInfo;
    public TrafficRoad(String road, String trafficInfo) {
        super();
        this.road = road;
        this.trafficInfo = trafficInfo;
    }
    public TrafficRoad() {
        super();
        // TODO Auto-generated constructor stub
    }
}