package com.jkydjk.healthier.clock.util;

import java.io.File;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FakeBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ImageLoaderUtil {
  
  public static DisplayImageOptions getDisplayImageOptions(Context context){
    
    DisplayImageOptions options = new DisplayImageOptions.Builder()
    .cacheInMemory()
    .cacheOnDisc()
    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
    .displayer(new FakeBitmapDisplayer())
    .build();
    
    return options;
  }

  /**
   * 
   * @param context
   * @return
   */
  public static ImageLoaderConfiguration getImageLoaderConfiguration(Context context) {

    File cacheDir = StorageUtils.getOwnCacheDirectory(context, "Healthier/Image/Cache");
    
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
    
    .denyCacheImageMultipleSizesInMemory()
    .discCacheFileNameGenerator(new Md5FileNameGenerator())
//    .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75) // Can slow ImageLoader, use it carefully (Better don't use it)
    .discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
    
    .enableLogging() // Not necessary in common
    
    .imageDownloader(new ExtendedImageDownloader(context))
//    .imageDownloader(new URLConnectionImageDownloader(5 * 1000, 20 * 1000)) // connectTimeout (5 s), readTimeout (20 s)

    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation
    .memoryCacheSize(2 * 1024 * 1024) // 2 Mb
    .memoryCacheExtraOptions(450, 450) // max width, max height
    
    .tasksProcessingOrder(QueueProcessingType.LIFO)
    .threadPriority(Thread.NORM_PRIORITY - 2)
    
    .build();
    
    //ImageLoaderConfiguration.createDefault(context)
    
    return config;
  }

}
