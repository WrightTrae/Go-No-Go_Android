package com.wright.android.t_minus.main_tabs.manifest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wright.android.t_minus.objects.Manifest;
import com.wright.android.t_minus.R;

import java.util.Calendar;
import java.util.Date;


public class ManifestListAdapter extends BaseAdapter{
    // BASE ID
    private static final long BASE_ID = 0x100000;

    // Reference to our owning screen (context)
    private final Context mContext;

    // Reference to our collection
    private final Manifest[] manifests;

    // C-tor
    public ManifestListAdapter(Context _context, Manifest[] manifests){
        mContext = _context;
        this.manifests = manifests;
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
            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();
            calendar.add(Calendar.HOUR, 24);
            Date offsetTime = calendar.getTime();
            Date launchTime = manifest.getTimeDate();
            if(launchTime.after(currentTime) && launchTime.before(offsetTime)){
                vh.tvLaunchAlert.setVisibility(View.VISIBLE);
            }else{
                vh.tvLaunchAlert.setVisibility(View.GONE);
            }

            vh.tvTitle.setText(manifest.getTitle());
            vh.tvTime.setText(manifest.getTime());
            if(manifest.getPadLocation() == null){
                vh.tvLocation.setText(R.string.unknown_location);
            }else {
                vh.tvLocation.setText(manifest.getPadLocation().getName());
            }
            Picasso picasso = Picasso.get();
            if(manifest.getImageUrl()!=null &&
                    !manifest.getImageUrl().trim().isEmpty()){
                picasso.load(manifest.getImageUrl()).fit().centerCrop()
                        .placeholder(R.drawable.rocket_default_image).into(vh.tvImage);
            }else {
                if(manifest.getAgencyURL() == null){
                    vh.tvImage.setImageDrawable(mContext.getDrawable(R.drawable.rocket_default_image));
                }else {
                    picasso.load(manifest.getAgencyURL()+"?size=700")
                            .fit().centerCrop().placeholder(R.drawable.rocket_default_image).into(vh.tvImage);
                }
            }
        }
        return _recycleView;
    }

    // Optimize with view holder!
    static class ViewHolder{
        final View tvLaunchAlert;
        final TextView tvTitle;
        final TextView tvTime;
        final TextView tvLocation;
        final ImageView tvImage;

        private ViewHolder(View _layout){
            tvTitle = _layout.findViewById(R.id.cellMissionTitle);
            tvTime = _layout.findViewById(R.id.detailsNETTime);
            tvLocation = _layout.findViewById(R.id.detailsLocation);
            tvImage = _layout.findViewById(R.id.cellImage);
            tvLaunchAlert = _layout.findViewById(R.id.cellActiveLaunchText);
        }
    }
}
