package com.webcloud.define;

import java.util.HashMap;

import com.webcloud.R;

/**
 * lbs商业poi类型。
 * 1. 零0以下的类型为本地扩展使用
 * 2. 1101-1199是商业类型一类，后台规定，不可占用
 * 3. 1201-1299是商业类型二类，后台规定，不可占用
 * @author  bangyue
 * @version  [版本号, 2013-12-10]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class BusiPoiType {  
    
    /**当前点坐标*/
    public static final int L_CURR = 0;
    
    /**起点*/
    public static final int L_START = 1;
    
    /**终点*/
    public static final int L_END = 2;
    
    /**家*/
    public static final int L_HOME = 3;
    
    /**公司*/
    public static final int L_COMPANY = 4;
    
    /**查询*/
    public static final int L_SEARCH = -1;
    
    /**点击*/
    public static final int L_CLICK = -2;
    
    /**其他*/
    public static final int L_OTHER = -100;
    
    /**酒驾*/
    public static final int L_DRUNK = -3;
    
    /**拥堵*/
    public static final int L_JAM = -4;
    
    /**违章*/
    public static final int L_BROKERULE = -5;
    
    /**普通公司的图标*/
    public static final int L_GS = -6;
    
    /**停车场*/
    public static final int TCC = 1101;
    
    /**加油站*/
    public static final int JYZ = 1102;
    
    /**修车*/
    public static final int XCC = 1103;
    /**维修站*/
    public static final int WXZ = 1104;
    /***/
    public static final int CD = 1105;
    /**翼支付*/
    public static final int YZF = 1199;
    /**中餐店*/
    public static final int ZCD = 1201;
    /**外卖*/
    public static final int WM = 1202;
    /**西餐*/
    public static final int XCT = 1203;
    /**甜品*/
    public static final int TPD = 1204;
    /**火锅*/
    public static final int HGD = 1205;
    
    /**海鲜*/
    public static final int HXD = 1206;
    
    /**蛋糕*/
    public static final int DGD = 1207;
    
    /**咖啡*/
    public static final int CAFED = 1208;
    
    /**酒吧*/
    public static final int JBD = 1209;
    
    /**酒店*/
    public static final int HOTEL = 9901;
    
    /**银行*/
    public static final int BANK = 9902;
    
    /**超市*/
    public static final int MARKET = 9903;
    
    public final static String[] drawIds = {"1201", "1202", "1203", "1204", "1205", "1206", "1207", "1208", "1209",
        "1210"};
    
    public static HashMap<String, Integer> maps = new HashMap<String, Integer>();
    
    /** 
     * 根据poi类型，获取对应在地图上标记的图标。
     *
     * @param type
     * @param defaultId
     * @return
     */
    public static int getIconIdByPoiType(int type,int defaultId){
        int iconId = defaultId;
        switch (type) {
            case BusiPoiType.L_CURR:
                iconId = R.drawable.lsman;
                break;
            case 9902:
                iconId = R.drawable.mark_location;
                break;
            case BusiPoiType.L_START:
                iconId = R.drawable.start;
                break;
            case BusiPoiType.L_END:
                iconId = R.drawable.end;
                break;
            case BusiPoiType.L_HOME:
                iconId = R.drawable.icon_vehicle_home;
                break;
            case BusiPoiType.L_COMPANY:
                iconId = R.drawable.icon_vehicle_work;
                break;
            case BusiPoiType.L_BROKERULE:
                iconId = R.drawable.lsman;
                break;
            case BusiPoiType.L_DRUNK:
                iconId = R.drawable.lsman;
                break;
            case BusiPoiType.L_JAM:
                iconId = R.drawable.lsman;
                break;
            case BusiPoiType.L_GS:
                iconId = R.drawable.markpoint;
                break;
        }
        return iconId;
    }
}
