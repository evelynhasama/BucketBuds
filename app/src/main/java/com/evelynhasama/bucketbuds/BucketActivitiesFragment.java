package com.evelynhasama.bucketbuds;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class BucketActivitiesFragment extends Fragment {

    private static final String ARG_BUCKET_LIST = "bucketList";

    BucketList bucketList;
    View view;
    List<User> users;
    BucketUsersAdapter bucketUsersAdapter;

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

        Switch swCompleted = view.findViewById(R.id.swCompletedFBA);
        TextView tvBucketName = view.findViewById(R.id.tvBucketNameFBA);
        TextView tvBucketDescription = view.findViewById(R.id.tvBucketDescriptionFBA);
        ImageView ivBucketImage = view.findViewById(R.id.ivBucketImageFBA);
        Button btnEditBucket = view.findViewById(R.id.btnEditBucketFBA);
        Button btnAddActivity = view.findViewById(R.id.btnAddActivityFBA);
        RecyclerView rvBucketUsers = view.findViewById(R.id.rvBucketFriendsFBA);
        RecyclerView rvBucketActivities = view.findViewById(R.id.rvActivitiesFBA);

        Glide.with(getContext()).load(bucketList.getImage().getUrl()).centerCrop()
                .transform(new RoundedCorners(10)).into(ivBucketImage);
        tvBucketName.setText(bucketList.getName());
        tvBucketDescription.setText(bucketList.getDescription());
        swCompleted.setChecked(bucketList.getCompleted() ? true : false);

        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: open dialog to create activity
            }
        });

        btnEditBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: open dialog to edit
            }
        });

        users = new ArrayList<>();
        bucketUsersAdapter = new BucketUsersAdapter(getContext(), users);
        rvBucketUsers.setAdapter(bucketUsersAdapter);
        rvBucketUsers.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.HORIZONTAL, false));

        bucketList.getUsersRelation().getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                for (ParseUser user: objects){
                    users.add(new User(user));
                    bucketUsersAdapter.notifyDataSetChanged();
                }
            }
        });

        // TODO: recycler view activities
        
        return view;
    }

}