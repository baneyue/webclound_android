package com.webcloud.func.map.util;

import com.webcloud.R;

public class WeaterUtil 
{
  public static int TextToImage(String weater)
  {
	  if("晴  ".equals(weater))
	  {
		  return R.drawable.w_qing;
	  }
	  else if("大雨".equals(weater))
	  {
		  return R.drawable.w_dayu;
	  }
	  else if("多云转阴".equals(weater))
	  {
		  return R.drawable.w_duoyunyintian;
	  }
	  else if("晴天多云".equals(weater))
	  {
		  return R.drawable.w_qingtianduoyun;
	  }
	  else if("晴天有大雨".equals(weater))
	  {
		  return R.drawable.w_qingtianyoudayu; 
	  }
	  else if("晴天有雾".equals(weater))
	  {
		  return R.drawable.w_qingtianyouwu;
	  }
	  else if("晴天有小雨".equals(weater))
	  {
		  return R.drawable.w_qingtianyouxiaoyu;
	  }
	  else if("晴天有雨".equals(weater))
	  {
		  return R.drawable.w_qingtianyouyu;
	  }
	  else if("晴转多云".equals(weater))
	  {
		  return R.drawable.w_qingzhuanduoyun;
	  }
	  else if("雾".equals(weater))
	  {
		  return R.drawable.w_wu;
	  }
	  else if("小雨".equals(weater))
	  {
		  return R.drawable.w_xiaoyu;
	  }
	  else if("雪".equals(weater))
	  {
		  return R.drawable.w_xue;
	  }
	  else if("阴".equals(weater))
	  {
		  return R.drawable.w_yintian;
	  }
	  else if("中雨".equals(weater))
	  {
		  return R.drawable.w_zhongyu;
	  }
	  else if("多云".equals(weater))
	  {
		  return R.drawable.w_qingzhuanduoyun;
	  }
	  else
	  {
		  return R.drawable.w_qingtianduoyun;
	  }
  }
}
