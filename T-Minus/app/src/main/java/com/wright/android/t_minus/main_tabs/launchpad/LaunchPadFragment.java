package com.wright.android.t_minus.main_tabs.launchpad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.wright.android.t_minus.ar.ArActivity;
import com.wright.android.t_minus.objects.PadLocation;
import com.wright.android.t_minus.R;

import java.util.ArrayList;

public class LaunchPadFragment extends Fragment implements ExpandableListView.OnChildClickListener {

    private ArrayList<PadLocation> padLocations;

    public LaunchPadFragment() {
        // Required empty public constructor
    }

    public static LaunchPadFragment newInstance() {
        return  new LaunchPadFragment();
    }

    public void setData(ArrayList<PadLocation> _padLocations){
        padLocations = _padLocations;
        if(getView()!=null){
            FloatingActionButton fab = getView().findViewById(R.id.fab);
            fab.setOnClickListener((View view)-> {
                Intent intent = new Intent(getContext(), ArActivity.class);
                intent.putExtra(ArActivity.ARG_ALL_LAUNCH_PADS, _padLocations);
                startActivity(intent);
            });

            (getView().findViewById(R.id.padProgressBar)).setVisibility(View.GONE);
            ExpandableListView listView = getView().findViewById(R.id.padList);
            listView.setOnChildClickListener(this);
            PadAdapter padAdapter = new PadAdapter(getContext(), _padLocations);
            listView.setAdapter(padAdapter);
            for(int i=0; i < padAdapter.getGroupCount(); i++)
                listView.expandGroup(i);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pad, container, false);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(getContext(), ArActivity.class);
        intent.putExtra(ArActivity.ARG_LAUNCH_PAD, padLocations.get(groupPosition).getLaunchPads().get(childPosition));
        startActivity(intent);
        return false;
    }
}
