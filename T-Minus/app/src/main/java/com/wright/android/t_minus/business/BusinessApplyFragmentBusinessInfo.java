package com.wright.android.t_minus.business;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.TextFieldUtils;

public class BusinessApplyFragmentBusinessInfo extends Fragment {

    private BusinessApplyButtonClicks mListener;
    private EditText et_business_name;
    private EditText et_business_address;
    private EditText et_business_number;

    public BusinessApplyFragmentBusinessInfo() {
        // Required empty public constructor
    }

    public static BusinessApplyFragmentBusinessInfo newInstance() {
        BusinessApplyFragmentBusinessInfo fragment = new BusinessApplyFragmentBusinessInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_apply_business_info, container, false);
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
        et_business_name = view.findViewById(R.id.business_apply_business_name);
        et_business_address = view.findViewById(R.id.business_apply_business_address);
        et_business_number = view.findViewById(R.id.business_apply_business_number);
        Button b = view.findViewById(R.id.business_apply_business_submit);
        b.setOnClickListener((View v)->
                mListener.businessInfoSubmitClick(et_business_name.getText().toString(),
                        et_business_number.getText().toString(), et_business_address.getText().toString()));
    }

    private void checkFields() {

        // Reset errors.
        et_business_name.setError(null);
        et_business_address.setError(null);
        et_business_number.setError(null);

        String name = et_business_name.getText().toString();
        String number = et_business_address.getText().toString();
        String email = et_business_number.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            et_business_name.setError(getString(R.string.error_field_required));
            focusView = et_business_name;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            et_business_address.setError(getString(R.string.error_field_required));
            focusView = et_business_address;
            cancel = true;
        } else if (!TextFieldUtils.isEmailValid(email)) {
            et_business_address.setError(getString(R.string.error_invalid_email));
            focusView = et_business_address;
            cancel = true;
        }

        // Check for a valid number
        if (TextUtils.isEmpty(number)) {
            et_business_number.setError(getString(R.string.error_field_required));
            focusView = et_business_number;
            cancel = true;
        } else if(TextFieldUtils.isPhoneNumberInvalid(number)){
            et_business_number.setError(getString(R.string.error_invalid_number));
            focusView = et_business_number;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mListener.businessInfoSubmitClick(et_business_name.getText().toString(), et_business_number.getText().toString(),
                    et_business_address.getText().toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
