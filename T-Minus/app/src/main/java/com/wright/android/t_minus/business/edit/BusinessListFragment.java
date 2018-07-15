package com.wright.android.t_minus.business.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.business.apply.BusinessApplyActivity;
import com.wright.android.t_minus.objects.Business;

import java.util.ArrayList;
import java.util.List;


public class BusinessListFragment extends Fragment {

    private static final String ARG_BUSINESS = "ARG_BUSINESS";
    private ArrayList<Business> businesses;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private MyBusinessRecyclerViewAdapter businessRecyclerViewAdapter;


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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            businesses = (ArrayList<Business>) getArguments().getSerializable(ARG_BUSINESS);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            Intent intent = new Intent(getContext(), BusinessApplyActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            businessRecyclerViewAdapter = new MyBusinessRecyclerViewAdapter(businesses, mListener);
            recyclerView.setAdapter(businessRecyclerViewAdapter);
        }
        return view;
    }

    public void notifyListView(Business newBusiness, int index){
        businesses.remove(index);
        businesses.add(index, newBusiness);
        businessRecyclerViewAdapter.notifyItemChanged(index);
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
        void onListFragmentInteraction(Business item);
    }
}
