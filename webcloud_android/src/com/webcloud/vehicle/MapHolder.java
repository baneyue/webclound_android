package com.webcloud.vehicle;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.webcloud.R;
import com.webcloud.func.map.util.AMapUtil;

public class MapHolder implements AMapLocationListener, OnMarkerClickListener,
		OnMapScreenShotListener, OnMapLongClickListener, OnRouteSearchListener,
		OnGeocodeSearchListener, OnCameraChangeListener {

	public final static String TAG = MapHolder.class.getName();

	public City city;
	public MapView mapView;
	public AMap map;
	private Handler aHandler;
	private GeocodeSearch geocoderSearch;
	private MarkerOptions currentGecodeMarker;
	private DrivingRouteOverlay drivingRouteOverlay;
	private ProgressDialog dialog;
	private Activity activity;
	private MarkerOptions mine, destination;
	public NaviType naviType;
	public volatile boolean navigation;

	public MapHolder(MapView mapView, Activity context, Handler handler) {
		this.mapView = mapView;
		map = mapView.getMap();
		map.getUiSettings().setZoomControlsEnabled(false);
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.getUiSettings().setCompassEnabled(false);
		map.getUiSettings().setScaleControlsEnabled(false);
		map.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
		map.setMyLocationEnabled(true);
		map.setOnMapLongClickListener(this);
		map.setOnMarkerClickListener(this);
		map.setOnCameraChangeListener(this);
		geocoderSearch = new GeocodeSearch(context);
		geocoderSearch.setOnGeocodeSearchListener(this);
		mine = new MarkerOptions();
		mine.icon(BitmapDescriptorFactory.fromResource(R.drawable.mine));
		mine.title("我的位置");
		city = new City(); 
		naviType = NaviType.CAR;
		this.activity = context;
		this.aHandler = handler;
	}

	public boolean navigate() {
		if (!navigation) {
			if (null != destination) {
				dialog = ProgressDialog.show(activity, "导航", "正在为您搜索路线");
				dialog.setCancelable(true);
				RouteSearch.FromAndTo fat = new RouteSearch.FromAndTo(
						AMapUtil.convertToLatLonPoint(mine.getPosition()),
						AMapUtil.convertToLatLonPoint(destination
								.getPosition()));
				DriveRouteQuery query = new DriveRouteQuery(fat,
						RouteSearch.DrivingDefault, null, null, "");
				RouteSearch route = new RouteSearch(activity);
				route.setRouteSearchListener(this);
				route.calculateDriveRouteAsyn(query);
				navigation = true;
			} else {
				Toast.makeText(activity, "没有终点", Toast.LENGTH_SHORT).show();
			}
		}
		return navigation;
	}

	public void clear() {
		map.clear();
		navigation = false;
		destination = null;
		map.addMarker(mine);
	}

	public void getMapLocation() {
		dialog = ProgressDialog.show(activity, "定位", "正在搜索您的位置");
		dialog.setCancelable(true);
		LocationManagerProxy.getInstance(activity).requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 1000, 10, this);
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		Log.e("onLocationChanged",
				"经度 --> " + location.getLatitude() + " : 纬度 --> "
						+ location.getLongitude() + " ： 城市 --> "
						+ location.getCity() + " ： 城市code --> "
						+ location.getCityCode());
		LatLng position = new LatLng(location.getLatitude(),
				location.getLongitude());
		map.moveCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition(position, 18, 0, 0)));
		mine.position(position);
		city.name = location.getCity();
		city.code = location.getCityCode();
		getAddress(mine.getPosition());
		currentGecodeMarker = mine;
		map.addMarker(mine);
		LocationManagerProxy.getInstance(activity).removeUpdates(this);
		dialog.dismiss();
	}

	public void getMapScreenShot() {
		map.getMapScreenShot(this);
	}

	@Override
	public void onCameraChange(CameraPosition arg) {
		Message msg = aHandler.obtainMessage();
		msg.what = 0;
		msg.obj = "onCameraChange";
		aHandler.sendMessage(msg);
	}

	public void getPosition(String address) {
		GeocodeQuery query = new GeocodeQuery(address, city.name);
		geocoderSearch.getFromLocationNameAsyn(query);
	}
	
	public void getPosition(String address, String city) {
		GeocodeQuery query = new GeocodeQuery(address, city);
		geocoderSearch.getFromLocationNameAsyn(query);
	}
	
	@Override
	public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
		Log.e(TAG, "onRegeocodeSearched -> rCode : " + rCode);
		if (rCode == 0) {
			if (null == destination) {
				destination = new MarkerOptions();
				destination.title("目的地");
			}
			if (null != geocodeResult
					&& geocodeResult.getGeocodeAddressList().size() > 0) {
				destination.position(AMapUtil.convertToLatLng(geocodeResult
						.getGeocodeAddressList().get(0).getLatLonPoint()));		
				destination.snippet(geocodeResult.getGeocodeQuery().getLocationName());		
				map.addMarker(destination);
				Message msg = aHandler.obtainMessage();
				msg.what = 3;
				msg.obj = destination;
				aHandler.sendMessage(msg);
				map.moveCamera(CameraUpdateFactory
						.newCameraPosition(new CameraPosition(destination.getPosition(), 18, 0, 0)));
			}
		}
	}

	public void getAddress(LatLng position) {
		RegeocodeQuery query = new RegeocodeQuery(
				AMapUtil.convertToLatLonPoint(position), 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
		Log.e(TAG, "onRegeocodeSearched -> rCode : " + rCode);
		if (rCode == 0) {
			if (null != regeocodeResult
					&& null != regeocodeResult.getRegeocodeAddress()) {
				currentGecodeMarker.snippet(""
						+ regeocodeResult.getRegeocodeAddress()
								.getFormatAddress());
			}
			map.addMarker(currentGecodeMarker);
			Message msg = aHandler.obtainMessage();
			msg.what = 3;
			if (currentGecodeMarker == mine) {
				msg.arg1 = 1;
			}
			msg.obj = currentGecodeMarker;
			aHandler.sendMessage(msg);
			currentGecodeMarker = null;
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		Message msg = aHandler.obtainMessage();
		msg.what = 4;
		msg.arg1 = -1;
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				drivingRouteOverlay = new DrivingRouteOverlay(activity, map,
						result.getPaths().get(0), result.getStartPos(),
						result.getTargetPos());
				map.clear();// 清理地图上的所有覆盖物
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
				msg.arg1 = 1;
			}
		}
		dialog.dismiss();
		aHandler.sendMessage(msg);
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
	}

	@Override
	public void onMapLongClick(LatLng position) {
		Message msg = aHandler.obtainMessage();
		msg.what = 1;
		msg.obj = "onMapLongClick";
		aHandler.sendMessage(msg);
		if (!navigation) {
			if (null == destination) {
				destination = new MarkerOptions();
			}
			map.clear();
			if (null != mine) {
				map.addMarker(mine);
			}
			destination.title("目的的");
			destination.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.destination));
			destination.position(position);
			map.addMarker(destination);
			getAddress(destination.getPosition());
			currentGecodeMarker = destination;
		}
	}

	@Override
	public void onMapScreenShot(Bitmap bitmap) {
		Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ImageView view = new ImageView(activity);
		Matrix matrix = new Matrix();
		matrix.postScale(0.75f, 0.75f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		view.setImageBitmap(resizeBmp);
		dialog.setContentView(view, params);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Message msg = aHandler.obtainMessage();
		msg.what = 2;
		msg.obj = marker;
		if (mine.getTitle().equals(marker.getTitle())) {
			msg.arg1 = 1;
		}
		aHandler.sendMessage(msg);
		return false;
	}


	public void onResume() {
		mapView.onResume();
	}

	public void onPause() {
		mapView.onPause();
	}

	public void onDestroy() {
		mapView.onDestroy();
		LocationManagerProxy.getInstance(activity).destory();
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
	}

	static class City {
		public String name;
		public String code;
	}
	
	enum NaviType{CAR, BUS , FOOT};
}
