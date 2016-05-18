package com.halaltrucking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.halaltrucking.fragments.ListFragment;
import com.halaltrucking.fragments.MapFragment;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.utils.ConstantURLs;
import com.halaltrucking.utils.CorrectSizeUtil;
import com.halaltrucking.utils.GlobalUtils;
import com.halaltrucking.utils.JSONParser;
import com.halaltrucking.utils.MultipleScreen;
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
    private Button Food = null;
    private Button Mosques = null;
    private Button Miscellaneous = null;
    AlertDialog alertDialog;
    public boolean from_refresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        new SCImageUtils(MainActivity.this);
        Food = (Button) findViewById(R.id.content_btn_street);
        Mosques = (Button) findViewById(R.id.content_btn_train);
        Miscellaneous = (Button) findViewById(R.id.content_btn_wall);

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

        Food.setOnClickListener(this);
        Mosques.setOnClickListener(this);
        Miscellaneous.setOnClickListener(this);

        //Init variable
        mCorrectSize = CorrectSizeUtil.getInstance(this);
        mCorrectSize.correctSize();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            GlobalUtils.all_marker.clear();
            GlobalUtils.Mosques.clear();
            GlobalUtils.Miscellaneous.clear();
            GlobalUtils.Food.clear();
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
                GlobalUtils.show_marker_types = "Food";
                if (mapFragment != null) {
                    mapFragment.updateMap("Food");
                }
                if (listFragment != null) {
                    listFragment.updateList("Food");
                }
                break;
            case R.id.content_btn_train:
                GlobalUtils.show_marker_types = "Mosques";
                if (mapFragment != null) {
                    mapFragment.updateMap("Mosques");
                }
                if (listFragment != null) {
                    listFragment.updateList("Mosques");
                }
                break;
            case R.id.content_btn_wall:
                GlobalUtils.show_marker_types = "Miscellaneous";
                if (mapFragment != null) {
                    mapFragment.updateMap("Miscellaneous");
                }
                if (listFragment != null) {
                    listFragment.updateList("Miscellaneous");
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
                //about code
                final Dialog dialog = new Dialog(this, R.style.CustomDialogTheme);
                LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflator.inflate(R.layout.layout_show_info_dialog, null);

                new MultipleScreen(this);
                MultipleScreen.resizeAllView((ViewGroup) v);

                dialog.setContentView(v);


                // set the custom dialog components - text, image and button
                Button btnOK = (Button) dialog.findViewById(R.id.dialog_btn_positive);
                TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_tv_title);
                TextView tvBody_one = (TextView) dialog.findViewById(R.id.dialog_tv_body_one);
                TextView tvBody_two = (TextView) dialog.findViewById(R.id.dialog_tv_body_two);
                TextView tvBody_three = (TextView) dialog.findViewById(R.id.dialog_tv_body_three);
                TextView tvBody_four = (TextView) dialog.findViewById(R.id.dialog_tv_body_four);

                tvBody_one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(MainActivity.this, WebviewActivity.class);
                        mIntent.putExtra("url", "http://connect2.delta.com/");
                        startActivity(mIntent);
                        overridePendingTransition(R.anim.anim_slide_in_bottom,
                                R.anim.anim_scale_to_center);
                    }
                });
                tvBody_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(MainActivity.this, WebviewActivity.class);
                        mIntent.putExtra("url", "http://skynet.ual.com/");
                        startActivity(mIntent);
                        overridePendingTransition(R.anim.anim_slide_in_bottom,
                                R.anim.anim_scale_to_center);
                    }
                });

                tvBody_three.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(MainActivity.this, WebviewActivity.class);
                        mIntent.putExtra("url", "https://wings.usairways.com/uswings/travel/myid_travel_new_home");
                        startActivity(mIntent);
                        overridePendingTransition(R.anim.anim_slide_in_bottom,
                                R.anim.anim_scale_to_center);
                    }
                });
                tvBody_four.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(MainActivity.this, WebviewActivity.class);
                        mIntent.putExtra("url", "http://www.skywestonline.com/");
                        startActivity(mIntent);
                        overridePendingTransition(R.anim.anim_slide_in_bottom,
                                R.anim.anim_scale_to_center);
                    }
                });

                tvTitle.setText("About");

                // if button is clicked, close the custom dialog
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                return true;
            case R.id.refresh:
                //your code here
                GlobalUtils.all_marker.clear();
                GlobalUtils.Mosques.clear();
                GlobalUtils.Miscellaneous.clear();
                GlobalUtils.Food.clear();
                GlobalUtils.images.clear();
                GlobalUtils.artist_name.clear();
                GlobalUtils.mClusterManager.clearItems();
                new RequestForAllMarkers().execute();
                GlobalUtils.show_marker_types = "All";
                from_refresh = true;
                return true;
            case R.id.exit:
                GlobalUtils.all_marker.clear();
                GlobalUtils.Mosques.clear();
                GlobalUtils.Miscellaneous.clear();
                GlobalUtils.Food.clear();
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

                if (marker.getArt_type().equals("Food")) {
                    GlobalUtils.Food.add(marker);
                } else if (marker.getArt_type().equals("Mosques")) {
                    GlobalUtils.Mosques.add(marker);
                } else if (marker.getArt_type().equals("Miscellaneous")) {
                    GlobalUtils.Miscellaneous.add(marker);
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
