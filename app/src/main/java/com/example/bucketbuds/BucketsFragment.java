package com.example.bucketbuds;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BucketsFragment extends Fragment {

    View view;
    RecyclerView rvBuckets;
    Button btnAddBucket;
    Button btnSortFilter;

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

        return view;
    }

}