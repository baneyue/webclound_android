package com.webcloud.map.street;

public interface OnClickStreetOverlayListener {
    /** 
     * 当街景图层受到点击时触发回调。
     *
     * @param poi
     * @see [类、类#方法、类#成员]
     */
    public void onClick(StreetPoiData poi);
}
