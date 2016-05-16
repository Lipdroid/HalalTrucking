package com.halaltrucking.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by WebHawks IT on 2/18/2016.
 */
public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private MarkerObject markerObject = new MarkerObject();

    public MyItem(double lat, double lng, MarkerObject markerObject) {
        mPosition = new LatLng(lat, lng);
        this.markerObject = markerObject;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public MarkerObject getMarkerObj() {
        return this.markerObject;
    }
}

