package com.webcloud.manager.client;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.baidu.android.common.logging.Log;
import com.funlib.imagefilter.ImageUtily;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.webcloud.WebCloudApplication;
import com.webcloud.define.HttpUrlImpl;

/**
 * 图片缓存管理器。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-13]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ImageCacheManager {
    public static final int CACHE_MODE_NOCACHE = 0;
    
    public static final int CACHE_MODE_CACHE = 1;
    
    public static final int CACHE_MODE_MEMORY = 2;
    
    public static final int CACHE_MODE_DISC = 3;
    
    Context ctx;
    
    ImageLoaderConfiguration config;
    
    public ImageCacheManager(Context ctx) {
        this.ctx = WebCloudApplication.getInstance();
        /*4. memoryCacheSize(...) and memoryCache(...) settings overlap each other. Use only one of them for one configuration object.
        这两个参数会互相覆盖，所以在Configuration中使用一个就好了
        5. discCacheSize(...), discCacheFileCount(...) and discCache(...) settings overlap each other, using only one of them for one configuration object.
        这三个参数会互相覆盖，只使用一个*/
        config =
            new ImageLoaderConfiguration.Builder(this.ctx).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                //.discCacheExtraOptions(1990, 1080, CompressFormat.PNG, 95, null)
                .memoryCacheExtraOptions(540, 960)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(getOptions(CACHE_MODE_CACHE))
                //.memoryCache(new WeakMemoryCache())
                .memoryCacheSize(10 * 1024)
                .memoryCacheSizePercentage(65)
                .discCacheFileCount(200)
                .threadPoolSize(5)
                .build();
        ImageLoader.getInstance().init(config);
    }
    
    public ImageLoader getImageLoader() {
        return ImageLoader.getInstance();
    }
    
    public DisplayImageOptions getOptions(int flag) {
        switch (flag) {
            case CACHE_MODE_NOCACHE:
                return new DisplayImageOptions.Builder().cacheInMemory(false)
                    .cacheOnDisc(false)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
            case CACHE_MODE_MEMORY:
                return new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(false)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
            case CACHE_MODE_DISC:
                return new DisplayImageOptions.Builder().cacheInMemory(false)
                    .cacheOnDisc(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
            default:
                return new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
        }
    }
    
    public static final int BITMAP_OPTION_ARGB_8888 = 0;
    
    public static final int BITMAP_OPTION_RGB_565 = 1;
    
    /** 
     * 获取位图配置。
     *
     * @param bitmapOptionFlag
     * @return
     */
    public BitmapFactory.Options getBitmapOptions(int bitmapOptionFlag){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;// 允许可清除
        options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
        if(bitmapOptionFlag == 1){
            options.inPreferredConfig = Config.RGB_565;
        } else {
            options.inPreferredConfig = Config.ARGB_8888;
        }
        return options;
    }
    
    /** 
     * 从disc加载处理好的pic.
     *
     * @return
     */
    public Bitmap loadPicFromDiscCache(String filename,BitmapFactory.Options options) {
        if (TextUtils.isEmpty(filename))
            return null;
        filename = HttpUrlImpl.IMG_URL + "/" + filename;
        File file = this.getImageLoader().getDiscCache().get(filename);
        Log.d("ImageCacheManager", file + "");
        if (file != null && file.exists()) {
            return BitmapFactory.decodeFile(file + "", options);
        }
        return null;
    }
    
    /** 
     * 保存图片到disc.
     *
     * @param filename 普通文件名，如vehicle_bg
     * @param bm
     * @throws IOException
     */
    public void savePicToDiscCache(String filename, Bitmap bm)
        throws IOException {
        if (TextUtils.isEmpty(filename))
            return;
        filename = HttpUrlImpl.IMG_URL + "/" + filename;
        //把图片存储到缓存磁盘中
        Md5FileNameGenerator md5 = new Md5FileNameGenerator();
        String md5filename = md5.generate(filename);
        String filepath = StorageUtils.getIndividualCacheDirectory(ctx) + "/" + md5filename;
        ImageUtily.saveBitmapToFile2(filepath, bm);
        this.getImageLoader().getDiscCache().put(filename, new File(filepath));
    }
}
