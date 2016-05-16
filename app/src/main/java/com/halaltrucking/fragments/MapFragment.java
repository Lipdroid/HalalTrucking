package com.halaltrucking.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.halaltrucking.MainActivity;
import com.halaltrucking.MarkerDetails;
import com.halaltrucking.R;
import com.halaltrucking.adapters.WorkAdapter;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.models.MyItem;
import com.halaltrucking.utils.GlobalUtils;
import com.halaltrucking.utils.MultipleScreen;
import com.halaltrucking.utils.OwnIconRendered;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by WebHawks IT on 2/16/2016.
 */
public class MapFragment extends Fragment implements ClusterManager.OnClusterClickListener<MyItem>, ClusterManager.OnClusterInfoWindowClickListener<MyItem>, ClusterManager.OnClusterItemClickListener<MyItem>, ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> {
    GoogleMap mGoogleMap;
    public boolean ischecked = false;
    public boolean isMapClear = false;
    private ImageView add, search_start, image, cross_list;
    private AutoCompleteTextView search;
    private TextView title, des, date, art_type;
    ArrayAdapter<String> dataAdapter;
    public LinearLayout markerln, listln;
    public RelativeLayout rlcross;
    ProgressBar progressBar;
    private WorkAdapter mAdapter = null;
    private ListView listView = null;
    public static List<MarkerObject> cluster_markers = new ArrayList<MarkerObject>();
    LinearLayout error_ln = null;

    // Declare a variable for the cluster manager.
    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        error_ln = (LinearLayout) root.findViewById(R.id.error_ln);
        add = (ImageView) root.findViewById(R.id.add);
        search_start = (ImageView) root.findViewById(R.id.img_search);
        image = ((ImageView) root.findViewById(R.id.badge));
        cross_list = ((ImageView) root.findViewById(R.id.cross_image));
        title = ((TextView) root.findViewById(R.id.title));
        art_type = ((TextView) root.findViewById(R.id.art_type));
        date = ((TextView) root.findViewById(R.id.date));
        markerln = (LinearLayout) root.findViewById(R.id.ln_marker);
        listln = (LinearLayout) root.findViewById(R.id.lnlist);
        des = ((TextView) root.findViewById(R.id.snippet));
        rlcross = (RelativeLayout) root.findViewById(R.id.rl_cross);
        rlcross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset the image view and invisible the marker linear layout
                markerln.setVisibility(View.GONE);
                image.setImageResource(R.drawable.error);
                image.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        cross_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listln.setVisibility(View.GONE);
            }
        });
        progressBar = (ProgressBar) root.findViewById(R.id.progress);
        markerln.setVisibility(View.GONE);
        listln.setVisibility(View.GONE);
        search_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (MarkerObject markerObject : GlobalUtils.all_marker) {
                    if (markerObject.getArtist_name().equals(search.getText().toString())) {
                        GlobalUtils.searched_marker.clear();
                        GlobalUtils.searched_marker.add(markerObject);
                        GlobalUtils.show_marker_types = "searched";
                        ((MainActivity) getActivity()).mapFragment.updateMap("searched");
                        ((MainActivity) getActivity()).listFragment.updateList("searched");

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    }
                }
            }
        });
        search = (AutoCompleteTextView) root.
                findViewById(R.id.et_search);
        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, GlobalUtils.artist_name);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // attaching data adapter to spinner
        search.setAdapter(dataAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) getActivity()).startAddActivity();

            }
        });
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(),
                    requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment
            mGoogleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            //  mGoogleMap.setInfoWindowAdapter(new CustomInfoAdapter(getActivity()));


        }
        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), "Something wrong with the GPS", Toast.LENGTH_LONG).show();
        }
        try {
            //move the my position button
//            View mapView = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getView();
//            View btnMyLocation = ((View) mapView.findViewById(1).getParent()).findViewById(2);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150,150); // size of button in dp
//            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//            params.setMargins(20, 20, 20, 20);
//            btnMyLocation.setLayoutParams(params);

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    MarkerObject markerObject = GlobalUtils.markers.get(marker.getId());
                    Intent i = new Intent(getActivity(), MarkerDetails.class);
                    i.putExtra("id", markerObject.getId());
                    startActivity(i);


                }
            });
        }catch (Exception e){

        }
        listView = (ListView) root.findViewById(R.id.list);
        initializeList();
        new MultipleScreen(getActivity());
        MultipleScreen.resizeAllView((ViewGroup) root);
        return root;
    }

    private void initializeList() {
        mAdapter = new WorkAdapter(this, cluster_markers);
        listView.setAdapter(mAdapter);

    }

    public void updateMap(String type) {
        try {
            if (dataAdapter != null)
                dataAdapter.notifyDataSetChanged();
            if (mGoogleMap != null)
                mGoogleMap.clear();
            if (GlobalUtils.mClusterManager != null)
                GlobalUtils.mClusterManager.clearItems();
            setUpClusterer();
            error_ln.setVisibility(View.GONE);
            if (type.equals("All")) {
                for (MarkerObject markerObject : GlobalUtils.all_marker) {
                    //setMarker(markerObject);
                    MyItem offsetItem = new MyItem(Double.parseDouble(markerObject.getLat()), Double.parseDouble(markerObject.getLng()), markerObject);
                    GlobalUtils.mClusterManager.addItem(offsetItem);
                }
                if (((MainActivity) getActivity()).from_refresh) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.259239, -95.9351567), 3));
                    ((MainActivity) getActivity()).from_refresh = false;
                }
            } else if (type.equals("street_art")) {
                for (MarkerObject markerObject : GlobalUtils.street_art) {

                    // setMarker(markerObject);

                    MyItem offsetItem = new MyItem(Double.parseDouble(markerObject.getLat()), Double.parseDouble(markerObject.getLng()), markerObject);
                    GlobalUtils.mClusterManager.addItem(offsetItem);

                }

            } else if (type.equals("train_art")) {
                for (MarkerObject markerObject : GlobalUtils.train_art) {

                    // setMarker(markerObject);
                    MyItem offsetItem = new MyItem(Double.parseDouble(markerObject.getLat()), Double.parseDouble(markerObject.getLng()), markerObject);
                    GlobalUtils.mClusterManager.addItem(offsetItem);

                }

            } else if (type.equals("wall_art")) {
                for (MarkerObject markerObject : GlobalUtils.wall_art) {

                    //setMarker(markerObject);
                    MyItem offsetItem = new MyItem(Double.parseDouble(markerObject.getLat()), Double.parseDouble(markerObject.getLng()), markerObject);
                    GlobalUtils.mClusterManager.addItem(offsetItem);

                }
            } else if (type.equals("searched")) {
                for (MarkerObject markerObject : GlobalUtils.searched_marker) {

                    //setMarker(markerObject);
                    MyItem offsetItem = new MyItem(Double.parseDouble(markerObject.getLat()), Double.parseDouble(markerObject.getLng()), markerObject);
                    GlobalUtils.mClusterManager.addItem(offsetItem);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(Double.parseDouble(GlobalUtils.searched_marker.get(0).getLat()), Double.parseDouble(GlobalUtils.searched_marker.get(0).getLng()))), 7));
                }
            }

            GlobalUtils.mClusterManager.cluster();
        }catch (Exception e){

        }

    }

    @Override
    public boolean onClusterClick(Cluster<MyItem> cluster) {
        // Show a toast with some info when the cluster is clicked.
//        LatLng firstName = cluster.getItems().iterator().next().getPosition();
//        Toast.makeText(getActivity(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
        cluster_markers.clear();
        Object[] itmes = cluster.getItems().toArray();
        MyItem item = null;
        for (int i = 0; i < itmes.length; i++) {
            item = (MyItem) itmes[i];
            cluster_markers.add(item.getMarkerObj());
        }
        mAdapter.notifyDataSetChanged();
        listln.setVisibility(View.VISIBLE);
        return true;
    }


    private void setUpClusterer() {

        // Position the map.
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        GlobalUtils.mClusterManager = new ClusterManager<MyItem>(getActivity(), mGoogleMap);
        // manager.
        mGoogleMap.setOnCameraChangeListener(GlobalUtils.mClusterManager);
        mGoogleMap.setOnMarkerClickListener(GlobalUtils.mClusterManager);
        //for zoom level limitaions
//        if (!GlobalUtils.show_marker_types.equals("street_art")) {
//            mGoogleMap.setOnCameraChangeListener(getCameraChangeListener());
//        } else {
//            mGoogleMap.setOnCameraChangeListener(GlobalUtils.mClusterManager);
//        }
        mGoogleMap.setOnCameraChangeListener(GlobalUtils.mClusterManager);
        mGoogleMap.setOnMarkerClickListener(GlobalUtils.mClusterManager);
        mGoogleMap.setOnInfoWindowClickListener(GlobalUtils.mClusterManager);

        GlobalUtils.mClusterManager.setOnClusterClickListener(this);
        GlobalUtils.mClusterManager.setOnClusterInfoWindowClickListener(this);
        GlobalUtils.mClusterManager.setOnClusterItemClickListener(this);
        GlobalUtils.mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        GlobalUtils.mClusterManager.setOnClusterClickListener(this);
        //for different icons in maps
        GlobalUtils.mClusterManager.setRenderer(new OwnIconRendered(getActivity().getApplicationContext(), mGoogleMap, GlobalUtils.mClusterManager));


    }
    //this is for zoom limitation
