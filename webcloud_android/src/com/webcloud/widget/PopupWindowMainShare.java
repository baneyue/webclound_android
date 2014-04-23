package com.webcloud.widget;

import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.api.YXMessage;
import im.yixin.sdk.api.YXWebPageMessageData;
import im.yixin.sdk.util.BitmapUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.cloudsdk.BaiduException;
import com.baidu.cloudsdk.IBaiduListener;
import com.baidu.cloudsdk.social.core.MediaType;
import com.baidu.cloudsdk.social.oauth.SocialConfig;
import com.baidu.cloudsdk.social.share.ShareContent;
import com.baidu.cloudsdk.social.share.SocialShare;
import com.funlib.json.JsonFriend;
import com.webcloud.R;
import com.webcloud.component.NewToast;
import com.webcloud.define.Global;
import com.webcloud.utily.Utily;

public class PopupWindowMainShare extends PopupWindow {

	private Context context;
	private String mshareTitle;
	private String mshareContent;
	private String mshareImgPath;
	private String mshareResUri;
	private AuthListener mAuthListener;
	private IYXAPI api;
	private Bitmap bmp;

	public PopupWindowMainShare() {
	}

	public PopupWindowMainShare(final Context context, View view,
			String shareTitle, String shareContent, String shareImgPath,
			String shareResUri) {
		super(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		this.context = context;
		this.mshareContent = shareContent;
		this.mshareTitle = shareTitle;
		this.mshareImgPath = shareImgPath;
		this.mshareResUri = shareResUri;
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setOutsideTouchable(true);
		this.setFocusable(true);

		final String clientID = SocialConfig.getInstance(context).getClientId(
				MediaType.BAIDU);
		final SocialShare share = SocialShare.getInstance(context, clientID);
		Uri shareUri = Uri.parse(shareImgPath);
		final ShareContent mcontent = new ShareContent(shareTitle,
				shareContent, shareResUri, shareUri);
		mAuthListener = new AuthListener();

		final ImageButton btnSms = (ImageButton) view
				.findViewById(R.id.ivpopsharesms);
		btnSms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = context.getResources().getString(
						R.string.share_content_main);
				String content = str + "\n" + " http://www.leso114.com/d#app";
				mcontent.setContent(content);
				Intent mIntent = new Intent(Intent.ACTION_VIEW);
				mIntent.putExtra("address", "");
				mIntent.putExtra("sms_body", mcontent.getTitle() + "\n"
						+ mcontent.getContent());
				mIntent.setType("vnd.android-dir/mms-sms");
				context.startActivity(mIntent);
				
				shareAusltData();
				
				dismiss();
			}
		});
		final ImageButton btnWeixin = (ImageButton) view
				.findViewById(R.id.ivpopshareweixin);
		btnWeixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				SocialShare.getInstance(context, clientID).share(mcontent,
						MediaType.WEIXIN.toString(), mAuthListener, true);
				
				shareAusltData();
				
			}
		});
		final ImageButton btnYixin = (ImageButton) view
				.findViewById(R.id.ivpopshareyixing);
		btnYixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				api = YXAPIFactory.createYXAPI(context, Global.YIXINAPPID);
				api.registerApp();
				new Thread() {
					@Override
					public void run() {
						try {
							bmp = BitmapFactory.decodeStream(new URL(
									mshareImgPath).openStream());
							if (bmp != null) {
								handler.sendEmptyMessage(0);
							}
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});
		final ImageButton btnFriend = (ImageButton) view
				.findViewById(R.id.ivpopsharefriend);
		btnFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				mcontent.setTitle("乐搜：搜美食，逛街景，避堵点，查违章，查快递…神器啊!");
				SocialShare.getInstance(context, clientID).share(mcontent,
						MediaType.WEIXIN_TIMELINE.toString(), mAuthListener,
						true);
			}
		});
		btnWeixin.setVisibility(View.VISIBLE);
		btnYixin.setVisibility(View.VISIBLE);
		btnFriend.setVisibility(View.VISIBLE);
		btnSms.setVisibility(View.VISIBLE);

		ImageButton btnMore = (ImageButton) view.findViewById(R.id.ivpopshare);
		btnMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String imgPath = mshareImgPath.toString();
				Utily.shareMsg(context, "分享", mshareTitle, mshareContent,
						imgPath);
			}
		});
		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.laypopshare);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private class AuthListener implements IBaiduListener {

		@Override
		public void onComplete() {
			Log.d("PopupWindowShare", "authorized onComplete");

			Toast.makeText(context, "成功!", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onComplete(JSONObject data) {

			Toast.makeText(context, "成功!", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onComplete(JSONArray data) {

			Toast.makeText(context, "成功!", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onCancel() {
			Log.d("PopupWindowShare", "authorized oncancel");
			Toast.makeText(context, "成功!", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(BaiduException ex) {
			Toast.makeText(context, "失败!" + ex.toString(), Toast.LENGTH_SHORT)
					.show();
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				try {
					YXWebPageMessageData webpage = new YXWebPageMessageData();
					webpage.webPageUrl = mshareResUri;
					YXMessage yxMsg = new YXMessage(webpage);
					yxMsg.title = mshareTitle;
					yxMsg.description = mshareContent;
					Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150,
							true);
					bmp.recycle();
					yxMsg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true); // 设置缩略图

					SendMessageToYX.Req req = new SendMessageToYX.Req();
					req.transaction = buildTransaction("webpage");
					req.message = yxMsg;
					req.scene = SendMessageToYX.Req.YXSceneSession;
					api.sendRequest(req);
					
					shareAusltData();
					
					// finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == 1) {
				String str = (String) msg.obj;
				if (str != null && str.length() > 0) {
					// {"retcode":"0","retmsg":"成功","retdata":{"score":"1"}}
					com.alibaba.fastjson.JSONObject obj = JsonFriend.parseJSONObject(str);
					String code = obj.getString("retcode");
					if (code.equals("0")) {
						com.alibaba.fastjson.JSONObject retdata = obj.getJSONObject("retdata");
						String score = retdata.getString("score");
						Toast.makeText(context, "分享成功!", 0).show();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						NewToast.show(context, "+" + score);
					}

				}
			}
		};
	};

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	private void shareAusltData() {
		/*final String imsi = CTCloudApplication
				.getInstance().getImsi();
		final String hashcode = CTCloudApplication.getInstance()
				.getHashcode();
		if (imsi != null && hashcode != null) {
			new Thread() {
				@Override
				public void run() {
					// type = 0 id = 1 op = 4 --> 分享
					String msg = ActionObjiectUtil.http_net(context, imsi, hashcode, "1", "4", "0");
					Message msg1 = new Message();
					msg1.obj = msg;
					msg1.what = 1;
					handler.sendMessage(msg1);
				}
			}.start();
		}*/
	}

}
