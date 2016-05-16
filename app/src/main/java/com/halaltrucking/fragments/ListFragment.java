package com.halaltrucking.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.halaltrucking.R;
import com.halaltrucking.adapters.WorkAdapter;
import com.halaltrucking.utils.GlobalUtils;

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
            mAdapter = new WorkAdapter(this, GlobalUtils.all_marker);
        } else if (type.equals("street_art")) {
            mAdapter = new WorkAdapter(this, GlobalUtils.street_art);
        } else if (type.equals("train_art")) {
            mAdapter = new WorkAdapter(this, GlobalUtils.train_art);
        } else if (type.equals("wall_art")) {
            mAdapter = new WorkAdapter(this, GlobalUtils.wall_art);
        }else if (type.equals("searched")) {
            mAdapter = new WorkAdapter(this, GlobalUtils.searched_marker);
        }
        listView.setAdapter(mAdapter);
    }

}