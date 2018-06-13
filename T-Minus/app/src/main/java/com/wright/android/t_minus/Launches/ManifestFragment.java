package com.wright.android.t_minus.Launches;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wright.android.t_minus.Manifest;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.networkConnection.GetManifestsFromAPI;
import com.wright.android.t_minus.networkConnection.NetworkUtils;

public class ManifestFragment extends Fragment implements GetManifestsFromAPI.OnFinished{

    private ManifestListAdapter manifestListAdapter;

    public ManifestFragment() {
        // Required empty public constructor
    }

    public static ManifestFragment newInstance() {
        return  new ManifestFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manifest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getContext()!=null&&NetworkUtils.isConnected(getContext())){
            new GetManifestsFromAPI(this).execute("");

        }
        ListView listView = view.findViewById(R.id.manifestList);
        manifestListAdapter = new ManifestListAdapter(getContext());
        listView.setAdapter(manifestListAdapter);
    }

    @Override
    public void onFinished(Manifest[] _manifests) {
        manifestListAdapter.updateData(_manifests);
        manifestListAdapter.notifyDataSetChanged();
    }
}
