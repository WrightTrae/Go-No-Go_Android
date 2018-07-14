package com.wright.android.t_minus.business.edit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.Business;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditBusinessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditBusinessFragment extends Fragment {

    private static final String ARG_BUSINESS = "ARG_BUSINESS";

    private Business business;

    public EditBusinessFragment() {

    }

    public static EditBusinessFragment newInstance(Business _business) {
        EditBusinessFragment fragment = new EditBusinessFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUSINESS, _business);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            business = (Business) getArguments().getSerializable(ARG_BUSINESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_business, container, false);
    }

}
