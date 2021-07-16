package com.example.bucketbuds;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class CreateBucketFragment extends Fragment {

    View view;
    EditText etBucketName;
    EditText etBucketDescription;
    RecyclerView rvBucketFriends;
    ImageView ivBucketImage;

    public CreateBucketFragment() {
        // Required empty public constructor
    }

    public static CreateBucketFragment newInstance() {
        CreateBucketFragment fragment = new CreateBucketFragment();
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
        view = inflater.inflate(R.layout.fragment_create_bucket, container, false);
        etBucketName = view.findViewById(R.id.etBucketNameFCB);
        etBucketDescription = view.findViewById(R.id.etBucketDescriptionFCB);
        rvBucketFriends = view.findViewById(R.id.rvBucketFriendsFCB);
        ivBucketImage = view.findViewById(R.id.ivBucketImageFCB);

        Glide.with(getContext()).load(R.drawable.bucket_placeholder).centerCrop().into(ivBucketImage);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem miLogout = menu.findItem(R.id.logout);
        MenuItem miCreate = menu.findItem(R.id.miCreate);
        miLogout.setVisible(false);
        miCreate.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.miCreate) {
            // TODO: process create bucket list
            FragmentActivity activity = getActivity();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, BucketsFragment.class, null).commit();
        }
        return super.onOptionsItemSelected(item);
    }

}