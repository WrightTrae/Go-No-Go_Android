package com.wright.android.t_minus.business.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.Business;

public class BusinessViewFragment extends Fragment {

    public static final String ARG_BUSINESS = "ARG_BUSINESS";

    private Business business;
//    private ImageView thumbnail;
    private TextView name;
    private TextView description;
    private TextView phone;
    private TextView address;
    private TextView[] businessTimes;
//    private TextView mondayOpen;
//    private TextView mondayClose;
//    private TextView tuesdayOpen;
//    private TextView tuesdayClose;
//    private TextView wednesdayOpen;
//    private TextView wednesdayClose;
//    private TextView thursdayOpen;
//    private TextView thursdayClose;
//    private TextView fridayOpen;
//    private TextView fridayClose;
//    private TextView saturdayOpen;
//    private TextView saturdayClose;
//    private TextView sundayOpen;
//    private TextView sundayClose;
//    private TextView specials;

    public BusinessViewFragment() {

    }

    public static BusinessViewFragment newInstance(Business _business) {
        BusinessViewFragment fragment = new BusinessViewFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFieldsFromView(view);
        name.setText(business.getName());
        description.setText(business.getDescription());
        phone.setText(String.format(getString(R.string.business_number), business.getNumber()));
        address.setText(String.format(getString(R.string.business_address), business.getAddress()));
    }

    private void getFieldsFromView(@NonNull View view){
//        thumbnail = view.findViewById(R.id.business_view_thumbnail);
        phone = view.findViewById(R.id.business_view_phone);
        address = view.findViewById(R.id.business_view_address);
        name = view.findViewById(R.id.business_view_name);
        description = view.findViewById(R.id.business_view_description);
        businessTimes = new TextView[]{
                view.findViewById(R.id.business_view_monday_hours),
                view.findViewById(R.id.business_view_tuesday_open_hours),
                view.findViewById(R.id.business_view_wednesday_hours),
                view.findViewById(R.id.business_view_thursday_hours),
                view.findViewById(R.id.business_view_friday_open_hours),
                view.findViewById(R.id.business_view_saturday_hours),
                view.findViewById(R.id.business_view_sunday_hours),
        };
        for (TextView dayTime: businessTimes){
            dayTime.setText("11am to 11pm");
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
//        mondayOpen = view.findViewById(R.id.business_view_monday_hours);
//        mondayClose = view.findViewById(R.id.business_view_monday_hours);
//        tuesdayOpen = view.findViewById(R.id.business_view_tuesday_hours);
//        tuesdayClose = view.findViewById(R.id.business_view_tuesday_hours);
//        wednesdayOpen = view.findViewById(R.id.business_view_wednesday_hours);
//        wednesdayClose = view.findViewById(R.id.business_view_wednesday_hours);
//        thursdayOpen = view.findViewById(R.id.business_view_thursday_hours);
//        thursdayClose = view.findViewById(R.id.business_view_thursday_hours);
//        fridayOpen = view.findViewById(R.id.business_view_friday_hours);
//        fridayClose = view.findViewById(R.id.business_view_friday_hours);
//        saturdayOpen = view.findViewById(R.id.business_view_saturday_hours);
//        saturdayClose = view.findViewById(R.id.business_view_saturday_hours);
//        sundayOpen = view.findViewById(R.id.business_view_sunday_hours);
//        sundayClose = view.findViewById(R.id.business_view_sunday_hours);
//        specials = view.findViewById(R.id.business_view_specials);
    }
}
