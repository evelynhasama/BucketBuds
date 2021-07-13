package com.example.bucketbuds;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.bumptech.glide.Glide;

import java.io.File;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    View view;
    File photoFile;
    TextView tvUsername;
    TextView tvFirst;
    TextView tvLast;
    TextView tvBio;
    TextView tvFriendCount;
    TextView tvBucketCount;
    ImageView ivProfileImage;
    Button btnEditProfile;
    TabLayout tabLayout;
    ViewPager viewPager;
    User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = new User(ParseUser.getCurrentUser());
        tvUsername = view.findViewById(R.id.tvUsernamePF);
        tvFirst = view.findViewById(R.id.tvFirstNamePF);
        tvLast = view.findViewById(R.id.tvLastNamePF);
        tvBio = view.findViewById(R.id.tvBioPF);
        tvFriendCount = view.findViewById(R.id.tvFriendCountPF);
        tvBucketCount = view.findViewById(R.id.tvBucketCountPF);
        ivProfileImage = view.findViewById(R.id.ivProfileImagePF);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tabLayout = view.findViewById(R.id.sliding_tabsPF);
        viewPager = view.findViewById(R.id.viewpagerPF);

        ParseFile image = user.getImage();
        if (image == null){
            Glide.with(view).load(R.drawable.profile_placeholder).transform(new CenterCrop(), new RoundedCorners(12)).into(ivProfileImage);
        } else {
            Glide.with(view).load(image.getUrl()).centerCrop().transform(new RoundedCorners(12)).into(ivProfileImage);
        }
        tvFirst.setText(user.getFirstName());
        tvLast.setText(user.getLastName());
        tvUsername.setText("@"+user.getUsername());
        tvFriendCount.setText(String.valueOf(user.getFriendCount()));
        tvBucketCount.setText(String.valueOf(user.getBucketCount()));

        String bio = user.getBio();
        if (bio == null) {
            tvBio.setVisibility(View.INVISIBLE);
        } else {
            tvBio.setVisibility(View.VISIBLE);
            tvBio.setText(bio);
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Edit Profile", Toast.LENGTH_LONG).show();
                // TODO: Open dialog to edit profile
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Change Profile Picture", Toast.LENGTH_LONG).show();
                // TODO: Open camera to change profile picture
            }
        });

        return view;
    }

}