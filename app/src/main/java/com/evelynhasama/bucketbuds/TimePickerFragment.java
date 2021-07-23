package com.evelynhasama.bucketbuds;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    Context context;
    ActivityDetailsFragment activityDetailsFragment;
    Boolean startTime;

    public TimePickerFragment(Context context, ActivityDetailsFragment activityDetailsFragment, Boolean startTime) {
        this.context = context;
        this.activityDetailsFragment = activityDetailsFragment;
        this.startTime = startTime;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.DialogTheme, (TimePickerDialog.OnTimeSetListener)
                activityDetailsFragment, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        String title = startTime? "Start Time" : "End Time";
        timePickerDialog.setTitle(title);
        return timePickerDialog;
    }

}
