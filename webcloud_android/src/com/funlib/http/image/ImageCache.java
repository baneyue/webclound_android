package com.funlib.http.image;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

//import com.ct.lbs.widget.MulitPointTouchImageView;
import com.funlib.config.WorldSharedPreferences;
import com.funlib.file.FileUtily;
import com.funlib.http.HttpRequestImpl;
import com.funlib.http.ReturnData;
import com.funlib.imagefilter.ImageUtily;
import com.funlib.log.Log;

/**
 * 图片缓存。
 * 
 * 图片查找步骤： 1，从内存中查找 2，从本地文件中查找 3，从网络获取，网络获取图片成功后需要做缓存，以供下次使用
 * 
 * @author zoubangyue
 * 
 */
public class ImageCache {
    private static final String TAG = "ImageCache";
    
    private Context mContext;
    
    /**
     * 内存软引用。
     */
    private static HashMap<String, SoftReference<Bitmap>> sBitampPools = new HashMap<String, SoftReference<Bitmap>>();
    
    public static String sImageCachePath = "";
    
    /**文件最大保持的天数，定期把15天前的文件给清空掉，否则磁盘会膨胀*/
    public static final int FILE_EXPIRE_DAY = 15;
    
    public static final long FILE_EXPIRE_MILLIN = 3600 * 24 * FILE_EXPIRE_DAY * 60 * 1000;
    
    static {
        String appPath = FileUtily.getAppSDPath();
        if (appPath != null) {
            sImageCachePath = appPath + File.separator + "imgcache" + File.separator;
            FileUtily.mkDir(sImageCachePath);
        }
    }
    
    /**
     * 获取图片对应的hashcode
     * 
     * @return
     */
    public static String hashString(String imgUrl) {
        
        if (TextUtils.isEmpty(imgUrl) == true) {
            imgUrl = String.valueOf(System.currentTimeMillis());
        }
        
        return imgUrl.hashCode() + ".pic";
    }
    
    /**
     * 添加图片到内存缓存
     * 
     * @param imgUrl
     * @param bitmap
     */
    private static void addBitmap(String imgUrl, Bitmap bitmap) {
        if (sBitampPools == null) {
            sBitampPools = new HashMap<String, SoftReference<Bitmap>>();
        }
        if (bitmap == null || TextUtils.isEmpty(imgUrl))
            return;
        
        sBitampPools.put(hashString(imgUrl), new SoftReference<Bitmap>(bitmap));
    }
    
    /**
     * 从内存缓存读取图片
     * 
     * @param imgUrl
     * @return
     */
    private static Bitmap getBitmap(String imgUrl) {
        
        if (sBitampPools == null)
            return null;
        SoftReference<Bitmap> sr = sBitampPools.get(hashString(imgUrl));
        if (sr == null)
            return null;
        return sr.get();
    }
    
    /**
     * 清除内存图片缓存。
     */
    public static void clearCache(Context context) {
        if (sBitampPools != null) {
            sBitampPools.clear();
        }
    }
    
