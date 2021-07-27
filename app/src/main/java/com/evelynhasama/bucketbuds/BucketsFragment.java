package com.evelynhasama.bucketbuds;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class BucketsFragment extends Fragment {

    public static final String TAG = "BucketsFragment";
    View view;
    RecyclerView rvBuckets;
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

        // set default sorting/filtering
        selectedFilterID = R.id.rbAll;
        selectedSortID = R.id.rbModified;

        rvBuckets.setLayoutManager(new GridLayoutManager(getContext(), 2));

        bucketLists = new ArrayList<>();
        adapter = new BucketListAdapter(getContext(), bucketLists, getActivity());
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
        if (selectedFilterID == R.id.rbCompleted){
            query = query.whereEqualTo(BucketList.KEY_COMPLETED, true);
        }
        if (selectedFilterID == R.id.rbActive){
            query = query.whereEqualTo(BucketList.KEY_COMPLETED, false);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        List<Integer> visibles = new ArrayList<>();
        visibles.add(MenuHelper.ADD);
        visibles.add(MenuHelper.TOOL);
        MenuHelper.onCreateOptionsMenu(menu, visibles);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MenuHelper.ADD) {
            FragmentActivity activity = getActivity();
            Fragment myFragment = new CreateBucketFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
        }
        else if (item.getItemId() == MenuHelper.TOOL){
            showSortFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }


}