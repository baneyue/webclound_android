package com.webcloud.utily;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.funlib.file.FileUtily;
import com.webcloud.R;
import com.webcloud.WebCloudApplication;
import com.webcloud.component.MessageDialog;
import com.webcloud.component.MessageDialogListener;
import com.webcloud.define.Global;
import com.webcloud.define.HttpUrlImpl;

public class Utily {
	private static final String TAG = "com.ct.utily.Utily";

	public static void showNetWorkErrorDialog(final Activity activity) {

		MessageDialog dialog = new MessageDialog();
		dialog.showDialogOKCancel(activity,
				activity.getString(R.string.network_error),
				new MessageDialogListener() {

					@Override
					public void onMessageDialogClick(MessageDialog dialog,
							int which) {
						if (which == MessageDialog.MESSAGEDIALOG_OK) {
							activity.startActivity(new Intent(
									Settings.ACTION_WIFI_SETTINGS));
						}
					}
				});
	}

	public static void showFailDialog(final Activity activity) {

		MessageDialog dialog = new MessageDialog();
		dialog.showDialogOK(activity, activity.getString(R.string.json_fail),
				null);
	}

	public static void showToast(Context context, String msg) {

		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/*public static void showMyToast(Context context, String num) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom, null);
		TextView title = (TextView) layout.findViewById(R.id.txtToastNum);
		TextView content = (TextView) layout.findViewById(R.id.txtToastContent);
		title.setText(num);
		// content.setText(msg);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		if (num.trim().equals("+") || num.trim().equals("+0")) {
			
		} else {
			toast.show();
		}
	}*/

	public static boolean isStringEmpty(String string) {

		boolean ret = TextUtils.isEmpty(string);
		if (ret == true)
			return true;

		String tmp = string.toLowerCase();
		if (tmp.equals("null"))
			return true;

		return false;
	}

	public static Bitmap getTopRoundCornerImage(Bitmap bitmap, int roundPixels) {

		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = roundPixels;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			return output;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean checkMobile(String mobile) {

		String regex = "^1(3[0-9]|5[012356789]|8[0789])\\d{8}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mobile);
		return m.find();
	}

	public static void quitApplication(final Context context) {

		FileUtily.deleteTmpFiles(context);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		com.funlib.utily.Utily.quitApplication((Activity) context);
	}

	/**
	 * 上传文件是否过大。 不超过5M.
	 * 
	 * @param context
	 * @param size
	 */
	public static boolean isUploadSizeOverflow(Activity context, int size) {
		if (size > 5 * 1024 * 1024) {
			MessageDialog dialog = new MessageDialog();
			dialog.initDialogRes(0, 0, 0, 0, false);
			dialog.showDialogOK(context, "该文件太大，请上传大小不超过5M的文件",
					new MessageDialogListener() {

						public void onMessageDialogClick(MessageDialog dialog,
								int which) {
							if (which == MessageDialog.MESSAGEDIALOG_OK) {
								dialog.dismissMessageDialog();
							}
						}
					});
			return true;
		}
		return false;
	}

	/**
	 * 信息分享功能(不支持微信)
	 * 
	 * @param context
	 * @param activityTitle
	 *            弹出框标题
	 * @param msgTitle
	 * @param msgText
	 *            分享信息
	 * @param imgPath
	 *            要分享的图片路径
	 */
	public static void shareMsg(Context context, CharSequence activityTitle,
			String msgTitle, String msgText, String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/*");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u); // 本地图片
			} else {
				intent.setType("text/plain");
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, activityTitle));
	}

	public static void copyShareQRCodeToSDCard(Context context) {
		try {
			String fileName = com.funlib.utily.Utily.getSDPath() + "lbs/leso.png";
			Log.d("OpenPic-save-logo", "path="+fileName);
			File file = new File(fileName);
			if (file.exists()) {
				return;
			}
			InputStream finput = context.getResources().openRawResource(R.drawable.ic_launcher);
			FileOutputStream foutput;
			foutput = new FileOutputStream(file);

			BufferedOutputStream bos = new BufferedOutputStream(foutput);
			int b;
			while (true) {
				if (finput.available() < 1024) {
					while ((b = finput.read()) != -1) {
						bos.write(b);
					}
					break;
				} else {
					b = finput.read();
					bos.write(b);
				}
			}
			finput.close();
			bos.close();
			foutput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * 给listView设置指定高度，根据内容个数适配。
	 *
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
					MeasureSpec.AT_MOST);
			listItem.measure(desiredWidth, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.height += 12;// if without this statement,the listview will be a
							// little short
		listView.setLayoutParams(params);
	}

	/** 
	 * 给其他listView设置当前listView一样的高度。
	 *
	 * @param listView
	 * @param listView2
	 */
	public static void setOtherListViewHeightBasedOnChildren(ListView listView,
			ListView listView2) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.height += 6;// if without this statement,the listview will be a
							// little short
		listView2.setLayoutParams(params);
	}

	public static String renameImgUrl(String imgUrl, int flag) {
		String url = "";
		if (flag == 1) {
			url = imgUrl.replace(".", "_001.");
		} else if (flag == 2) {
			url = imgUrl.replace(".", "_002.");
		} else if (flag == 3) {
			url = imgUrl.replace(".", "_003.");
		}
		return url;
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// opt.inSampleSize = 2;// 设置图片为原来的1/2
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/** 
     * 获取url全称。
     *
     * @param requestId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getWholeUrl(HttpUrlImpl requestId) {
        String url = requestId.getUrl();
        
        if (TextUtils.indexOf(url, "?") < 0) {
            url += "?";
        }

        if (url.endsWith("?") == false)
            url += "&";

        if (!TextUtils.isEmpty(Global.sLoginParam.imei)) {
            url = url + "imei=" + Global.sLoginParam.imei;
        }
        if (!TextUtils.isEmpty(Global.sLoginParam.uid)) {
            url = url + "&uid=" + Global.sLoginParam.uid;
        }
        if (!TextUtils.isEmpty(Global.sLoginParam.mobile)) {
            url = url + "&mobile=" + Global.sLoginParam.mobile;
        }
        if (!TextUtils.isEmpty(Global.sLoginParam.cversion)) {
            url = url + "&cversion=" + Global.sLoginParam.cversion;
        }

        if (WebCloudApplication.isDebug) {
            String uuidRaw = UUID.randomUUID().toString();
            uuidRaw = uuidRaw.replaceAll("-", "");
            url = url + "&imsi=" + uuidRaw;
        } else {
            url = url + "&imsi=" + com.funlib.utily.Utily.getDeviceIMSI();
        }
        return url;
    }
}
