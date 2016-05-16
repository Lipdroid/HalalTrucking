package com.halaltrucking;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.utils.CorrectSizeUtil;
import com.halaltrucking.utils.GlobalUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    GoogleMap mGoogleMap;
    Point newpPoint;
    private CorrectSizeUtil mCorrectSize = null;
    LocationManager mlocationManager;
    String markerId = null;
    MarkerObject markerObjectfinal = null;
    ImageView marker_img = null;
    Button submit = null;
    LinearLayout error_ln =null;
    Marker marker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        marker_img = (ImageView) findViewById(R.id.marker);
        submit = (Button) findViewById(R.id.submit);
        error_ln = (LinearLayout) findViewById(R.id.error_ln);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            markerId = null;
        } else {
            markerId = extras.getString("id");
            for (MarkerObject markerObject : GlobalUtils.all_marker) {
                if (markerObject.getId().equals(markerId)) {
                    markerObjectfinal = markerObject;
                }
            }
        }

        Display display = getWindowManager().getDefaultDisplay();
        newpPoint = new Point();
        newpPoint.x = display.getWidth() / 2;
        newpPoint.y = display.getHeight() / 2;
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment
            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            // Getting Google Map
            mGoogleMap = fragment.getMap();

            if (markerId != null) {
                marker_img.setVisibility(View.GONE);
                submit.setVisibility(View.GONE);
                LatLng latlng = new LatLng(Double.parseDouble(markerObjectfinal.getLat()), Double.parseDouble(markerObjectfinal.getLng()));
                marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title(markerObjectfinal.getArtist_name())
                        .snippet(markerObjectfinal.getDescription())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 7));
                //mMap is an instance of GoogleMap
                mGoogleMap.setOnCameraChangeListener(getCameraChangeListener());

            } else {
                mGoogleMap.setMyLocationEnabled(true);
                Location location = getLastKnownLocation();
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
                }
            }
        }

        //Init variable
        mCorrectSize = CorrectSizeUtil.getInstance(this);
        mCorrectSize.correctSize();
    }

    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                Log.d("Zoom", "Zoom: " + position.zoom);
                if(markerId != null){
                    if(!markerObjectfinal.getArt_type().equals("street_art")) {
                        if (position.zoom <= 7) {
                            error_ln.setVisibility(View.GONE);
                            marker.setVisible(true);
                        } else {
                            error_ln.setVisibility(View.VISIBLE);
                            marker.setVisible(false);
                        }
                    }
                }
            }
        };
    }

    private Location getLastKnownLocation() {
        LocationManager mlocationManager = (LocationManager) this.getApplicationContext()
                .getSystemService(this.LOCATION_SERVICE);
        List<String> providers = mlocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mlocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void submit(View v) {
        LatLng newCenterLatLng = mGoogleMap.getProjection().fromScreenLocation(newpPoint);
        Intent intent = new Intent();
        intent.putExtra("lat", newCenterLatLng.latitude + "");
        intent.putExtra("lng", newCenterLatLng.longitude + "");
        setResult(2, intent);
        finish();//finishing activity
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (markerId != null)
            return super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "Please Select a Place..",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }
}
