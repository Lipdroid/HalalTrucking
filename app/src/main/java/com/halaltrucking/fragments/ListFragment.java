package com.halaltrucking.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.halaltrucking.R;
import com.halaltrucking.adapters.WorkAdapter;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.utils.GlobalUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by WebHawks IT on 2/16/2016.
 */
public class ListFragment extends Fragment {
    private WorkAdapter mAdapter = null;
    private ListView listView = null;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_list, container, false);
        listView = (ListView) root.findViewById(R.id.marker_lv);
        initializeList();
        return root;
    }

    private void initializeList() {
        mAdapter = new WorkAdapter(this, GlobalUtils.all_marker);
        listView.setAdapter(mAdapter);

    }

    public void updateList(String type) {
        mAdapter = null;
        if (type.equals("All")) {
            mAdapter = new WorkAdapter(this, sortList(GlobalUtils.all_marker));
        } else if (type.equals("Food")) {
            mAdapter = new WorkAdapter(this, sortList(GlobalUtils.Food));
        } else if (type.equals("Mosques")) {
            mAdapter = new WorkAdapter(this, sortList(GlobalUtils.Mosques));
        } else if (type.equals("Miscellaneous")) {
            mAdapter = new WorkAdapter(this,sortList(GlobalUtils.Miscellaneous));
        }else if (type.equals("searched")) {
            mAdapter = new WorkAdapter(this, sortList(GlobalUtils.searched_marker));
        }
        listView.setAdapter(mAdapter);
    }
    public List<MarkerObject> sortList(List<MarkerObject> sortedList){
        Collections.sort(sortedList, new Comparator<MarkerObject>() {
            @Override
            public int compare(MarkerObject o1, MarkerObject o2) {

                Double newest1 = distance(GlobalUtils.user_lat, GlobalUtils.user_lng, Double.parseDouble(o1.getLat()), Double.parseDouble(o1.getLng()));
                Double newest2 = distance(GlobalUtils.user_lat, GlobalUtils.user_lng, Double.parseDouble(o2.getLat()), Double.parseDouble(o2.getLng()));
                return newest1.compareTo(newest2);
            }
        });
        return  sortedList;
    }

    static double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }

}