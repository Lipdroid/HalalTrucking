package com.halaltrucking.utils;

import android.graphics.Bitmap;

import com.google.maps.android.clustering.ClusterManager;
import com.halaltrucking.R;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.models.MyItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by WebHawks IT on 2/17/2016.
 */
public class GlobalUtils {
    public static List<MarkerObject> all_marker = new ArrayList<MarkerObject>();
    public static List<MarkerObject> street_art = new ArrayList<MarkerObject>();
    public static List<MarkerObject> train_art = new ArrayList<MarkerObject>();
    public static List<MarkerObject> wall_art = new ArrayList<MarkerObject>();
    public static List<String> artist_name = new ArrayList<String>();
    public static Hashtable<String, MarkerObject> markers = new Hashtable<String, MarkerObject>();
    public static Hashtable<String, Bitmap> images = new Hashtable<String, Bitmap>();
    public static ClusterManager<MyItem> mClusterManager = null;
    public static String show_marker_types = "All";

    public static List<MarkerObject> searched_marker = new ArrayList<MarkerObject>();

    public static DisplayImageOptions sOptForImgLoader = new DisplayImageOptions.Builder()
            .showImageOnLoading(android.R.color.white).showImageForEmptyUri(R.color.common_gray)
            .showImageOnFail(R.color.common_gray).bitmapConfig(Bitmap.Config.RGB_565)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .resetViewBeforeLoading(true)
            .displayer(new FadeInBitmapDisplayer(400))//500 is the fade
            .build();

    // ImageLoader instance for all activity
    public static ImageLoader sImageLoader = ImageLoader.getInstance();
    // Pause loading image when user scroll of fling listview, gridview,...
    public static PauseOnScrollListener sPauseOnScrollListener = new PauseOnScrollListener(
            sImageLoader, false, true);
}
