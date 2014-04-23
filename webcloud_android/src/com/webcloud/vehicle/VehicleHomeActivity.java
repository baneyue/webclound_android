package com.webcloud.vehicle;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.webcloud.R;
import com.webcloud.vehicle.MapHolder.NaviType;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class VehicleHomeActivity extends Activity implements OnClickListener,
		CheckBox.OnCheckedChangeListener, TextWatcher, InputtipsListener,
		OnItemClickListener, RadioGroup.OnCheckedChangeListener {

	public static final String TAG = VehicleHomeActivity.class.getName();

	private MapView mapView;
	private MapHolder holder;
	private View infoLayout, searchLayout;
	private TextView title, info;
	private Inputtips tips;
	private List<Tip> data;
	private SearchAdapter adapter;
	private ListView list;
	private InputMethodManager inputManager;
	private NaviWindow navi;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:// 地图拖动
				closeInfo();
				break;
			case 1:// 长按地图
				showInfo();
				showSearch();
				break;
			case 2:// 点击marker
				Marker marker = (Marker) msg.obj;
				if (null != marker) {
					showInfo();
					title.setText("" + marker.getTitle());
					info.setText("" + marker.getSnippet());
					if (msg.arg1 == 1) {
						infoLayout.findViewById(R.id.there).setEnabled(false);
					} else {
						infoLayout.findViewById(R.id.there).setEnabled(true);
					}
				}
				break;
			case 3:// 地址查询 / 反地址查询
				MarkerOptions options = (MarkerOptions) msg.obj;
				if (null != options) {
					showInfo();
					title.setText("" + options.getTitle());
					info.setText("" + options.getSnippet());
					if (msg.arg1 == 1) {
						infoLayout.findViewById(R.id.there).setEnabled(false);
					} else {
						infoLayout.findViewById(R.id.there).setEnabled(true);
					}
				}
				break;
			case 4:// 导航成功
				if(msg.arg1 == 1){
					closeInfo();
					closeSearch();
				}else{
					holder.navigation = false;
					Toast.makeText(VehicleHomeActivity.this, "导航失败，请稍后再试", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehicle_home);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		holder = new MapHolder(mapView, this, mHandler);
		findViewById(R.id.larger).setOnClickListener(this);
		findViewById(R.id.smaller).setOnClickListener(this);
		findViewById(R.id.location).setOnClickListener(this);
		findViewById(R.id.searchText).setOnClickListener(this);
		((CheckBox) findViewById(R.id.traffic))
				.setOnCheckedChangeListener(this);
		infoLayout = getLayoutInflater().inflate(R.layout.vehicle_info, null);
		infoLayout.findViewById(R.id.there).setOnClickListener(this);
		title = (TextView) infoLayout.findViewById(R.id.title);
		info = (TextView) infoLayout.findViewById(R.id.info);
		searchLayout = getLayoutInflater().inflate(R.layout.vehicle_search, null);
		searchLayout.findViewById(R.id.searchInput).setOnClickListener(this);
		searchLayout.findViewById(R.id.back).setOnClickListener(this);
		((EditText) searchLayout.findViewById(R.id.searchInput))
				.addTextChangedListener(this);
		tips = new Inputtips(VehicleHomeActivity.this, this);
		data = new ArrayList<Tip>();
		adapter = new SearchAdapter(data, this);
		list = (ListView) searchLayout.findViewById(R.id.searchList);
		list.setCacheColorHint(Color.TRANSPARENT);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		navi = new NaviWindow(this, mHandler);
		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		holder.getMapLocation();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.larger:
			holder.map.animateCamera(CameraUpdateFactory.zoomIn());
			break;
		case R.id.smaller:
			holder.map.animateCamera(CameraUpdateFactory.zoomOut());
			break;
		case R.id.location:
			holder.getMapLocation();
			break;
		case R.id.there:
			navi.show();
			break;
		case R.id.back:
			onBackPressed();
			break;
		case R.id.searchButton:
			holder.navigate();
			navi.close();
			break;
		case R.id.searchText:
			showSearch();
		case R.id.searchInput:
			inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.bus:
			holder.naviType = NaviType.BUS;
			break;
		case R.id.car:
			holder.naviType = NaviType.CAR;
			break;
		case R.id.foot:
			holder.naviType = NaviType.FOOT;
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		switch (view.getId()) {
		case R.id.traffic:
			holder.map.setTrafficEnabled(isChecked);
			break;
		default:
			break;
		}
	}

	@Override
	public void onTextChanged(CharSequence chars, int start, int before,
			int count) {
		if (!TextUtils.isEmpty(chars)) {
			try {
				tips.requestInputtips(chars.toString(), holder.city.name);
			} catch (AMapException e) {
				e.printStackTrace();
			}
		} else {
			data.clear();
			adapter.notifyDataSetChanged();
			searchLayout.findViewById(R.id.listLayout).setVisibility(
					View.INVISIBLE);
		}
	};

	@Override
	public void onGetInputtips(List<Tip> tipList, int rCode) {
		data.clear();
		if (rCode == 0) {
			if (tipList.size() > 0) {
				searchLayout.findViewById(R.id.listLayout).setVisibility(
						View.VISIBLE);
				data.addAll(tipList);
			} else {
				data.add(new Tip());
			}
		} else {
			data.add(new Tip());
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Tip tip = data.get(position);
		if (null != tip.getName() && !"".equals(tip.getName())) {
			holder.getPosition(tip.getName(), tip.getDistrict());
			searchLayout.findViewById(R.id.listLayout).setVisibility(
					View.INVISIBLE);
			data.clear();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onBackPressed() {
		if (navi.isShown()) {
			navi.close();
		} else if (infoLayout.isShown() || searchLayout.isShown()) {
			FrameLayout layout = (FrameLayout) findViewById(R.id.infoLayout);
			layout.removeAllViews();
			layout = (FrameLayout) findViewById(R.id.searchLayout);
			data.clear();
			adapter.notifyDataSetChanged();
			searchLayout.findViewById(R.id.listLayout).setVisibility(
					View.INVISIBLE);
			layout.removeAllViews();
		} else if (holder.navigation) {
			holder.clear();
		} else {
			super.onBackPressed();
		}
	}

	private void showSearch(){
		if (!searchLayout.isShown()) {
			FrameLayout layout = (FrameLayout) findViewById(R.id.searchLayout);
			layout.addView(searchLayout);
			((EditText) layout.findViewById(R.id.searchInput)).setText("");
		}
	}
	
	private void closeSearch(){
		if (searchLayout.isShown()) {
			FrameLayout layout = (FrameLayout) findViewById(R.id.searchLayout);
			layout.removeAllViews();
		}		
	}
	
	private void showInfo(){
		if (!infoLayout.isShown()) {
			FrameLayout layout = (FrameLayout) findViewById(R.id.infoLayout);
			layout.addView(infoLayout);
		}
	}
	
	private void closeInfo(){
		if (infoLayout.isShown()) {
			FrameLayout layout = (FrameLayout) findViewById(R.id.infoLayout);
			layout.removeAllViews();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		holder.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		holder.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		holder.onDestroy();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
