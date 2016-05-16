package com.halaltrucking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.utils.GlobalUtils;
import com.halaltrucking.utils.MultipleScreen;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class MarkerDetails extends AppCompatActivity {
    String markerId = null;
    MarkerObject markerObjectfinal;
    TextView location, date, type, artist_name, description;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_details);
        location = (TextView) findViewById(R.id.tv_location);
        date = (TextView) findViewById(R.id.date);
        type = (TextView) findViewById(R.id.tv_type);
        artist_name = (TextView) findViewById(R.id.tv_title);
        description = (TextView) findViewById(R.id.tv_des);
        imageView = (ImageView) findViewById(R.id.image_upload);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            markerId = null;
        } else {
            markerId = extras.getString("id");
        }

        for (MarkerObject markerObject : GlobalUtils.all_marker) {
            if (markerObject.getId().equals(markerId)) {
                markerObjectfinal = markerObject;
            }
        }
        date.setText(markerObjectfinal.getUpload_date());
        type.setText(markerObjectfinal.getArt_type());
        artist_name.setText(markerObjectfinal.getArtist_name());
        description.setText(markerObjectfinal.getDescription());
        GlobalUtils.sImageLoader.displayImage(markerObjectfinal.getImage_url(),imageView, GlobalUtils.sOptForImgLoader, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(markerObjectfinal.getLat()), Double.parseDouble(markerObjectfinal.getLng()), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            location.setText(address + "," + city);
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void map(View v){
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("id", markerObjectfinal.getId());
        startActivity(i);
    }
}
