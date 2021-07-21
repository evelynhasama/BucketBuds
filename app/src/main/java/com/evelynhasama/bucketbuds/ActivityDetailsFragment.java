package com.evelynhasama.bucketbuds;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.parse.ParseException;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ActivityDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private static final String ARG_ACTIVITY_OBJ = "activityObj";
    public static final String TAG = "ActivityDetailsFragment";

    private ActivityObj activityObj;
    View view;
    TextView tvTitle;
    TextView tvDescription;
    TextView tvLocation;
    TextView tvWebsite;
    TextView tvDate;
    TextView tvTime;
    Date activityDate;
    Date activityTime;
    Fragment fragment;

    public ActivityDetailsFragment() {
        // Required empty public constructor
    }

    public static ActivityDetailsFragment newInstance(ActivityObj activityObj) {
        ActivityDetailsFragment fragment = new ActivityDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ACTIVITY_OBJ, activityObj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activityObj = getArguments().getParcelable(ARG_ACTIVITY_OBJ);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_activity_details, container, false);

        tvTitle = view.findViewById(R.id.tvTitleFAD);
        tvDescription = view.findViewById(R.id.tvDescriptionFAD);
        tvDate = view.findViewById(R.id.tvDateFAD);
        tvLocation = view.findViewById(R.id.tvLocationFAD);
        tvWebsite = view.findViewById(R.id.tvWebsiteFAD);
        tvTime = view.findViewById(R.id.tvTimeFAD);

        tvTitle.setText(activityObj.getName());
        tvDescription.setText("Description: "+ activityObj.getDescription());
        tvWebsite.setText("Website: "+ activityObj.getWeb());
        tvLocation.setText("Location: "+ activityObj.getLocation());
        if (activityObj.getDate() != null){
           tvDate.setText(convertDateToString(activityObj.getDate()));
        } else {
            tvDate.setText("Date: " );
        }

        if (activityObj.getTime() != null){
            tvTime.setText(convertTimeToString(activityObj.getTime()));
        } else {
            tvTime.setText("Time: " );
        }

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        return view;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment timeFragment = new TimePickerFragment(getContext(), this);
        timeFragment.show(getActivity().getSupportFragmentManager(), "timePicker");

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Date date = new GregorianCalendar(year, month, day).getTime();
        tvDate.setText(convertDateToString(date));
        activityObj.setDate(date);
        activityObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.d(TAG, String.valueOf(e));
                    return;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment dateFragment = new DatePickerFragment(getContext(), this);
        dateFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Date time = new Date(0, 1, 1, hourOfDay, minute);
        tvTime.setText(convertTimeToString(time));
        activityObj.setTime(time);
        activityObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.d(TAG, String.valueOf(e));
                    return;
                }
            }
        });
    }

    public String convertDateToString(Date date){
        activityDate = date;
        String strDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
        return ("Date: " + strDate);
    }

    public String convertTimeToString(Date time){
        activityTime = time;
        String strTime = DateFormat.getTimeInstance(DateFormat.DEFAULT).format(time);
        return ("Time: " + strTime);
    }

}