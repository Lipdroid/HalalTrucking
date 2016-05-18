package com.halaltrucking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.utils.CorrectSizeUtil;
import com.halaltrucking.utils.GlobalUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    private Toolbar toolbar;
    GoogleMap mGoogleMap;
    Point newpPoint;
    private CorrectSizeUtil mCorrectSize = null;
    LocationManager mlocationManager;
    String markerId = null;
    String type = null;
    MarkerObject markerObjectfinal = null;
    ImageView marker_img = null;
    Button submit = null;
    LinearLayout error_ln = null;
    Marker marker = null;
    Double user_lat = null;
    Double user_lng = null;
    private List<LatLng> pontos;
    ProgressDialog progressDialog, dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_map);

        //setting the toolbars
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        marker_img = (ImageView) findViewById(R.id.marker);
        submit = (Button) findViewById(R.id.submit);
        error_ln = (LinearLayout) findViewById(R.id.error_ln);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            markerId = null;
        } else {
            markerId = extras.getString("id");
            type = extras.getString("type");

            if (type != null) {

            } else {

            }

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

            if (markerId != null && type == null) {
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
                // mGoogleMap.setOnCameraChangeListener(getCameraChangeListener());

            } else {
                try {
                    mGoogleMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    Toast.makeText(this, "Something wrong with the GPS", Toast.LENGTH_LONG).show();
                }

                Location location = getLastKnownLocation();
                if (location != null) {
                    user_lat = location.getLatitude();
                    user_lng = location.getLongitude();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
                }

                if (type != null) {
                    marker_img.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                    new GetDirection().execute();
                } else {

                }

            }
        }

        //Init variable
        mCorrectSize = CorrectSizeUtil.getInstance(this);
        mCorrectSize.correctSize();
    }
//Remove the zooming restriction
//    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
//    {
//        return new GoogleMap.OnCameraChangeListener()
//        {
//            @Override
//            public void onCameraChange(CameraPosition position)
//            {
//                Log.d("Zoom", "Zoom: " + position.zoom);
//                if(markerId != null){
//                    if(!markerObjectfinal.getArt_type().equals("Food")) {
//                        if (position.zoom <= 7) {
//                            error_ln.setVisibility(View.GONE);
//                            marker.setVisible(true);
//                        } else {
//                            error_ln.setVisibility(View.VISIBLE);
//                            marker.setVisible(false);
//                        }
//                    }
//                }
//            }
//        };
//    }

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
        overridePendingTransition(R.anim.anim_nothing,
                R.anim.anim_slide_out_bottom);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (markerId != null) {
            finish();
            overridePendingTransition(R.anim.anim_nothing,
                    R.anim.anim_slide_out_bottom);
            return super.onKeyDown(keyCode, event);

        }
        if (keyCode == KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "Please Select a Place..",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }


    class GetDirection extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MapActivity.this);
            dialog.setMessage("Drawing the route, please wait!");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        protected String doInBackground(String... args) {
            String origin = user_lat + ","
                    + user_lng;
            String destination = markerObjectfinal.getLat() + ","
                    + markerObjectfinal.getLng();
            String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin="
                    + origin
                    + "&destination="
                    + destination
                    + "&sensor=true&mode=driving";
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(stringUrl);
                HttpURLConnection httpconn = (HttpURLConnection) url
                        .openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(httpconn.getInputStream()),
                            8192);
                    String strLine = null;

                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }

                String jsonOutput = response.toString();

                JSONObject jsonObject = new JSONObject(jsonOutput);

                // routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                // Grab the first route
                JSONObject route = routesArray.getJSONObject(0);

                JSONObject poly = route.getJSONObject("overview_polyline");
                String polyline = poly.getString("points");
                pontos = decodePoly(polyline);

            } catch (Exception e) {

            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            try {
                for (int i = 0; i < pontos.size() - 1; i++) {
                    LatLng src = pontos.get(i);
                    LatLng dest = pontos.get(i + 1);
                    try {
                        // here is where it will draw the polyline in your map
                        Polyline line = mGoogleMap
                                .addPolyline(new PolylineOptions()
                                        .add(new LatLng(src.latitude, src.longitude),
                                                new LatLng(dest.latitude,
                                                        dest.longitude)).width(10)
                                        .color(Color.parseColor("#03A9F4")).geodesic(true));


                    } catch (NullPointerException e) {
                        Log.e("Error",
                                "NullPointerException onPostExecute: "
                                        + e.toString());
                    } catch (Exception e2) {
                        Log.e("Error", "Exception onPostExecute: " + e2.toString());
                    }

                }
                Marker destination = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(markerObjectfinal.getLat()), Double.parseDouble(markerObjectfinal.getLng())))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default))
                        .title("DATE: " + markerObjectfinal.getArtist_name())
                        .snippet(" DESCRIPTION: " + markerObjectfinal.getDescription()));
            }catch (NullPointerException e) {
                Log.e("Error",
                        "NullPointerException onPostExecute: "
                                + e.toString());
                Toast.makeText(MapActivity.this,"No Pathe found",Toast.LENGTH_LONG).show();
            } catch (Exception e2) {
                Log.e("Error", "Exception onPostExecute: " + e2.toString());
                Toast.makeText(MapActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();


        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
