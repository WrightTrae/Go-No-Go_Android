package com.wright.android.t_minus.business.edit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.Business;

import java.util.ArrayList;
import java.util.List;


public class BusinessListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_BUSINESS = "ARG_BUSINESS";
    private ArrayList<Business> businesses;
    private OnListFragmentInteractionListener mListener;


    public BusinessListFragment() {
    }

    public static BusinessListFragment newInstance(ArrayList<Business> _businesses) {
        BusinessListFragment fragment = new BusinessListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUSINESS, _businesses);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            businesses = (ArrayList<Business>) getArguments().getSerializable(ARG_BUSINESS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyBusinessRecyclerViewAdapter(businesses, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Business item);
    }
}
