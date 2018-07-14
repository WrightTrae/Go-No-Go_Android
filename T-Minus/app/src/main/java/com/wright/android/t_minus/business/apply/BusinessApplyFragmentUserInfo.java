package com.wright.android.t_minus.business.apply;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.TextFieldUtils;

public class BusinessApplyFragmentUserInfo extends Fragment {

    private BusinessApplyButtonClicks mListener;
    private EditText et_user_name;
    private EditText et_user_email;
    private EditText et_user_number;

    public BusinessApplyFragmentUserInfo() {
        // Required empty public constructor
    }

    public static BusinessApplyFragmentUserInfo newInstance() {
        BusinessApplyFragmentUserInfo fragment = new BusinessApplyFragmentUserInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_apply_user_info, container, false);
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
        et_user_name = view.findViewById(R.id.business_apply_your_name);
        et_user_email = view.findViewById(R.id.business_apply_your_email);
        et_user_number = view.findViewById(R.id.business_apply_your_number);
        view.findViewById(R.id.business_apply_next_btn).setOnClickListener((View v)->checkFields());
    }

    private void checkFields() {

        // Reset errors.
        et_user_name.setError(null);
        et_user_number.setError(null);
        et_user_email.setError(null);

        String name = et_user_name.getText().toString();
        String number = et_user_number.getText().toString();
        String email = et_user_email.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            et_user_name.setError(getString(R.string.error_field_required));
            focusView = et_user_name;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            et_user_email.setError(getString(R.string.error_field_required));
            focusView = et_user_name;
            cancel = true;
        } else if (!TextFieldUtils.isEmailValid(email)) {
            et_user_email.setError(getString(R.string.error_invalid_email));
            focusView = et_user_number;
            cancel = true;
        }

        // Check for a valid number
        if (TextUtils.isEmpty(number)) {
            et_user_number.setError(getString(R.string.error_field_required));
            focusView = et_user_number;
            cancel = true;
        } else if(TextFieldUtils.isPhoneNumberInvalid(number)){
            et_user_number.setError(getString(R.string.error_invalid_number));
            focusView = et_user_number;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mListener.userInfoNextClick(et_user_name.getText().toString(), et_user_email.getText().toString(),
                    et_user_number.getText().toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
