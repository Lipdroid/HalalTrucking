package com.halaltrucking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.halaltrucking.fragments.ListFragment;
import com.halaltrucking.fragments.MapFragment;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.utils.ConstantURLs;
import com.halaltrucking.utils.CorrectSizeUtil;
import com.halaltrucking.utils.GlobalUtils;
import com.halaltrucking.utils.JSONParser;
import com.halaltrucking.utils.SCImageUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CorrectSizeUtil mCorrectSize = null;
    ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject json = new JSONObject();
    public static MapFragment mapFragment = new MapFragment();
    public static ListFragment listFragment = new ListFragment();
    ArrayList<Object> listParams = new ArrayList<Object>();
    ArrayList<Map.Entry<String, Bitmap>> bitmapParams = new ArrayList<Map.Entry<String, Bitmap>>();
    ArrayList<NameValuePair> nameValueParams = new ArrayList<NameValuePair>();
    private Button street_art = null;
    private Button train_art = null;
    private Button wall_art = null;
    AlertDialog alertDialog;
    public boolean from_refresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        new SCImageUtils(MainActivity.this);
        street_art = (Button) findViewById(R.id.content_btn_street);
        train_art = (Button) findViewById(R.id.content_btn_train);
        wall_art = (Button) findViewById(R.id.content_btn_wall);

        //request for retrieving all the markers
        new RequestForAllMarkers().execute();
        //setting the toolbars
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.truck_logo);
        //getSupportActionBar().setTitle("Halal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        street_art.setOnClickListener(this);
        train_art.setOnClickListener(this);
        wall_art.setOnClickListener(this);

        //Init variable
        mCorrectSize = CorrectSizeUtil.getInstance(this);
        mCorrectSize.correctSize();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            GlobalUtils.all_marker.clear();
            GlobalUtils.train_art.clear();
            GlobalUtils.wall_art.clear();
            GlobalUtils.street_art.clear();
            GlobalUtils.artist_name.clear();
            GlobalUtils.images.clear();
            GlobalUtils.mClusterManager.clearItems();
            GlobalUtils.show_marker_types = "All";
            new RequestForAllMarkers().execute();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapFragment, "Map");
        adapter.addFragment(listFragment, "List");
        viewPager.setAdapter(adapter);
    }

    public void startAddActivity() {
        Intent intent = new Intent(MainActivity.this, AddMarkerActivity.class);
        startActivityForResult(intent, 3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_btn_street:
                GlobalUtils.show_marker_types = "street_art";
                if (mapFragment != null) {
                    mapFragment.updateMap("street_art");
                }
                if (listFragment != null) {
                    listFragment.updateList("street_art");
                }
                break;
            case R.id.content_btn_train:
                GlobalUtils.show_marker_types = "train_art";
                if (mapFragment != null) {
                    mapFragment.updateMap("train_art");
                }
                if (listFragment != null) {
                    listFragment.updateList("train_art");
                }
                break;
            case R.id.content_btn_wall:
                GlobalUtils.show_marker_types = "wall_art";
                if (mapFragment != null) {
                    mapFragment.updateMap("wall_art");
                }
                if (listFragment != null) {
                    listFragment.updateList("wall_art");
                }
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                //your about code here
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);

                // set title
                alertDialogBuilder.setTitle("About");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                alertDialog.dismiss();
                            }
                        });

                // create alert dialog
                alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                return true;
            case R.id.refresh:
                //your code here
                GlobalUtils.all_marker.clear();
                GlobalUtils.train_art.clear();
                GlobalUtils.wall_art.clear();
                GlobalUtils.street_art.clear();
                GlobalUtils.images.clear();
                GlobalUtils.artist_name.clear();
                GlobalUtils.mClusterManager.clearItems();
                new RequestForAllMarkers().execute();
                GlobalUtils.show_marker_types = "All";
                from_refresh = true;
                return true;
            case R.id.exit:
                GlobalUtils.all_marker.clear();
                GlobalUtils.train_art.clear();
                GlobalUtils.wall_art.clear();
                GlobalUtils.street_art.clear();
                GlobalUtils.images.clear();
                GlobalUtils.artist_name.clear();
                GlobalUtils.mClusterManager.clearItems();
                GlobalUtils.show_marker_types = "All";
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Background Async Task to process the recharge
     */
    class RequestForAllMarkers extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
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
            nameValueParams.add(new BasicNameValuePair("tag", "retriveMarkers"));
            // getting JSON Object
            // Note that create product url accepts POST method
            listParams.add(nameValueParams);
            listParams.add(bitmapParams);
            json = jsonParser.makeHttpRequest(ConstantURLs.base_url, "POST", listParams);

            if (json != null) {

                try {
                    if (json.getInt("success") == 1) {
                        parseJsonData(json);

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


            if (mapFragment != null) {
                mapFragment.updateMap("All");
            }
            if (listFragment != null) {
                listFragment.updateList("All");
            }
        }

    }

    private void parseJsonData(JSONObject json) {
        try {
            JSONArray jsonArray = json.getJSONArray("markers");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                MarkerObject marker = new MarkerObject();
                marker.setId(jObj.getString("id"));
                marker.setArtist_name(jObj.getString("artist_name"));
                marker.setArt_type(jObj.getString("art_type"));
                marker.setDescription(jObj.getString("description"));
                marker.setLat(jObj.getString("lat"));
                marker.setLng(jObj.getString("lng"));
                marker.setImage_url(jObj.getString("image_url"));
                marker.setUpload_date(jObj.getString("date"));
                if (!GlobalUtils.artist_name.contains(jObj.getString("artist_name"))) {
                    GlobalUtils.artist_name.add(jObj.getString("artist_name"));
                }
                GlobalUtils.all_marker.add(marker);

                if (marker.getArt_type().equals("street_art")) {
                    GlobalUtils.street_art.add(marker);
                } else if (marker.getArt_type().equals("train_art")) {
                    GlobalUtils.train_art.add(marker);
                } else if (marker.getArt_type().equals("wall_art")) {
                    GlobalUtils.wall_art.add(marker);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mapFragment != null) {
                if (mapFragment.markerln.getVisibility() == View.VISIBLE)
                    mapFragment.markerln.setVisibility(View.GONE);
                if (mapFragment.listln.getVisibility() == View.VISIBLE)
                    mapFragment.listln.setVisibility(View.GONE);
            }
        }


        return false;
        // Disable back button..............
    }
}
