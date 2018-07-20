package com.wright.android.t_minus.main_tabs.photos;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.main_tabs.manifest.ManifestDetailsActivity;
import com.wright.android.t_minus.objects.Manifest;

import java.util.ArrayList;


public class PhotoAdapter extends BaseAdapter{
    // BASE ID
    private static final long BASE_ID = 0x100000;

    // Reference to our owning screen (context)
    private final Context mContext;

    private ArrayList<String> downloadUrls;

    // C-tor
    public PhotoAdapter(Context _context){
        mContext = _context;
        downloadUrls = new ArrayList<>();
    }

    // Count
    public int getCount(){
        if(downloadUrls !=null) {
            return downloadUrls.size();
        }
        return 0;
    }

    // Item
    public String getItem(int _position){
        if(downloadUrls !=null) {
            return downloadUrls.get(_position);
        }
        return null;
    }

    public void addData(String path){
        downloadUrls.add(path);
    }

    public void resetData(){
        downloadUrls = new ArrayList<>();
    }

    // Item ID
    public long getItemId(int _position){
        return BASE_ID + _position;
    }

    // Get the inflated child / line-item view
    public View getView(int _position, View _recycleView, ViewGroup _parentView){
        ViewHolder vh;
        if(_recycleView == null) {
            _recycleView = LayoutInflater.from(mContext).inflate(R.layout.photo_grid_cell, _parentView, false);
            vh = new ViewHolder(_recycleView);
            _recycleView.setTag(vh);
        }else{
            vh = (ViewHolder) _recycleView.getTag();
        }
        String url = getItem(_position);
        vh.tvLikes.setText("0");
        Picasso picasso = Picasso.get();
        picasso.load(url)
                .centerCrop().fit().placeholder(R.drawable.rocket_default_image).into(vh.ivImage);
        vh.ivImage.setTag(picasso.load(url)
                .placeholder(R.drawable.rocket_default_image));
        return _recycleView;
    }

    // Optimize with view holder!
    static class ViewHolder{
        final ImageView ivImage;
        final TextView tvLikes;
        private ViewHolder(View _layout){
            ivImage = _layout.findViewById(R.id.grid_image);
            tvLikes = _layout.findViewById(R.id.grid_cell_likes);
        }
    }
}
