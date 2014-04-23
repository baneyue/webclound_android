package com.funlib.datacache;

import java.io.Serializable;

public class DataCacheModel implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public String content;            		/** 内容 */
//    public long lastModifiedTime;           /** 上次修改时间 */
//    public long maxAvaiableTime;            /** cache有效时间 */
//    public long saveTime;                   /** 保存cache时间 */
    public String cacheHeader;
}
