package com.wright.android.t_minus.business.apply;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wright.android.t_minus.R;

public class BusinessApplyFragmentConformation extends Fragment {

    private BusinessApplyButtonClicks mListener;

    public BusinessApplyFragmentConformation() {
        // Required empty public constructor
    }

    public static BusinessApplyFragmentConformation newInstance() {
        BusinessApplyFragmentConformation fragment = new BusinessApplyFragmentConformation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_apply_conformation, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BusinessApplyButtonClicks) {
            mListener = (BusinessApplyButtonClicks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BusinessApplyButtonClicks");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.business_apply_conformation).setOnClickListener((View v)->
            mListener.conformationDoneClick());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
