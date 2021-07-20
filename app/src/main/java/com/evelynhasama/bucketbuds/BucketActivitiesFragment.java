package com.evelynhasama.bucketbuds;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class BucketActivitiesFragment extends Fragment {

    private static final String ARG_BUCKET_LIST = "bucketList";

    BucketList bucketList;
    View view;

    public BucketActivitiesFragment() {
    }

    public static BucketActivitiesFragment newInstance(BucketList bucketList) {
        BucketActivitiesFragment fragment = new BucketActivitiesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BUCKET_LIST, bucketList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bucketList = getArguments().getParcelable(ARG_BUCKET_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bucket_activities, container, false);

        TextView tvCompleted = view.findViewById(R.id.tvCompletedFBA);
        TextView tvBucketName = view.findViewById(R.id.tvBucketNameFBA);
        TextView tvBucketDescription = view.findViewById(R.id.tvBucketDescriptionFBA);
        ImageView ivBucketImage = view.findViewById(R.id.ivBucketImageFBA);

        Glide.with(getContext()).load(bucketList.getImage().getUrl()).centerCrop().into(ivBucketImage);
        tvBucketName.setText(bucketList.getName());
        tvBucketDescription.setText(bucketList.getDescription());
        tvCompleted.setText(bucketList.getCompleted() ? "Completed" : "Active");

        return view;
    }

}