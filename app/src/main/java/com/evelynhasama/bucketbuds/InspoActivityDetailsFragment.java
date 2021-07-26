package com.evelynhasama.bucketbuds;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InspoActivityDetailsFragment extends Fragment {

    private static final String ARG_ACTIVITY_OBJ = "activityObj";
    public static final String TAG = "InspoActivityDetailsFragment";
    public static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("M/dd/yyyy hh:mm a", Locale.US);
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("M/dd/yyyy", Locale.US);

    View view;
    TextView tvTitle;
    TextView tvDescription;
    TextView tvLocation;
    TextView tvWebsite;
    TextView tvStartDate;
    TextView tvEndDate;
    Spinner spAddBucket;
    Button btnAddBucket;
    List<BucketList> buckets;
    List<String> bucketNames;

    private ActivityObj mActivityObj;

    public InspoActivityDetailsFragment() {
        // Required empty public constructor
    }

    public static InspoActivityDetailsFragment newInstance(ActivityObj activityObj) {
        InspoActivityDetailsFragment fragment = new InspoActivityDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ACTIVITY_OBJ, activityObj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mActivityObj = getArguments().getParcelable(ARG_ACTIVITY_OBJ);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_inspo_activity_details, container, false);

        tvTitle = view.findViewById(R.id.tvTitleFIAD);
        tvDescription = view.findViewById(R.id.tvDescriptionFIAD);
        tvStartDate = view.findViewById(R.id.tvStartDateFIAD);
        tvLocation = view.findViewById(R.id.tvLocationFIAD);
        tvWebsite = view.findViewById(R.id.tvWebsiteFIAD);
        tvEndDate = view.findViewById(R.id.tvEndDateFIAD);
        spAddBucket = view.findViewById(R.id.spAddBucketFIAD);
        btnAddBucket = view.findViewById(R.id.btnAddActivityFIAD);

        tvTitle.setText(mActivityObj.getName());
        tvDescription.setText("Description: "+ mActivityObj.getDescription());
        tvWebsite.setText("Website: "+ mActivityObj.getWeb());
        tvLocation.setText("Location: "+ mActivityObj.getLocation());
        tvStartDate.setText(getDateText(true, mActivityObj.getStartDate()));
        tvEndDate.setText(getDateText(false, mActivityObj.getEndDate()));

        buckets =  new ArrayList<>();
        bucketNames = new ArrayList<>();
        bucketNames.add("Select a bucket list");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item , bucketNames);
        spAddBucket.setAdapter(spinnerAdapter);

        User.getCurrentUser().getUserPubQuery().getBucketsRelation().getQuery().findInBackground(new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, String.valueOf(e));
                    return;
                }
                for (BucketList bucketList: objects) {
                    bucketNames.add(bucketList.getName());
                }
                buckets.addAll(objects);
                spinnerAdapter.notifyDataSetChanged();
            }
        });

        btnAddBucket.setOnClickListener(getAddBucketClickListener());

        return view;
    }

    private View.OnClickListener getAddBucketClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = spAddBucket.getSelectedItemPosition();
                if (position == 0) {
                    Toast.makeText(getContext(), "Please select a bucket list", Toast.LENGTH_SHORT).show();
                    return;
                }
                // position - 1 since the 1st item in bucket names is the prompt
                BucketList bucketList = buckets.get(position-1);
                mActivityObj.setBucket(bucketList);

                SaveCallback bucketListSaveCallback = new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, String.valueOf(e));
                            return;
                        }
                        Toast.makeText(getContext(), "Added activity to "+ bucketNames.get(position), Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                };

                SaveCallback activitySaveCallback = new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, String.valueOf(e));
                            return;
                        }
                        bucketList.addActivity(mActivityObj);
                        bucketList.saveInBackground(bucketListSaveCallback);
                    }
                };

                mActivityObj.saveInBackground(activitySaveCallback);
            }
        };
    }

    public String getDateText(Boolean start, Date date){
        String text = start ? "Start: " : "End: ";
        SimpleDateFormat sdFormat = mActivityObj.getAllDayBool() ? DATE_FORMATTER : DATE_TIME_FORMATTER;
        if (date == null) {
            return text;
        }
        return text + (sdFormat.format(date));
    }

}