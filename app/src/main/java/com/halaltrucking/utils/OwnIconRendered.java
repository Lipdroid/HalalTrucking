package com.halaltrucking.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.halaltrucking.R;
import com.halaltrucking.models.MyItem;

/**
 * Created by WebHawks IT on 5/16/2016.
 */
public class OwnIconRendered extends DefaultClusterRenderer<MyItem> {
    Context context = null;
    public OwnIconRendered(Context context, GoogleMap map,
                           ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        if(item.getMarkerObj().getArt_type().equals("street_art")){
            BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_default);
            markerOptions.icon(marker);
        }else if(item.getMarkerObj().getArt_type().equals("train_art")){
            BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_default);
            markerOptions.icon(marker);
        }else if(item.getMarkerObj().getArt_type().equals("wall_art")){
            BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_default);
            markerOptions.icon(marker);
        }

        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
