package com.halaltrucking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.halaltrucking.R;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.utils.GlobalUtils;

/**
 * Created by WebHawks IT on 2/17/2016.
 */
public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private View view;
    Context mContext = null;

    public CustomInfoAdapter(Context context) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflator.inflate(R.layout.custom_info_window,
                null);
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        MarkerObject markerObject = null;

        final ImageView image = ((ImageView) view.findViewById(R.id.badge));
        final TextView title = ((TextView) view.findViewById(R.id.title));
        final TextView date = ((TextView) view.findViewById(R.id.date));

        final TextView des = ((TextView) view.findViewById(R.id.snippet));

        if (marker.getId() != null && GlobalUtils.markers != null && GlobalUtils.markers.size() > 0) {
            if (GlobalUtils.markers.get(marker.getId()) != null &&
                    GlobalUtils.markers.get(marker.getId()) != null) {
                markerObject = GlobalUtils.markers.get(marker.getId());
                title.setText(markerObject.getArtist_name());
                des.setText(markerObject.getDescription());
                date.setText(markerObject.getUpload_date());
                image.setImageBitmap(GlobalUtils.images.get(markerObject.getImage_url()));
            }else{
                return null;
            }
        }


        return view;


    }
}

