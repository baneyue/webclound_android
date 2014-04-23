package com.webcloud.define;

/**
 * http接口枚举使用接口来组织。
 * 在此处进行版本标识。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-10-19]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface HttpUrlImpl {
    
    public static String IMG_URL = "http://61.191.44.251/cloudservise/";
    public static String SERVER_URL = "http://61.191.44.251/cloudservise/";
    
    public static String SERVER_CTB_URL = "http://lk.chetuobang.com/snstraffic/";//key:ctbhndx2013
    
    /**pm25开放平台*/
    public static String PM25_URL = "http://www.pm25.in/api/querys/";
    
    /**中国气象台开放平台*/
    public static String WEATHER_CHINA_URL = "http://m.weather.com.cn/data/";
    
    /**通过请求这个接口获取，返回结果需要解析*/
    public static String WEATHER_GET_CITYCODE_BY_IP_URL = "http://61.4.185.48:81/g/";
    
    /**新浪通过城市名获取天气id,这是新浪个一个私有接口，已经使用*/
    public static String WEATHER_GET_CITYCODE_BY_SINA_URL = "http://someapi.sinaapp.com/citycode/";
    
    public static String BAIDU_SDK_URL = "http://api.map.baidu.com/";
    
    public int getId();
    
    public String getUrl();
    
    public static enum V1 implements HttpUrlImpl {
        A;
        
        @Override
        public int getId() {
            return this.ordinal();
        }
        
        @Override
        public String getUrl() {
            String url = SERVER_URL;
            switch (this) {
                case A:
                    url += "AD_0_0_0.ashx";
                    break;
                default:
                    break;
            }
            return url;
        }
    }
    
    public static enum CTB implements HttpUrlImpl {
        /**车托邦实时路况*/
        REALTIME_ROUTE_TRAFFIC,
        /**热门关键字，目前只提供长沙的数据*/
        HOTKEYS,
        /**查询电子狗*/
        EDOG;
        
        @Override
        public int getId() {
            return this.ordinal();
        }
        
        @Override
        public String getUrl() {
            String url = SERVER_CTB_URL;
            switch (this) {
                case REALTIME_ROUTE_TRAFFIC:
                    url += "getroadcondition";//?format=json&keywords=URLEncode.encode("长沙",“utf-8”)
                    break;
                case EDOG:
                    url += "getedog";//?format=json&lon=113.02017151355312&lat=28.193030581147337&range=1000&app_id=1
                    break;
                case HOTKEYS:
                    url += "gethotkeys";//?format=json
                    break;
                default:
                    break;
            }
            return url;
        }
    }
    
    /**空气质量指数*/
    public static enum WEATHER implements HttpUrlImpl {
        /**根据城市名称查询aqi详情*/
        AQI_DETAIL,
        /**所有可查询aqi的城市列表*/
        ALL_CITIES,
        /**获取中央气象台的cityId*/
        GET_CITYCODE_BY_IP,
        /**从新浪app获取中央气象台cityId*/
        GET_CITYCODE_BY_SINA,
        /**通过中央气象台的cityId，查询天气*/
        GET_WEATHER_BY_CITY_ID,
        /**获取中央天气图片*/
        GET_WEATHER_PIC;
        
        @Override
        public int getId() {
            return this.ordinal();
        }
        
        @Override
        public String getUrl() {
            String url = SERVER_CTB_URL;
            switch (this) {
                case AQI_DETAIL:
                    url = PM25_URL + "aqi_details.json";//token=5j1znBVAsnSf5xQyNQyq&city=hf&avg=true&stations=no"
                    break;
                case ALL_CITIES:
                    url = PM25_URL + "all_cities.json";//token=5j1znBVAsnSf5xQyNQyq
                    break;
                case GET_CITYCODE_BY_IP:
                    url = WEATHER_GET_CITYCODE_BY_IP_URL;//无参
                    break;
                case GET_CITYCODE_BY_SINA:
                    url = WEATHER_GET_CITYCODE_BY_SINA_URL;//?city=%E5%90%88%E8%82%A5%E5%B8%82&encode=utf-8
                    break;
                case GET_WEATHER_BY_CITY_ID:
                    url = WEATHER_CHINA_URL;//101220101.html
                    break;
                case GET_WEATHER_PIC:
                    url = "http://m.weather.com.cn/img/";
                    break;
                default:
                    break;
            }
            return url;
        }
    }
    
    public static enum EXPRESS implements HttpUrlImpl {
        
        GETEXPRESS;
        
        @Override
        public int getId() {
            return this.ordinal();
        }
        
        @Override
        public String getUrl() {
            String url = "http://api.ickd.cn/";
            switch (this) {
                case GETEXPRESS:
                    break;
                default:
                    break;
            }
            return url;
        }
    }
    
    public static enum EMAIL implements HttpUrlImpl {
        
        GET_INBOX_EMAIL,
        GET_EMAIL_DETAIL,
        SENT_EMAIL;
        
        @Override
        public int getId() {
            return this.ordinal();
        }
        
        @Override
        public String getUrl() {
            String url = IMG_URL;
            switch (this) {
                case GET_INBOX_EMAIL:
                	url += "sso/mail_list.action";
                    break;
                case GET_EMAIL_DETAIL:
                	url += "sso/mail_getDetail.action";
                	break;
                case SENT_EMAIL:
                	url += "sso/mail_send.action";
                	break;
                default:
                    break;
            }
            return url;
        }
    }
    
}