    /**
     * 清除sd磁盘中过期图片文件。
     * 
     * 每15天清理一次。
     */
    public static void clearExpireFile(Context context) {
        try {
            //判断是否存在此字段
            if (WorldSharedPreferences.checkKeyExists("lastClearDate")) {
                long lastClearDate = WorldSharedPreferences.getLong("lastClearDate");
                Log.d(TAG, String.valueOf(lastClearDate));
                final long currMill = System.currentTimeMillis();
                //15天之前清空的
                if (currMill - lastClearDate > FILE_EXPIRE_MILLIN) {
                    Log.e(TAG, String.valueOf("expire"));
                    WorldSharedPreferences.saveLong("lastClearDate", currMill);
                    File imgCacheDir = new File(sImageCachePath);
                    FileFilter filter = new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            long lastmillies = pathname.lastModified();
                            if (currMill - lastmillies > FILE_EXPIRE_MILLIN)
                                return true;
                            return false;
                        }
                    };
                    File files[] = imgCacheDir.listFiles(filter);
                    if (files != null) {
                        int cnt = files.length;
                        for (int i = 0; i < cnt; ++i) {
                            File f = files[i];
                            f.delete();
                        }
                    }
                }
            } else {
                WorldSharedPreferences.saveLong("lastClearDate", System.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 缓存图片
     * 
     * @param imgUrl
     * @param bitmap
     * @param bitmapBytes
     */
    public void storeCachedBitmap(String imgUrl, Bitmap bitmap, byte[] bitmapBytes) {
        try {
            // 缓存到内存
            addBitmap(imgUrl, bitmap);
            // 缓存到文件
            FileUtily.saveBytes(sImageCachePath + hashString(imgUrl), bitmapBytes);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            clearCache(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 在内存中查找
     * 
     * @return
     */
    public Bitmap lookupInMemory(String imgUrl) {
        //		Bitmap bmp = null;
        //		bmp = BitmapFactory.decodeByteArray(ImageUtil.decodeBitmap(imgUrl), 0, ImageUtil.decodeBitmap(imgUrl).length);  
        Bitmap bmp = getBitmap(imgUrl);
        return bmp;
    }
    
    /**
     * 在缓存文件中查找
     * 
     * @return
     */
    public Bitmap lookupInFiles(String imgUrl) {
        try {
            String filePath = sImageCachePath + hashString(imgUrl);
            File file = new File(filePath);
            if (file.exists()) {
                /*Date nowDate = new Date();
                Date createDate = new Date(file.lastModified());
                // 图片维持7天的有效期
                long differ = Math.abs((nowDate.getTime() - createDate.getTime()) / (24 * 3600 * 1000));
                if (differ >= 7)
                	return null;*/
                
                //Bitmap bmp = getBitmap(filePath);
                Bitmap bmp = null;
                bmp =
                    BitmapFactory.decodeByteArray(ImageUtil.decodeBitmap(filePath),
                        0,
                        ImageUtil.decodeBitmap(filePath).length);
                //缓存到内存
                addBitmap(imgUrl, bmp);
                return bmp;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 获取缓存图片. 延迟加载。
     * 
     * @param context
     * @param listener
     * @param target
     * @param imgUrl
     * @param params
     * @param forceFromNet 是否强制从网络拉取图片 
     */
    public Bitmap cacheImageLazy(Context context, final ImageCacheListener listener, final Object target,
        final String imgUrl, final int targetW, final int targetH, final Map<String, String> params,
        boolean forceFromNet) {
        
        this.mContext = context;
        try {
            new ImageCacheLazyTask().execute(listener, target, imgUrl, targetW, targetH, params, forceFromNet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 获取缓存图片. 延迟加载.从本地缓存或内存获取。
     * 
     * @param context
     * @param listener
     * @param target
     * @param imgUrl
     * @param params
     * @param forceFromNet 是否强制从网络拉取图片 
     */
    public Bitmap cacheImageLocalLazy(Context context, final ImageCacheListener listener, final Object target,
        final String imgUrl) {
        
        this.mContext = context;
        try {
            new ImageCacheLocalLazyTask().execute(listener, target, imgUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 改为延迟获取图片。 不管是通过网络还是通过缓存，目的是不阻塞用户操作。
     * 
     * @author zoubangyue
     */
    private class ImageCacheLazyTask extends AsyncTask<Object, Integer, Bitmap> {
        
        private ImageCacheListener listener;
        
        private Object target;
        
        private String imgUrl;
        
        private int targetW;
        
        private int targetH;
        
        private boolean forceFormNet;
        
        private Map<String, String> requestParams;
        
        @SuppressWarnings("unchecked")
        @Override
        protected Bitmap doInBackground(Object... params) {
            
            listener = (ImageCacheListener)params[0];
            target = params[1];
            imgUrl = (String)params[2];
            targetW = (Integer)params[3];
            targetH = (Integer)params[4];
            requestParams = ((Map<String, String>)params[5]);
            forceFormNet = (Boolean)params[6];
            
            Bitmap resultBmp = null;
            if (!forceFormNet) {
                // find memory 内存
                resultBmp = lookupInMemory(imgUrl);
                if (resultBmp != null) {
                    return resultBmp;
                }
                // find files
                resultBmp = lookupInFiles(imgUrl);
                if (resultBmp != null) {
                    return resultBmp;
                }
            }
            
            Log.d(TAG, "StartLoad:" + imgUrl);
            try {
                if (!TextUtils.isEmpty(imgUrl)) {
                    HttpRequestImpl request = new HttpRequestImpl(mContext);
                    ReturnData data = request.doGetDownloadRequest(imgUrl, requestParams, ReturnData.BYTEARRAY);
                    if (data.status == ReturnData.SC_OK && data.dataType == ReturnData.BYTEARRAY) {
                        byte[] bmpBytes = data.bytes;
                        if (bmpBytes != null)
                            resultBmp = ImageUtily.scaleBitmapFromByte(bmpBytes, targetW, targetH);
                    }
                    request.consumeContent();
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                //e.printStackTrace();
                Log.e(TAG, "ErrorLoad:" + imgUrl, e);
            }
            Log.d(TAG, "EndLoad:" + imgUrl);
            //从网络拉取得图片，需要缓存到内存和文件
            if (resultBmp != null) {
                storeCachedBitmap(imgUrl, resultBmp, ImageUtily.bitmap2Byte(resultBmp));
                //storeCachedBitmap(imgUrl, resultBmp, ImageUtil.decodeBitmap(imgUrl));
            }
            return resultBmp;
        }
        
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (target != null && bitmap != null) {
                if (target instanceof View) {
                    
                    View view = (View)target;
                    if (imgUrl.equals(view.getTag())) {
                        
                        if (target instanceof ImageView) {
                            
                            ImageView iv = (ImageView)target;
                            iv.setImageBitmap(bitmap);
                        } else if (target instanceof ImageButton) {
                            
                            ImageButton iv = (ImageButton)target;
                            //iv.setBackgroundColor(Color.WHITE);
                            iv.setImageBitmap(bitmap);
                        } else if (target instanceof View) {
                            
                            Drawable drawable = new BitmapDrawable(bitmap);
                            view.setBackgroundDrawable(drawable);
                        }/* else if (target instanceof MulitPointTouchImageView) {
                            MulitPointTouchImageView iv = (MulitPointTouchImageView)target;
                            iv.setImageBitmap(bitmap);
                        }*/
                    }
                }
                
            }
            int status = ImageCacheStatus.SUCCESS;
            if (bitmap == null) {
                status = ImageCacheStatus.FAIL;
            }
            if (listener != null) {
                listener.getImageFinished(status, target, bitmap, ImageCache.this);
            }
        }
        
    }
    
    /**
     * 本地延迟加载图片。
     * 
     * @author zoubangyue
     */
    private class ImageCacheLocalLazyTask extends AsyncTask<Object, Integer, Bitmap> {
        
        private ImageCacheListener listener;
        
        private Object target;
        
        private String imgUrl;
        
        @SuppressWarnings("unchecked")
        @Override
        protected Bitmap doInBackground(Object... params) {
            
            listener = (ImageCacheListener)params[0];
            target = params[1];
            imgUrl = (String)params[2];
            
            Bitmap resultBmp = null;
            // find memory 内存
            resultBmp = lookupInMemory(imgUrl);
            if (resultBmp != null) {
                return resultBmp;
            }
            // find files
            resultBmp = lookupInFiles(imgUrl);
            if (resultBmp != null) {
                return resultBmp;
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (target != null && bitmap != null) {
                if (target instanceof View) {
                    
                    View view = (View)target;
                    if (imgUrl.equals(view.getTag())) {
                        
                        if (target instanceof ImageView) {
                            
                            ImageView iv = (ImageView)target;
                            iv.setImageBitmap(bitmap);
                        } else if (target instanceof ImageButton) {
                            
                            ImageButton iv = (ImageButton)target;
                            //iv.setBackgroundColor(Color.WHITE);
                            iv.setImageBitmap(bitmap);
                        } else if (target instanceof View) {
                            
                            Drawable drawable = new BitmapDrawable(bitmap);
                            view.setBackgroundDrawable(drawable);
                        }/* else if (target instanceof MulitPointTouchImageView) {
                            MulitPointTouchImageView iv = (MulitPointTouchImageView)target;
                            iv.setImageBitmap(bitmap);
                        }*/
                    }
                }
                
            }
            int status = ImageCacheStatus.SUCCESS;
            if (bitmap == null) {
                status = ImageCacheStatus.FAIL;
            }
            if (listener != null) {
                listener.getImageFinished(status, target, bitmap, ImageCache.this);
            }
        }
        
    }
}
