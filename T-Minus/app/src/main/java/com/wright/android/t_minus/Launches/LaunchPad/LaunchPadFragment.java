package com.wright.android.t_minus.Launches.LaunchPad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.wright.android.t_minus.Objects.PadLocation;
import com.wright.android.t_minus.Objects.LaunchPad;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.networkConnection.GetPadsFromAPI;
import com.wright.android.t_minus.networkConnection.NetworkUtils;

import java.util.ArrayList;
import java.util.ListIterator;

public class LaunchPadFragment extends Fragment implements GetPadsFromAPI.OnFinished {

    private ArrayList<PadLocation> padLocations;
    private ListIterator<PadLocation> iterator;

    public LaunchPadFragment() {
        // Required empty public constructor
    }

    public static LaunchPadFragment newInstance() {
        return  new LaunchPadFragment();
    }

    public void setData(ArrayList<PadLocation> _padLocations){
        if (getView() == null){
            return;
        }
        padLocations = _padLocations;
        iterator = padLocations.listIterator();
        (getView().findViewById(R.id.padProgressBar)).setVisibility(View.VISIBLE);
        if(getContext()!=null&& NetworkUtils.isConnected(getContext())&&iterator.hasNext()){
            new GetPadsFromAPI(this).execute(Integer.toString(iterator.next().getId()));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pad, container, false);
    }

    @Override
    public void onFinished(ArrayList<LaunchPad> _padList) {
        if(iterator.hasPrevious()) {
            padLocations.get(iterator.previousIndex()).setLaunchPads(_padList);
        }
        if(iterator.hasNext()) {
            new GetPadsFromAPI(this).execute(Integer.toString(iterator.next().getId()));
        }else{
            if(getView()!=null){
                FloatingActionButton fab = getView().findViewById(R.id.fab);
                fab.setOnClickListener((View view)->
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show());

                (getView().findViewById(R.id.padProgressBar)).setVisibility(View.GONE);
                ExpandableListView listView = getView().findViewById(R.id.padList);
                PadAdapter padAdapter = new PadAdapter(getContext(), padLocations);
                listView.setAdapter(padAdapter);
                for(int i=0; i < padAdapter.getGroupCount(); i++)
                    listView.expandGroup(i);
            }
        }
    }
}
