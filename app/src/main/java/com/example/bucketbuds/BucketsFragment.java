package com.example.bucketbuds;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class BucketsFragment extends Fragment {

    public static final String TAG = "BucketsFragment";
    public static final String KEY_BUCKET_UPDATED = "updatedAt";
    View view;
    RecyclerView rvBuckets;
    Button btnAddBucket;
    Button btnSortFilter;
    BucketListAdapter adapter;
    UserPub userPub;
    List<BucketList> bucketLists;

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

        btnAddBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                Fragment myFragment = new CreateBucketFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
            }
        });

        rvBuckets.setLayoutManager(new GridLayoutManager(getContext(), 3));

        bucketLists = new ArrayList<>();
        adapter = new BucketListAdapter(getContext(), bucketLists);
        rvBuckets.setAdapter(adapter);

        getBuckets();

        return view;
    }

    public void getBuckets(){
        Log.d(TAG, "getBuckets()");
        userPub = User.getCurrentUser().getUserPubQuery();
        userPub.getBucketsRelation().getQuery().addDescendingOrder(KEY_BUCKET_UPDATED).findInBackground(new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> objects, ParseException e) {
                if (e == null) {
                    adapter.clear();
                    bucketLists.clear();
                    bucketLists.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "error adding bucket lists", e);
                }

            }
        });

    }

}