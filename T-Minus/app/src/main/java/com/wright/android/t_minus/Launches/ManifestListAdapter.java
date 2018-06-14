package com.wright.android.t_minus.Launches;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wright.android.t_minus.Manifest;
import com.wright.android.t_minus.R;


public class ManifestListAdapter extends BaseAdapter {
    // BASE ID
    private static final long BASE_ID = 0x100000;

    // Reference to our owning screen (context)
    private final Context mContext;

    // Reference to our collection
    private Manifest[] manifests;

    // C-tor
    public ManifestListAdapter(Context _context){
        mContext = _context;
        this.manifests = new Manifest[0];
    }

    public void updateData(Manifest[] _manifests){
        manifests = _manifests;
    }

    // Count
    public int getCount(){
        if(manifests !=null) {
            return manifests.length;
        }
        return 0;
    }

    // Item
    public Object getItem(int _position){
        if(manifests !=null) {
            return manifests[_position];
        }
        return null;
    }

    // Item ID
    public long getItemId(int _position){
        return BASE_ID + _position;
    }

    // Get the inflated child / line-item view
    public View getView(int _position, View _recycleView, ViewGroup _parentView){
        ViewHolder vh;
        if(_recycleView == null) {
            _recycleView = LayoutInflater.from(mContext).inflate(R.layout.custom_manifest_cell, _parentView, false);
            vh = new ViewHolder(_recycleView);
            _recycleView.setTag(vh);
        }else{
            vh = (ViewHolder) _recycleView.getTag();
        }
        Manifest manifest = (Manifest) getItem(_position);
        if(manifest!=null){
            vh.tvTitle.setText(manifest.getTitle());
            vh.tvTime.setText(manifest.getTime());
            vh.tvLocation.setText(manifest.getLocation());
            if(!manifest.getImageUrl().equals("https://s3.amazonaws.com/launchlibrary/RocketImages/placeholder_1920.png")){
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(true);
                picasso.load(manifest.getImageUrl()).fit().centerCrop()
                        .placeholder(R.drawable.logo_outline).into(vh.tvImage);
            }else {
                vh.tvImage.setImageDrawable(mContext.getDrawable(R.drawable.logo_outline));
            }
        }
        return _recycleView;
    }

    // Optimize with view holder!
    static class ViewHolder{
        final TextView tvTitle;
        final TextView tvTime;
        final TextView tvLocation;
        final ImageView tvImage;

        private ViewHolder(View _layout){
            tvTitle = _layout.findViewById(R.id.cellMissionTitle);
            tvTime = _layout.findViewById(R.id.cellNETTime);
            tvLocation = _layout.findViewById(R.id.cellLocation);
            tvImage = _layout.findViewById(R.id.cellImage);
        }
    }
}
