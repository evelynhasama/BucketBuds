package com.evelynhasama.bucketbuds;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class BucketsFragment extends Fragment {

    public static final String TAG = "BucketsFragment";
    View view;
    RecyclerView rvBuckets;
    Button btnAddBucket;
    Button btnSortFilter;
    BucketListAdapter adapter;
    UserPub userPub;
    List<BucketList> bucketLists;
    int selectedFilterID;
    int selectedSortID;

    public BucketsFragment() {
        // Required empty public constructor
    }

    public static BucketsFragment newInstance() {
        BucketsFragment fragment = new BucketsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_buckets, container, false);
        rvBuckets = view.findViewById(R.id.rvBucketsFB);
        btnAddBucket = view.findViewById(R.id.btnAddBucketFB);
        btnSortFilter = view.findViewById(R.id.btnSortFB);

        // set default sorting/filtering
        selectedFilterID = R.id.rbAll;
        selectedSortID = R.id.rbModified;

        btnAddBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                Fragment myFragment = new CreateBucketFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
            }
        });

        btnSortFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortFilterDialog();
            }
        });

        rvBuckets.setLayoutManager(new GridLayoutManager(getContext(), 3));

        bucketLists = new ArrayList<>();
        adapter = new BucketListAdapter(getContext(), bucketLists, selectedFilterID, getActivity());
        rvBuckets.setAdapter(adapter);

        getBuckets();

        return view;
    }

    public void getBuckets(){

        userPub = User.getCurrentUser().getUserPubQuery();
        FindCallback<BucketList> bucketListFindCallback = new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> objects, ParseException e) {
                if (e == null) {
                    adapter.clear();
                    bucketLists.clear();
                    bucketLists.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "error adding bucket lists", e);
                }
            }
        };
        ParseQuery<BucketList> query = userPub.getBucketsRelation().getQuery();
        switch (selectedSortID) {
            case R.id.rbAlphabetical:
                query = query.addAscendingOrder(BucketList.KEY_NAME);
                break;
            case R.id.rbCreated:
                query = query.addDescendingOrder(BucketList.KEY_BUCKET_CREATED);
                break;
            default:
            case R.id.rbModified:
                query = query.addDescendingOrder(BucketList.KEY_BUCKET_UPDATED);
                break;
        }
        query.findInBackground(bucketListFindCallback);
    }

    public void showSortFilterDialog() {
        View messageView = LayoutInflater.from(getContext()).
                inflate(R.layout.dialog_sort_filter, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button btnSave = messageView.findViewById(R.id.btnSaveDSF);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDSF);

        RadioGroup rgSort = messageView.findViewById(R.id.rgSort);
        RadioGroup rgFilter = messageView.findViewById(R.id.rgFilter);

        rgSort.check(selectedSortID);
        rgFilter.check(selectedFilterID);

        // Configure dialog buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterID = rgFilter.getCheckedRadioButtonId();
                selectedSortID = rgSort.getCheckedRadioButtonId();
                adapter.filter(selectedFilterID);
                getBuckets();
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        // Display the dialog
        alertDialog.show();
    }

}