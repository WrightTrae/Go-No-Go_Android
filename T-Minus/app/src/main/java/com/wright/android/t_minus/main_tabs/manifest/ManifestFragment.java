package com.wright.android.t_minus.main_tabs.manifest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wright.android.t_minus.network_connection.GetAgencyUrlAPI;
import com.wright.android.t_minus.network_connection.NetworkUtils;
import com.wright.android.t_minus.objects.Manifest;
import com.wright.android.t_minus.R;

public class ManifestFragment extends Fragment implements ListView.OnItemClickListener, GetAgencyUrlAPI.OnFinished{

    private Manifest[] manifests;
    private int agenciesCompleted = 1;
    private static final String TAG = "TESTETAG";

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
        downloadAgencyLogo();
    }

    private void downloadAgencyLogo(){
        if(NetworkUtils.isConnected(getContext())){
            new GetAgencyUrlAPI(this, manifests).execute();
        }else{
            Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", (View v) -> downloadAgencyLogo()).show();
        }
    }

    @Override
    public void onFinished(Manifest[] _manifests) {//downloadAgencyLogo Finish
        manifests = _manifests;
        getView().findViewById(R.id.manifestProgressBar).setVisibility(View.GONE);
        ListView listView = getView().findViewById(R.id.manifestList);
        ManifestListAdapter manifestListAdapter = new ManifestListAdapter(getContext(), manifests);
        listView.setAdapter(manifestListAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), ManifestDetailsActivity.class);
        intent.putExtra(ManifestDetailsActivity.ARG_MANIFEST, manifests[position]);
        startActivity(intent);
    }
}
