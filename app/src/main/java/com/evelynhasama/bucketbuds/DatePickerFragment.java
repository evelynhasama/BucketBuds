package com.evelynhasama.bucketbuds;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    public static final String TAG = "DatePickerFragment";
    Context context;
    ActivityDetailsFragment activityDetailsFragment;
    Boolean startDate;

    public DatePickerFragment(Context context, ActivityDetailsFragment activityDetailsFragment, Boolean startDate) {
        this.context = context;
        this.activityDetailsFragment = activityDetailsFragment;
        this.startDate = startDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog =  new DatePickerDialog(context, R.style.DialogTheme, (DatePickerDialog.OnDateSetListener)
        activityDetailsFragment , year, month, day);
        String title = startDate? "Start Date" : "End Date";
        datePickerDialog.setTitle(title);
        return  datePickerDialog;
    }

}

