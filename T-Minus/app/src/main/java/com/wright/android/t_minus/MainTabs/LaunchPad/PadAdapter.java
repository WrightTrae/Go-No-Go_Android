package com.wright.android.t_minus.MainTabs.LaunchPad;

import android.widget.BaseExpandableListAdapter;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wright.android.t_minus.Objects.LaunchPad;
import com.wright.android.t_minus.Objects.PadLocation;
import com.wright.android.t_minus.R;

public class PadAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final ArrayList<PadLocation> groups;

    public PadAdapter(Context context, ArrayList<PadLocation> groups) {
        this.context = context;
        this.groups = groups;
    }

    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<LaunchPad> chList = groups.get(groupPosition).getLaunchPads();
        return chList.get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        LaunchPad child = (LaunchPad) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (infalInflater != null) {
                view = infalInflater.inflate(R.layout.pad_content_cell, parent, false);
            }
        }
        if(view!=null) {
            TextView tv = view.findViewById(R.id.cellPadName);
            tv.setText(child.getName());
        }
        return view;
    }

    public int getChildrenCount(int groupPosition) {
        ArrayList<LaunchPad> chList = groups.get(groupPosition).getLaunchPads();
        return chList.size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        PadLocation group = (PadLocation) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inf != null) {
                view = inf.inflate(R.layout.pad_header_view, parent, false);
            }
        }
        if(view!=null) {
            TextView tv = view.findViewById(R.id.separator);
            tv.setText(group.getName());
        }
        return view;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}
