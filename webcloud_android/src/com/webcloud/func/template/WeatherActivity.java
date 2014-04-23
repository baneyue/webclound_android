package com.webcloud.func.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.funlib.http.HttpRequest;
import com.funlib.http.HttpRequestImpl;
import com.funlib.http.request.RequestListener;
import com.funlib.http.request.RequestStatus;
import com.funlib.http.request.Requester;
import com.funlib.json.JsonFriend;
import com.funlib.utily.DateTimeUtils;
import com.funlib.utily.MoonCalendar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webcloud.BaseActivity;
import com.webcloud.R;
import com.webcloud.define.HttpUrlImpl;
import com.webcloud.map.MapUtils;
import com.webcloud.model.AqiModel;

public class WeatherActivity extends BaseActivity {
	ScrollView svWeek;

	TextView tvDate, tvTemp, tvWChina, tvWind, tvPm;

	ImageView ivPic;

	String cityName, cityId;

	SharedPreferences mWeatherPrefs;

	boolean wetherSucc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);
		initView();
		mWeatherPrefs = getSharedPreferences("WeatherConfig", MODE_PRIVATE);
		requestWeatherCitycode(true);
	}

	private void initView() {
		svWeek = (ScrollView) findViewById(R.id.svWeek);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvTemp = (TextView) findViewById(R.id.tvTemp);
		tvWChina = (TextView) findViewById(R.id.tvWChina);
		tvWind = (TextView) findViewById(R.id.tvWind);
		tvPm = (TextView) findViewById(R.id.tvPm);
	}

	public void requestWeatherCitycode(boolean byCityname) {
		cityName = mWeatherPrefs.getString("cityName", null);
		if (byCityname && !TextUtils.isEmpty(cityName)) {
			// 看看天气城市id有没有备份
			cityId = mWeatherPrefs.getString(
					MapUtils.getCityNameExcludeShi(cityName), null);
			if (!TextUtils.isEmpty(cityId)) {
				// 请求中央气象台接口
				new Requester(this).request(requestListenerWeather,
						HttpUrlImpl.WEATHER.GET_WEATHER_BY_CITY_ID,
						HttpUrlImpl.WEATHER_CHINA_URL + cityId + ".html", null,
						HttpRequest.GET, false);
				return;
			} else {
				// 通过新浪的接口获得城市id
				Map<String, String> param = new HashMap<String, String>();
				param.put("encode", "utf-8");
				param.put("city", MapUtils.getCityNameExcludeShi(cityName));
				new Requester(this).request(requestListenerWeather,
						HttpUrlImpl.WEATHER.GET_CITYCODE_BY_SINA,
						HttpUrlImpl.WEATHER.GET_CITYCODE_BY_SINA.getUrl(),
						param, HttpRequest.GET, false);
			}
		} else {
			// 如果城市名称为空，则通过ip获取城市id
			new Requester(this).request(requestListenerWeather,
					HttpUrlImpl.WEATHER.GET_CITYCODE_BY_IP,
					HttpUrlImpl.WEATHER.GET_CITYCODE_BY_IP.getUrl(), null,
					HttpRequestImpl.GET, false);
		}
	}

	RequestListener requestListenerWeather = new RequestListener() {

		@Override
		public void requestStatusChanged(int statusCode, HttpUrlImpl requestId,
				String responseString, Map<String, String> params) {
			if (!(requestId instanceof HttpUrlImpl.WEATHER)) {
				return;
			}
			// 若无需业务处理就直接返回
			if (statusCode != RequestStatus.SUCCESS) {
				return;
			}
			try {
				HttpUrlImpl.WEATHER v1 = (HttpUrlImpl.WEATHER) requestId;
				switch (v1) {
				case GET_CITYCODE_BY_IP: {
					cityId = "0";
					try {
						// var ip="61.132.203.242";var
						// id=101220101;if(typeof(id_callback)!="undefined"){id_callback();}
						if (!TextUtils.isEmpty(responseString)) {
							String[] arr = responseString.split(";");
							if (arr.length > 2) {
								String[] ids = arr[1].split("=");
								if (ids.length == 2) {
									cityId = ids[1];
									// 请求中央气象台接口
									new Requester(WeatherActivity.this)
											.request(
													requestListenerWeather,
													HttpUrlImpl.WEATHER.GET_WEATHER_BY_CITY_ID,
													HttpUrlImpl.WEATHER_CHINA_URL
															+ cityId + ".html",
													null, HttpRequest.GET,
													false);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				case GET_CITYCODE_BY_SINA: {
					String cityId = "0";
					try {
						if (!TextUtils.isEmpty(responseString)) {
							cityId = responseString;
							// 请求中央气象台接口
							new Requester(WeatherActivity.this).request(
									requestListenerWeather,
									HttpUrlImpl.WEATHER.GET_WEATHER_BY_CITY_ID,
									HttpUrlImpl.WEATHER_CHINA_URL + cityId
											+ ".html", null, HttpRequest.GET,
									false);
						} else {
							requestWeatherCitycode(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
						requestWeatherCitycode(false);
					}
				}
					break;
				case GET_WEATHER_BY_CITY_ID: {
					try {
						JSONObject parent = JsonFriend
								.parseJSONObject(responseString);
						JSONObject weather = parent
								.getJSONObject("weatherinfo");
						String city = weather.containsKey("city") ? weather
								.getString("city") : null;
						String temp1 = weather.containsKey("temp1") ? weather
								.getString("temp1") : "气温无";
						String clzs = weather.containsKey("index_cl") ? weather
								.getString("index_cl") : "指数无";
						String tq = weather.containsKey("weather1") ? weather
								.getString("weather1") : "天气无";
						String wind1 = weather.containsKey("wind1") ? weather
								.getString("wind1") : "";
						String img1 = weather.containsKey("img1") ? weather
								.getString("img1") : null;
						String cityid = weather.containsKey("cityid") ? weather
								.getString("cityid") : null;
						String date = weather.containsKey("date_y") ? weather
								.getString("date_y") : null;
						String week = weather.containsKey("week") ? weather
								.getString("week") : null;
						// 储存城市和id对应
						if (!TextUtils.isEmpty(city)
								&& !TextUtils.isEmpty(cityid)) {
							Editor edit = mWeatherPrefs.edit();
							edit.putString(city, cityid);
							edit.putString("cityName", city);
							edit.commit();
						}
						// ivPic.setImageResource(WeaterUtil.TextToImage(tq));
						tvTemp.setText(temp1);
						showWeatherList(weather);
						tvWChina.setText(tq + "\n" + wind1);
						Calendar today = Calendar.getInstance();
						today.setTime(MoonCalendar.chineseDateFormat
								.parse(date));
						MoonCalendar lunar = new MoonCalendar(today);
						tvDate.setText(date + " " + week + " 农历"
								+ lunar.toString());
						try {
							InputStream is = getResources().getAssets().open(
									"b" + img1 + ".gif");
							Bitmap image = BitmapFactory.decodeStream(is);
							is.close();
							ivPic.setBackgroundDrawable(new BitmapDrawable(
									image));
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (!TextUtils.isEmpty(city)) {
							// 请求开放空气质量接口
							// token=5j1znBVAsnSf5xQyNQyq&city=hf&avg=true&stations=no
							Map<String, String> param = new HashMap<String, String>();
							param.put(
									"token",
									getResources().getString(
											R.string.pm25_key_lbs));
							param.put("avg", "true");
							param.put("stations", "no");
							param.put("city", city);
							new Requester(WeatherActivity.this).request(
									requestListenerWeather,
									HttpUrlImpl.WEATHER.AQI_DETAIL,
									HttpUrlImpl.WEATHER.AQI_DETAIL.getUrl(),
									param, HttpRequest.GET, false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				case AQI_DETAIL: {
					try {
						// 解析空气指数
						JsonFriend<AqiModel> aqiJson = new JsonFriend<AqiModel>(
								AqiModel.class);
						AqiModel aqi = aqiJson.parseArray(responseString)
								.get(0);
						String strPm = "pm2.5 " + aqi.getPm2_5() + "/"
								+ aqi.getQuality();
						String str = "\npm2.5 " + aqi.getPm2_5() + "/"
								+ aqi.getQuality();
						tvPm.setText(strPm);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void showWeatherList(JSONObject weather) {
		svWeek.removeAllViews();
		LinearLayout lin2 = new LinearLayout(getBaseContext());
		lin2.setOrientation(LinearLayout.VERTICAL);
		lin2.setGravity(Gravity.BOTTOM);
		try {
			String date = weather.containsKey("date_y") ? weather
					.getString("date_y") : null;
			Date d = DateTimeUtils.formatSCStringToDate(date);
			Calendar today = Calendar.getInstance();
			today.setTime(MoonCalendar.chineseDateFormat.parse(date));
			/*
			 * MoonCalendar lunar = new MoonCalendar(today);
			 * tvDate.setText(date+" "+week+" 农历"+lunar.toString());
			 */
			for (int i = 0; i < 5; i++) {
				int j = i + 1;
				String temp1 = weather.containsKey("temp" + j) ? weather
						.getString("temp" + j) : "";
				String weather1 = weather.containsKey("weather" + j) ? weather
						.getString("weather" + j) : "";
				String wind1 = weather.containsKey("wind" + j) ? weather
						.getString("wind" + j) : "";
				String img1 = weather.containsKey("img" + (i * 2 + 1)) ? weather
						.getString("img" + (i * 2 + 1)) : "";

				LinearLayout lin = (LinearLayout) getLayoutInflater().inflate(
						R.layout.weather_list_item, null);
				TextView tvWChina = (TextView) lin.findViewById(R.id.tvWChina);
				TextView tvDate = (TextView) lin.findViewById(R.id.tvDate);
				TextView tvTemp = (TextView) lin.findViewById(R.id.tvTemp);
				ImageView ivPic = (ImageView) lin.findViewById(R.id.ivPic);
				ImageLoader imgLoader = mgr.imgCacheMgr.getImageLoader();
				imgLoader.displayImage(
						HttpUrlImpl.WEATHER.GET_WEATHER_PIC.getUrl() + "a"
								+ img1 + ".gif", ivPic);

				tvWChina.setText(weather1);
				tvTemp.setText(temp1);
				tvDate.setText(DateTimeUtils.formatCalToSCString(today));
				today.add(Calendar.DATE, 1);
				lin2.addView(lin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		svWeek.addView(lin2);
	}

}
