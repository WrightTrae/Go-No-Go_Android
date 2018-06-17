package com.wright.android.t_minus.Launches.Manifest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wright.android.t_minus.Objects.Manifest;
import com.wright.android.t_minus.R;

public class ManifestFragment extends Fragment implements ListView.OnItemClickListener{

    private ManifestListAdapter manifestListAdapter;
    private Manifest[] manifests;

    public ManifestFragment() {
        // Required empty public constructor
    }

    public static ManifestFragment newInstance() {
        return  new ManifestFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manifest, container, false);
    }

    public void setData(Manifest[] _manifests){
        if(getView() == null){
            return;
        }
        manifests = _manifests;
        getView().findViewById(R.id.manifestProgressBar).setVisibility(View.GONE);
        ListView listView = getView().findViewById(R.id.manifestList);
        manifestListAdapter = new ManifestListAdapter(getContext(), manifests);
        listView.setAdapter(manifestListAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), ManifestDetailsActivity.class);
        intent.putExtra(ManifestDetailsActivity.ARG_MANIFEST, manifests[position]);
        startActivity(intent);
    }
}