//    public GoogleMap.OnCameraChangeListener getCameraChangeListener() {
//        return new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition position) {
//                Log.d("Zoom", "Zoom: " + position.zoom);
//                if (!GlobalUtils.show_marker_types.equals("street_art")) {
//                    if (position.zoom <= 7) {
//                        error_ln.setVisibility(View.GONE);
//                        if (!ischecked) {
//                            ((MainActivity) getActivity()).mapFragment.updateMap(GlobalUtils.show_marker_types);
//                            Log.e("ischecked", ischecked + "");
//                            ischecked = true;
//                        }
//                        isMapClear = false;
//                    } else {
//                        error_ln.setVisibility(View.VISIBLE);
//                        mGoogleMap.clear();
//                        ischecked = false;
//                    }
//
//                }
//            }
//        };
//    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MyItem> cluster) {

    }

    @Override
    public boolean onClusterItemClick(MyItem myItem) {
        MarkerObject markerObject = new MarkerObject();
        markerObject = myItem.getMarkerObj();
        title.setText(markerObject.getArtist_name());
        des.setText(markerObject.getDescription());
        date.setText(markerObject.getUpload_date());
        art_type.setText(markerObject.getArt_type());
        GlobalUtils.sImageLoader.displayImage(markerObject.getImage_url(), new ImageView(getActivity()), GlobalUtils.sOptForImgLoader, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);
                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                progressBar.setVisibility(View.GONE);
            }
        });
        markerln.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MyItem myItem) {

    }
}
