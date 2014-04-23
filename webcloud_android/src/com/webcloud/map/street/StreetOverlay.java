
package com.webcloud.map.street;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.tencent.street.animation.AnimGLSet;
import com.tencent.street.animation.ScaleResumeAnimGL;
import com.tencent.street.animation.TranslateAnimGL;
import com.tencent.street.overlay.ItemizedOverlay;
import com.tencent.street.overlay.model.ItemModel;

/**
 * 一个自己实现的overlay的example
 * 
 */
public class StreetOverlay extends ItemizedOverlay {

    private ArrayList<StreetPoiData> mPois;
    
    private OnClickStreetOverlayListener clicker;

    public StreetOverlay(ArrayList<StreetPoiData> pois,OnClickStreetOverlayListener clicker) {
        this.mPois = pois;
        this.clicker = clicker;
    }

    @Override
    public int size() {
        return mPois.size();
    }

    @Override
    public void onTap(int index, float x, float y) {
        /**处理图层点击事件*/
        Bitmap bitmap = Bitmap.createBitmap(mPois.get(index).markerPressed);
        
        StreetPoiData poi = mPois.get(index);
        poi.updateMarker(bitmap, bitmap.toString());
        refresh(index);
        
        //处理回调
        if(clicker != null){
            clicker.onClick(poi);
        }
    }

    @Override
    public ItemModel getItem(int index) {
        final StreetPoiData poi = mPois.get(index);
        if (poi == null)
            return null;

        ItemModel item = new CustomerItem(poi.latE6, poi.lonE6, poi.heightOffset);
        item.setAdapter(new ItemModel.IItemMarkerAdapter() {

            @Override
            public int getMarkerWidth() {
                return poi.marker.getWidth();
            }

            @Override
            public int getMarkerHeight() {
                return poi.marker.getHeight();
            }

            @Override
            public Bitmap getMarker(int state) {
                if (state == CustomerItem.STATE_PRESSED && poi.markerPressed != null) {
                    return poi.markerPressed;
                }
                return poi.marker;
            }

            @Override
            public void onGetMarker(boolean suc) {

            }

            @Override
            public String getMarkerUID() {
                return poi.uid;
            }
        });

        TranslateAnimGL translateAnim = new TranslateAnimGL(0, 0, 180, 0, 400);
        // ScaleAnimGL scaleAnimGLMin = new ScaleAnimGL(1, 1, 1, 0.7f, 200);
        // ScaleAnimGL scaleAnimGLResume = new ScaleAnimGL(1, 1, 0.7f, 1f,
        // 2000);
        ScaleResumeAnimGL scaleResumeAnimGL = new ScaleResumeAnimGL(1, 1, 1, 0.7f, 100, 100);
        // ScaleAnimGL empty = new ScaleAnimGL(1, 1, 1, 1, 1000L);
        AnimGLSet animset = new AnimGLSet(translateAnim, scaleResumeAnimGL);// ,
                                                                            // scaleAnimGLResume,
                                                                            // empty);
        // animset.setRepeat(true);
        item.startAnim(animset);

        return item;
    }

    /**
     * 自定义一个Item 用来实现每个item的动画
     * 
     * @author michaelzuo
     */
    private class CustomerItem extends ItemModel {

        private float heightOffset;

        public CustomerItem(int latE6, int lonE6, IItemMarkerAdapter adapter, float offset) {
            super(latE6, lonE6, adapter);
            this.heightOffset = offset;
        }

        public CustomerItem(int latE6, int lonE6, float offset) {
            super(latE6, lonE6);
            this.heightOffset = offset;
        }

        @Override
        public float onGetItemScale(double distance, float angleScale) {
            /*
             * 当distance < minDis , scale为maxScale 当distance > maxDis ,
             * scale为minScale 当distance在minDis和maxDis之间 , scale按比例变化 当distance >
             * maxShowDis , 不再显示
             */
            float scale = 1.0f;

            // 近大远小功能
            // final float minScale = 0.8f;
            // final float maxScale = 1.5f;
            // final double minDis = 20;
            // final double maxDis = 40;
            // final double maxShowDis = 150;
            //
            // if(distance < minDis) {
            // scale = maxScale;
            // } else if (distance < maxDis) {
            // scale = (float) (maxScale - (maxScale - minScale) * (distance -
            // minDis) / (maxDis - minDis));
            // } else if (distance < maxShowDis) {
            // scale = minScale;
            // } else {
            // scale = SCALE_INVISIBLE;
            // return scale;
            // }

            // 根据视角poi进行缩放
            final float factor = 0.2f; // 根据视角放大倍数对Item缩放的因子
            scale = scale + (angleScale - 1) * factor;

            return scale;
        }

        @Override
        protected float onGetItemHeightOffset() {
            return this.heightOffset;
        }

        @Override
        public void setAdapter(IItemMarkerAdapter adapter) {
            super.setAdapter(adapter);
        }

    }

}
