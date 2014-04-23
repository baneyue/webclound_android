package com.webcloud.func.map;

import android.os.Bundle;

import com.amap.api.maps.MapView;
import com.webcloud.R;
import com.webcloud.manager.SystemManager;
import com.webcloud.manager.client.UserManager;
import com.webcloud.map.BaseMapFragmentActivity;
import com.webcloud.map.MapViewHolderImpl;
import com.webcloud.model.GaoDeAddress;

public class MapLocationActivity extends BaseMapFragmentActivity {
    
    private LocationMapViewHolder mapHolder;
    private UserManager userMgr;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);
        mapHolder = new LocationMapViewHolder(this, (MapView)findViewById(R.id.mvMap), savedInstanceState);
        userMgr = SystemManager.getInstance(getApplication()).userMgr;
    }
    
    class LocationMapViewHolder extends MapViewHolderImpl{

        protected LocationMapViewHolder(MapLocationActivity mContext, MapView mMapView, Bundle savedInstanceState) {
            super(mContext, mMapView, savedInstanceState);
            setMapHolder(this);
            this.mContext = mContext;
            if (mAddress != null) {
                markCurrPoint();
            }
        }

        @Override
        protected void onMapConfig() {
            
        }

        @Override
        protected void onReceiveAddress(GaoDeAddress gdAddress) {
            
        }

        @Override
        protected void onReceiveLocation(double iLatitu, double iLongti) {
            
        }
        
    }
}
