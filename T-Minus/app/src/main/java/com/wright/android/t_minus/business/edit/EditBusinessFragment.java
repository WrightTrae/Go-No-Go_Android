package com.wright.android.t_minus.business.edit;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.Business;

import java.util.ArrayList;

public class EditBusinessFragment extends Fragment {

    private static final String ARG_BUSINESS = "ARG_BUSINESS";

    private Business business;
    private ImageView thumbnail;
    private EditText name;
    private EditText description;
    private EditText[][] businessTimes;
    private EditText specials;

    public EditBusinessFragment() {

    }

    public static EditBusinessFragment newInstance(Business _business) {
        EditBusinessFragment fragment = new EditBusinessFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUSINESS, _business);
        fragment.setArguments(args);
        return fragment;
    }

    public boolean checkFields() {
        if(name == null || description == null || specials == null){
            return false;
        }
        // Reset errors.
        name.setError(null);
        description.setError(null);
        specials.setError(null);

        String nameString = name.getText().toString();
        String descriptionString = description.getText().toString();
        String specialString = specials.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(descriptionString)) {
            description.setError(getString(R.string.error_field_required));
            focusView = description;
            cancel = true;
        }

        if (TextUtils.isEmpty(nameString)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        if (TextUtils.isEmpty(specialString)) {
            specials.setError(getString(R.string.error_field_required));
            focusView = specials;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public Business getNewData(){
        if(name == null || description == null){
            return business;
        }
        business.setName(name.getText().toString());
        business.setDescription(description.getText().toString());
//        business.setSpecials(specials.getText().toString());
        return business;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getArguments() != null) {
            business = (Business) getArguments().getSerializable(ARG_BUSINESS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_business, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFieldsFromView(view);
        name.setText(business.getName());
        description.setText(business.getDescription());
    }

    private void getFieldsFromView(@NonNull View view){
        name = view.findViewById(R.id.business_edit_name);
        description = view.findViewById(R.id.business_edit_description);
        thumbnail = view.findViewById(R.id.business_edit_thumbnail);
        specials = view.findViewById(R.id.business_edit_specials);
        businessTimes = new EditText[][]{
                new EditText[]{view.findViewById(R.id.business_edit_monday_open_hours),
                        view.findViewById(R.id.business_edit_monday_close_hours)},
                new EditText[]{view.findViewById(R.id.business_edit_tuesday_open_hours),
                        view.findViewById(R.id.business_edit_tuesday_close_hours)},
                new EditText[]{view.findViewById(R.id.business_edit_wednesday_open_hours),
                        view.findViewById(R.id.business_edit_wednesday_close_hours)},
                new EditText[]{view.findViewById(R.id.business_edit_thursday_open_hours),
                        view.findViewById(R.id.business_edit_thursday_close_hours)},
                new EditText[]{view.findViewById(R.id.business_edit_friday_open_hours),
                        view.findViewById(R.id.business_edit_friday_close_hours)},
                new EditText[]{view.findViewById(R.id.business_edit_saturday_open_hours),
                        view.findViewById(R.id.business_edit_saturday_close_hours)},
                new EditText[]{view.findViewById(R.id.business_edit_sunday_open_hours),
                        view.findViewById(R.id.business_edit_sunday_close_hours)}
        };
        specials.setText("Half-price drinks during rocket launches.");
        for (EditText[] dayTime: businessTimes){
            dayTime[0].setText("11am");
            dayTime[1].setText("11pm");
//            for (EditText timeText: dayTime){
//                timeText.setOnClickListener((View v)->{
//                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
//                            (TimePicker picker, int hourOfDay, int minute) -> {
//
//                    }, 12, 0, false);
//                    timePickerDialog.show();
//                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                });
//            }
        }
    }
}
