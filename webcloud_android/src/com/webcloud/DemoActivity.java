package com.webcloud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.funlib.http.HttpRequestImpl;
import com.funlib.http.request.RequestStatus;
import com.funlib.http.request.Requester;
import com.funlib.json.JsonFriend;
import com.funlib.log.Log;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.webcloud.component.NewLoadingDialog;
import com.webcloud.component.WaitCancelListener;
import com.webcloud.define.HttpUrlImpl;
import com.webcloud.manager.client.ImageCacheManager;
import com.webcloud.utily.JsonResponse;
import com.webcloud.utily.SetRoundBitmap;

public class DemoActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    /** 
     * 加载网络数据的写法。
     * 
     * <br>1.启用加载进度条，固定写法
     * <br>2.启用加载进度条的取消功能，取消接口请求，固定写法
     * <br>3.Activity或其他组件需要继承自RequestListener接口，也可以在当前处直接生成回到实例
     * <br>4.使用{@link HttpUrlImpl}，见类代码。在枚举中定义接口和返回接口url.
     * <br>上面两点不需要关心释放进度条，在BaseActivity里面已经做了这件事。
     *
     */
    public void testLoadData() {
        Requester req = new Requester(this);
        Map<String, String> params = new HashMap<String, String>();
        /**如果需要取消请求，请实现等待取消的回调方法*/
        WaitCancelListener lisr = new WaitCancelListener(req) {
            @Override
            public void onWaittingCancel() {
                if (obj instanceof Requester) {
                    ((Requester)obj).cancel();
                }
            }
        };
        /**如果需要加载进度条，请创建加载进度条*/
        //解析加载进度框，若存在则先释放再重新创建
        if (loadingDialog == null) {
            loadingDialog = new NewLoadingDialog(this, lisr);
        } else {
            loadingDialog.dismiss();
            loadingDialog = new NewLoadingDialog(this, lisr);
        }
        req.request(this, HttpUrlImpl.V1.A, HttpUrlImpl.V1.A.getUrl(), params, HttpRequestImpl.POST, false);
    }
    
    /** 
     * 使用开源缓存图片包缓存图片。
     * 
     * 1.使用时注意尽量给控件设置高度或宽度，只设置一个属性，工具会根据图片高宽进行等比例缩放。这样可以尽最大可能的降低图片在缓存中的大小，避免原图过大图片
     * 过多造成内存吃紧
     * 2.可以处理加载图片的回调
     * 3.displayImage方法必须传递一个ImageView实例，如果没有布局的id,那么就手动new一个临时的实例。
     * 4.可以额外的生存圆角等二次处理的图片
     */
    public void testLoadPic() {
        ImageLoader imgLoader = mgr.imgCacheMgr.getImageLoader();
        String picUrl = HttpUrlImpl.IMG_URL + "***.jpg";
        ImageView ivPic = new ImageView(this);//如果是主动实例化imageView,带上宽度和高度效果较好
        imgLoader.displayImage(picUrl,
            ivPic,
            mgr.imgCacheMgr.getOptions(ImageCacheManager.CACHE_MODE_CACHE),
            new SimpleImageLoadingListener() {
                
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //可以处理回调方法，比如在此处生存圆角图片。如下：
                    Bitmap bm = SetRoundBitmap.SD(loadedImage, 51);
                    //如果不想处理也可直接传递null,此外其他几个回调方法也可按需求使用
                    //super.onLoadingComplete(imageUri, view, loadedImage);
                }
            });
    }
    
    @Override
    public void requestStatusChanged(int statusCode, HttpUrlImpl requestId, String responseString,
        Map<String, String> requestParams) {
        //需要调用父类的实现
        super.requestStatusChanged(statusCode, requestId, responseString, requestParams);
        if (statusCode == RequestStatus.SUCCESS) {
            try {
                /**解析json数据：首先要和web端约束好json数据的格式，解析json使用alibaba.jar包的工具，性能优越，api较好用方便
                 * 1.首先在各模块中定义与web接口对应的实体bean,放在各模快的model文件夹
                 * 2.创建JsonFriend实例，对对应的实体进行方便的解析
                 * 
                 * 
                 * */
                JSONObject json = JsonFriend.parseJSONObject(responseString);
                String retcode = json.getString(JsonResponse.RET_CODE);
                JSONObject retdata = json.getJSONObject(JsonResponse.RET_DATA);
                String retmsg = json.getString(JsonResponse.RET_MSG);
                HttpUrlImpl.V1 v1 = (HttpUrlImpl.V1)requestId;
                switch (v1) {
                // 取得最新一页的主题数据，包含分页信息，缓存到缓存文件中
                    case A: {
                        /**
                         * {"retcode":"0","retmsg":"成功","retdata":
                         * {"datalist":[{"score"
                         * :"593","notecount":"0","concerned":"0"}]}}]
                         */
                        if (JsonResponse.CODE_SUCC.equals(retcode)) {
                            JsonFriend<Object> JSF = new JsonFriend<Object>(Object.class);
                            List<Object> userinfoList = JSF.parseArray(retdata.getString("datalist"));
                            JSONObject jsobj = JsonFriend.parseJSONObject("jsongdata");
                        }
                    }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /** 
     * 日志打印统一使用，funlib下的Log,该log做了优化处理。
     * 发布线上版本时可以把调试级别的日志给屏蔽。
     * 
     * 不要使用system.out.println()；
     */
    public void testLog(){
        Log.d("tag", "不要使用system.out.println()");
    }
    
}
