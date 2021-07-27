package com.evelynhasama.bucketbuds;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ActivityDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private static final String ARG_ACTIVITY_OBJ = "activityObj";
    public static final String TAG = "ActivityDetailsFragment";
    public static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("M/dd/yyyy hh:mm a", Locale.US);
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("M/dd/yyyy", Locale.US);
    public static final int REQUEST_PERMISSION_CODE = 133;

    private ActivityObj activityObj;
    View view;
    TextView tvTitle;
    TextView tvDescription;
    TextView tvLocation;
    TextView tvWebsite;
    TextView tvStartDate;
    TextView tvEndDate;
    Button btnCalendarEvent;
    Boolean allDay;
    Switch swAllDay;
    Boolean afterSetStart; // false when setting the start date/time and true after
    TextView tvCalEventStatus;
    Button btnDeleteActivity;
    Menu mMenu;

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
        setHasOptionsMenu(true);
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
        tvStartDate = view.findViewById(R.id.tvStartDateFAD);
        tvLocation = view.findViewById(R.id.tvLocationFAD);
        tvWebsite = view.findViewById(R.id.tvWebsiteFAD);
        tvEndDate = view.findViewById(R.id.tvEndDateFAD);
        btnCalendarEvent = view.findViewById(R.id.btnCalendarEventFAD);
        swAllDay = view.findViewById(R.id.swAllDay);
        tvCalEventStatus = view.findViewById(R.id.tvCalStatusFAD);
        btnDeleteActivity = view.findViewById(R.id.btnDeleteActivityFAD);

        tvTitle.setText(activityObj.getName());
        tvDescription.setText("Description: "+ activityObj.getDescription());
        tvWebsite.setText("Website: "+ activityObj.getWeb());
        tvLocation.setText("Location: "+ activityObj.getLocation());
        allDay = activityObj.getAllDayBool();
        Boolean checked = allDay? true : false;
        swAllDay.setChecked(checked);

        if (activityObj.getStartDate() != null){
           tvStartDate.setText(getDateText(true, activityObj.getStartDate()));
           tvEndDate.setText(getDateText(false, activityObj.getEndDate()));
        } else {
            tvStartDate.setText("Start: " );
            tvEndDate.setText("End: " );
        }

        if (activityObj.getEventCreated()){
            eventAlreadyCreated();
        } else {
            tvCalEventStatus.setText("Create the event after your dates are finalized");
        }

        swAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                allDay = isChecked;
                activityObj.setAllDayBool(allDay);
                saveActivityObj("saving all day ");
                changeDatesAllDay();
            }
        });

        btnCalendarEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityObj.getStartDate() == null || activityObj.getEndDate() == null){
                    Toast.makeText(getContext(), "Please set start and end dates", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_PERMISSION_CODE);
                    return;
                }
                CalendarHelper.createEventIntent(getContext(), activityObj);
                eventAlreadyCreated();
            }
        });

        btnDeleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BucketList bucketList = activityObj.getBucket();
                bucketList.removeActivity(activityObj);
                activityObj.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            }
        });

        return view;
    }

    public void showDatePickerDialog(View v, Boolean startDate) {
        DialogFragment dateFragment = new DatePickerFragment(getContext(), this, startDate);
        dateFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v, Boolean startTime) {
        DialogFragment timeFragment = new TimePickerFragment(getContext(), this, startTime);
        timeFragment.show(getActivity().getSupportFragmentManager(), "timePicker");

    }

    @Override
    public void onDateSet(DatePicker dpView, int year, int month, int day) {
        Date date = new GregorianCalendar(year, month, day).getTime();
        // Start date
        if (!afterSetStart) {
            activityObj.setStartDate(date);
            if (allDay) {
                tvStartDate.setText(getDateText(true, date));
                showDatePickerDialog(view, false);
                afterSetStart = true;
                return;
            }
            showTimePickerDialog(view, true);
        }
        // End date
        else {
            if (allDay) {
                if (date.before(activityObj.getStartDate())) {
                    Toast.makeText(getContext(), "End date must be after start date", Toast.LENGTH_SHORT).show();
                } else {
                    tvEndDate.setText(getDateText(false, date));
                    activityObj.setEndDate(date);
                }
                saveActivityObj("error saving dates ");
                return;
            }
            activityObj.setEndDate(date);
            showTimePickerDialog(view, false);
        }
    }

    @Override
    public void onTimeSet(TimePicker tpView, int hourOfDay, int minute) {
        // Start time
        if (!afterSetStart){
            Date date = activityObj.getStartDate();
            Date datetime = new Date(date.getYear(), date.getMonth(), date.getDate(), hourOfDay, minute);
            tvStartDate.setText(getDateText(true, datetime));
            activityObj.setStartDate(datetime);
            afterSetStart = true;
            showDatePickerDialog(view, false);
        }
        // End time
        else {
            Date date = activityObj.getEndDate();
            Date datetime = new Date(date.getYear(), date.getMonth(), date.getDate(), hourOfDay, minute);
            if (datetime.before(activityObj.getStartDate())) {
                Toast.makeText(getContext(), "End date must be after start date", Toast.LENGTH_SHORT).show();
            } else {
                tvEndDate.setText(getDateText(false, datetime));
                activityObj.setEndDate(datetime);
            }
            saveActivityObj("error saving dates with times ");
        }
    }

    public String getDateText(Boolean start, Date date){
        String text = start ? "Start: " : "End: ";
        SimpleDateFormat sdFormat = allDay ? DATE_FORMATTER : DATE_TIME_FORMATTER;
        if (date == null) {
            return text;
        }
        return text + (sdFormat.format(date));
    }

    public void saveActivityObj(String errorDescription){
        activityObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, errorDescription + e);
                }
            }
        });
    }

    public void changeDatesAllDay(){
        if (activityObj.getStartDate() == null && activityObj.getEndDate() == null){
            return;
        }
        Date startDate = activityObj.getStartDate();
        Date endDate = activityObj.getEndDate();
        if (allDay){
            // change from date and time to just date
            if (startDate != null) {
                startDate.setHours(0);
                startDate.setMinutes(0);
                activityObj.setStartDate(startDate);
            }
            if (endDate != null){
                endDate.setHours(0);
                endDate.setMinutes(0);
                activityObj.setEndDate(endDate);
            }
            saveActivityObj("error saving all day dates ");
        }
        tvEndDate.setText(getDateText(false, endDate));
        tvStartDate.setText(getDateText(true, startDate));

    }

    public void eventAlreadyCreated(){
        if (mMenu != null){
            MenuHelper.setInvisible(mMenu, MenuHelper.CALENDAR);
        }
        btnCalendarEvent.setVisibility(View.GONE);
        swAllDay.setClickable(false);
        tvCalEventStatus.setText("This activity is already scheduled and a calendar invite has been sent");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CalendarHelper.createEventIntent(getContext(), activityObj);
                eventAlreadyCreated();
                return;
            }
            Toast.makeText(getContext(), "Please accept calendar permission requests to create events", Toast.LENGTH_LONG).show();
        }
    }

    public void showEditActivityDialog(){
        View messageView = LayoutInflater.from(getContext()).
                inflate(R.layout.dialog_add_activity, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        EditText etTitle = messageView.findViewById(R.id.etTitleDAA);
        EditText etDescription = messageView.findViewById(R.id.etDescriptionDAA);
        EditText etLocation = messageView.findViewById(R.id.etLocationDAA);
        EditText etWebsite = messageView.findViewById(R.id.etWebsiteDAA);
        Button btnSave = messageView.findViewById(R.id.btnSaveDAA);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDAA);
        TextView tvDialogTitle = messageView.findViewById(R.id.tvTitleDAA);

        tvDialogTitle.setText("Edit Activity Info");
        etTitle.setText(activityObj.getName(), TextView.BufferType.EDITABLE);
        etDescription.setText(activityObj.getDescription(), TextView.BufferType.EDITABLE);
        etLocation.setText(activityObj.getLocation(), TextView.BufferType.EDITABLE);
        etWebsite.setText(activityObj.getWeb(), TextView.BufferType.EDITABLE);

        // Configure dialog buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                String location = etLocation.getText().toString();
                String web = etWebsite.getText().toString();

                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT);
                    return;
                }
                activityObj.setName(title);
                activityObj.setDescription(description);
                activityObj.setLocation(location);
                activityObj.setWeb(web);
                SaveCallback saveCallback = new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "error saving edited activity", e);
                            return;
                        }
                        tvTitle.setText(title);
                        tvDescription.setText("Description: "+ description);
                        tvLocation.setText("Location: " + location);
                        tvWebsite.setText("Website: "+ web);
                        alertDialog.dismiss();
                    }
                };
                activityObj.saveInBackground(saveCallback);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        inflater.inflate(R.menu.menu_main, menu);
        List<Integer> visibles = new ArrayList<>();
        if (!activityObj.getEventCreated()) {
            visibles.add(MenuHelper.CALENDAR);
        }
        visibles.add(MenuHelper.EDIT);
        MenuHelper.onCreateOptionsMenu(menu, visibles);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MenuHelper.EDIT) {
            showEditActivityDialog();
        }
        else if (item.getItemId() == MenuHelper.CALENDAR){
            afterSetStart = false;
            showDatePickerDialog(view, true);
        }
        return super.onOptionsItemSelected(item);
    }



}