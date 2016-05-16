package com.halaltrucking.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.halaltrucking.MarkerDetails;
import com.halaltrucking.R;
import com.halaltrucking.adapters.holders.ListHolder;
import com.halaltrucking.models.MarkerObject;
import com.halaltrucking.utils.GlobalUtils;
import com.halaltrucking.utils.MultipleScreen;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;


public class WorkAdapter extends BaseAdapter {
    private static final String TAG_LOG = "WorkAdapter";
    private Context mContext = null;
    private Fragment mFragment = null;
    private List<MarkerObject> mListData = null;
    ListHolder mHolder = null;

    public WorkAdapter(Fragment fragment,List<MarkerObject> listData) {
        // TODO Auto-generated constructor stub
        mContext = fragment.getActivity();
        mFragment = fragment;
        mListData = listData;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.item_work, null);
            mHolder = new ListHolder();

            mHolder.btnRoot = (Button) convertView.findViewById(R.id.item_work_btn_root);
            mHolder.imgContent = (RoundedImageView) convertView.findViewById(R.id.item_work_img);
            mHolder.tvTitle = (TextView) convertView.findViewById(R.id.item_work_tv_title);
            mHolder.tvPoint = (TextView) convertView.findViewById(R.id.item_work_tv_point);
            mHolder.tvDate = (TextView) convertView.findViewById(R.id.item_work_tv_date);
            mHolder.btnAttend = (Button) convertView.findViewById(R.id.item_work_btn_attend);

            new MultipleScreen(mContext);
            MultipleScreen.resizeAllView((ViewGroup) convertView);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ListHolder) convertView.getTag();
        }

        final MarkerObject markerObject = (MarkerObject) mListData.get(position);

        mHolder.tvTitle.setText(markerObject.getDescription());
        mHolder.tvDate.setText(markerObject.getUpload_date());
        mHolder.tvPoint.setText(markerObject.getArtist_name());
        mHolder.btnAttend.setText(markerObject.getArt_type());
        String url = markerObject.getImage_url();
        GlobalUtils.sImageLoader.displayImage(url, mHolder.imgContent,GlobalUtils.sOptForImgLoader, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                try {
                    float imgRatio = 990.0f / 470;
                    float bmRatio = bitmap.getWidth() * 1.0f / (bitmap.getHeight() +MultipleScreen.getValueAfterResize(60));

                    if (imgRatio >= bmRatio) {
                        ((RoundedImageView) view).setCornerRadius(
                                MultipleScreen.getValueAfterResize(30), MultipleScreen.getValueAfterResize(30), 0, 0);
                       // ((RoundedImageView) view).setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ((RoundedImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
                    } else {
                        ((RoundedImageView) view).setCornerRadius(0, 0, 0, 0);
                        ((RoundedImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        // set listener

        mHolder.btnRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MarkerDetails.class);
                i.putExtra("id", markerObject.getId());
                mContext.startActivity(i);
            }
        });
        return convertView;
    }

}
