package com.halaltrucking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.halaltrucking.utils.ConstantURLs;
import com.halaltrucking.utils.CorrectSizeUtil;
import com.halaltrucking.utils.JSONParser;
import com.halaltrucking.utils.SCImageUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddMarkerActivity extends AppCompatActivity implements View.OnClickListener {
    private CorrectSizeUtil mCorrectSize = null;
    // Spinner element
    private Spinner spinner = null;
    private Button submit = null;
    private Button capture = null;
    private ImageView set_image = null;
    private EditText title = null;
    private EditText description = null;
    private Context mContext = null;
    public static final int TYPE_UPLOAD_CAMPUS_WORK_PHOTO = 999;
    private Bitmap image = null;
    private String lat = null;
    private String lng = null;
    private TextView loc = null;
    int success = 0;
    ArrayList<Map.Entry<String, Bitmap>> bitmapParams = new ArrayList<Map.Entry<String, Bitmap>>();
    ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject json = new JSONObject();
    ArrayList<Object> listParams = new ArrayList<Object>();
    String str_title = null;
    String str_description = null;
    String str_type = "street_art";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            lat = data.getStringExtra("lat");
            lng = data.getStringExtra("lng");

            Double lati = Double.parseDouble(lat);
            Double lngi = Double.parseDouble(lng);
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(lati, lngi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                loc.setText(address + "," + city);
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == TYPE_UPLOAD_CAMPUS_WORK_PHOTO) {
                InputStream stream = null;
                Bitmap bitmap = null;

                try {
                    Uri uri = data.getData();
                    Bundle bundle = data.getExtras();
                    if (uri == null) {
                        // case nexus device
                        Bitmap imageBitmap = (Bitmap) bundle.get("data");
                        // mImgProfile.setImageBitmap(imageBitmap);
                        bitmap = imageBitmap;
                    } else {
                        stream = mContext.getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(stream);

                        String absPath = SCImageUtils.getPath(mContext, uri);
                        bitmap = SCImageUtils.rotateBitmap(bitmap, Uri.parse(absPath));
                    }
                    // thumb bitmap
                    int bmHeight = bitmap.getHeight();
                    int bmWith = bitmap.getWidth();

                    float ratio = bmWith * 1.0f / bmHeight;

                    Bitmap thumbBitmap = SCImageUtils.getBitmapThumb(bitmap, 1080, Math.round(1080 / ratio));
                    image = thumbBitmap;
                    set_image.setImageBitmap(thumbBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        mContext = this;
        spinner = (Spinner) findViewById(R.id.spinner);
        submit = (Button) findViewById(R.id.btn_submit);
        capture = (Button) findViewById(R.id.btn_camera);
        set_image = (ImageView) findViewById(R.id.image_upload);
        title = (EditText) findViewById(R.id.et_title);
        description = (EditText) findViewById(R.id.et_des);
        loc = (TextView) findViewById(R.id.tv_location);
        // Spinner Drop down elements
        final List<String> categories = new ArrayList<String>();
        categories.add("street_art");
        categories.add("train_art");
        categories.add("wall_art");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_type = categories.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        Location location = getLastKnownLocation();
        if (location != null) {
            lat = location.getLatitude() + "";
            lng = location.getLongitude() + "";
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                loc.setText(address + "," + city);
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Init variable
        mCorrectSize = CorrectSizeUtil.getInstance(this);
        mCorrectSize.correctSize();

        submit.setOnClickListener(this);
        capture.setOnClickListener(this);

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

    public void map(View v) {
        Intent intent = new Intent(AddMarkerActivity.this, MapActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                Intent intentChooseImage = null;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setType("image/*");

                if (Build.VERSION.SDK_INT < 19) {
                    intentChooseImage = new Intent();
                    intentChooseImage.setAction(Intent.ACTION_GET_CONTENT);
                    intentChooseImage.setType("image/*");

                } else {
                    intentChooseImage = new Intent(Intent.ACTION_GET_CONTENT);
                    intentChooseImage.addCategory(Intent.CATEGORY_OPENABLE);
                    intentChooseImage.setType("image/*");
                }
                Intent chooserIntent = Intent.createChooser(takePictureIntent, getResources().getString(R.string.dialog_choose_image_title));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                startActivityForResult(chooserIntent, TYPE_UPLOAD_CAMPUS_WORK_PHOTO);

                break;
            case R.id.btn_submit:
                if (image != null) {
                    str_title = title.getText().toString();
                    str_description = description.getText().toString();
                    requestForAddMarker();
                } else {
                    Toast.makeText(this, "Please Select an Image", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void requestForAddMarker() {

        new RequestForSavelMarker().execute();
    }


    /**
     * Background Async Task to process the recharge
     */
    class RequestForSavelMarker extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddMarkerActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("tag", "storeMarker"));
            params.add(new BasicNameValuePair("artist_name", str_title));
            params.add(new BasicNameValuePair("art_type", str_type));
            params.add(new BasicNameValuePair("description", str_description));
            params.add(new BasicNameValuePair("lat", lat));
            params.add(new BasicNameValuePair("lng", lng));

            // create hash map to save avatar bitmap
            Map.Entry<String, Bitmap> hashIcon = new Map.Entry<String, Bitmap>() {

                @Override
                public String getKey() {
                    // TODO Auto-generated method stub
                    return "image_url";
                }

                @Override
                public Bitmap getValue() {
                    // TODO Auto-generated method stub
                    return image;
                }

                @Override
                public Bitmap setValue(Bitmap object) {
                    // TODO Auto-generated method stub
                    return image;
                }
            };

            bitmapParams.add(hashIcon);
            listParams.add(params);
            listParams.add(bitmapParams);
            // getting JSON Object
            // Note that create product url accepts POST method
            json = jsonParser.makeHttpRequest(ConstantURLs.base_url, "POST", listParams);

            if (json != null) {

                try {
                    if (json.getInt("success") == 1) {
                        success = 1;
                    }
                } catch (JSONException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }

                Log.d("Response", json.toString());

                // check for success tag
                try {
                    int errorCode = json.getInt("error");

                    if (errorCode == 0) {
                        Log.d("Result", json.getString("error_msg"));
                    } else {
                        // failed to create product
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (success == 1) {
                Toast.makeText(AddMarkerActivity.this, "Successfully added the marker", Toast.LENGTH_LONG).show();
                success = 0;
            }
            finish();
        }

    }


}
