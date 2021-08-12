package com.evelynhasama.bucketbuds;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendProfileFragment extends Fragment {

    public static final String ARG_PARSE_FRIEND = "friend";
    public static final String ARG_FRIEND_USER_PUB = "friendUserPub";
    public static final String ARG_CURRENT_USER_PUB = "userPub";
    public static final String TAG = "FriendProfileFragment";

    UserPub currentUserPub;
    User friend;
    UserPub friendUserPub;
    View view;
    TextView tvUsername;
    TextView tvFirst;
    TextView tvLast;
    TextView tvBio;
    TextView tvFriendCount;
    TextView tvBucketCount;
    ImageView ivProfileImage;
    RecyclerView rvFriendBucketLists;
    List<BucketList> bucketLists;
    FriendBucketsAdapter adapter;

    public FriendProfileFragment() {
        // Required empty public constructor
    }

    public static FriendProfileFragment newInstance(ParseUser parseFriend, UserPub friendUserPub, UserPub currentUserPub) {
        FriendProfileFragment fragment = new FriendProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FRIEND_USER_PUB, friendUserPub);
        args.putParcelable(ARG_CURRENT_USER_PUB, currentUserPub);
        args.putParcelable(ARG_PARSE_FRIEND, parseFriend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ParseUser parseUser = getArguments().getParcelable(ARG_PARSE_FRIEND);
            friend = new User(parseUser);
            friendUserPub = getArguments().getParcelable(ARG_FRIEND_USER_PUB);
            currentUserPub = getArguments().getParcelable(ARG_CURRENT_USER_PUB);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friend_profile, container, false);

        tvFirst = view.findViewById(R.id.tvFirstNameFPF);
        tvLast = view.findViewById(R.id.tvLastNameFPF);
        tvBio = view.findViewById(R.id.tvBioFPF);
        tvFriendCount = view.findViewById(R.id.tvFriendCountFPF);
        tvBucketCount = view.findViewById(R.id.tvBucketCountFPF);
        ivProfileImage = view.findViewById(R.id.ivProfileImageFPF);
        tvUsername = view.findViewById(R.id.tvUsernameFPF);
        rvFriendBucketLists = view.findViewById(R.id.rvFriendBucketLists);

        ParseFile image = friend.getImage();
        if (image == null){
            Glide.with(view).load(R.drawable.profile_placeholder).transform(new CenterCrop(), new RoundedCorners(20)).into(ivProfileImage);
        } else {
            Glide.with(view).load(image.getUrl()).transform(new CenterCrop(), new RoundedCorners(20)).into(ivProfileImage);
        }
        tvFirst.setText(friend.getFirstName());
        tvLast.setText(friend.getLastName());
        tvUsername.setText("@"+friend.getUsername());
        tvFriendCount.setText(String.valueOf(friendUserPub.getFriendCount()));
        tvBucketCount.setText(String.valueOf(friendUserPub.getBucketCount()));
        String bio = (friend.getBio() == null) ? "" : friend.getBio();
        tvBio.setText(bio);

        bucketLists = new ArrayList<>();
        rvFriendBucketLists.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new FriendBucketsAdapter(getContext(), bucketLists);
        rvFriendBucketLists.setAdapter(adapter);
        getUserBuckets();

        return view;
    }

    private void getUserBuckets() {
        FindCallback<BucketList> findCallback = new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, String.valueOf(e));
                    return;
                }
                List<String> bucketIds = new ArrayList<>();
                for (BucketList bucketList: objects){
                    bucketIds.add(bucketList.getObjectId());
                }
                adapter.setMyBuckets(bucketIds);
                getFriendBuckets();
            }
        };
        currentUserPub.getBucketsRelation().getQuery().findInBackground(findCallback);
    }

    private void getFriendBuckets() {
        FindCallback<BucketList> findCallback = new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, String.valueOf(e));
                    return;
                }
                bucketLists.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        };
        friendUserPub.getBucketsRelation().getQuery().findInBackground(findCallback);
    }

}