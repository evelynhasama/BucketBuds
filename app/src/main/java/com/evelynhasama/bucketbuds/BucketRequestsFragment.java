package com.evelynhasama.bucketbuds;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class BucketRequestsFragment extends Fragment {

    public static final String TAG = "BucketRequestsFragment";
    View view;
    RecyclerView rvBucketRequests;
    BucketRequestsAdapter adapter;
    List<BucketRequest> bucketRequests;
    List<BucketList> bucketLists;

    public BucketRequestsFragment() {
    }

    public static BucketRequestsFragment newInstance() {
        BucketRequestsFragment fragment = new BucketRequestsFragment();
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
        view =  inflater.inflate(R.layout.fragment_bucket_requests, container, false);
        rvBucketRequests = view.findViewById(R.id.rvBucketRequests);
        bucketRequests = new ArrayList<>();
        adapter = new BucketRequestsAdapter(getContext(), bucketRequests);
        rvBucketRequests.setAdapter(adapter);
        rvBucketRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        getBucketRequests();

        return view;
    }

    private void getBucketRequests(){
        UserPub userPub = User.getCurrentUser().getUserPubQuery();
        userPub.getBucketsRelation().getQuery().findInBackground(new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, String.valueOf(e));
                    return;
                }
                bucketLists = objects;
                Log.d(TAG, "bucket lists size " + objects.size());
                getUsersBucketRequests();
            }
        });
    }

    private void getUsersBucketRequests() {
        // finds other user's requests to join user's buckets
        ParseQuery<ParseObject> otherRequestQuery = new ParseQuery<ParseObject>(BucketRequest.CLASS_NAME);
        otherRequestQuery.whereContainedIn(BucketRequest.KEY_BUCKET, bucketLists);

        // finds user's requests to join other buckets
        ParseQuery<ParseObject> userRequestQuery = new ParseQuery<ParseObject>(BucketRequest.CLASS_NAME);
        userRequestQuery.whereEqualTo(BucketRequest.KEY_FROM_USER, User.getCurrentUser().getParseUser());

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(otherRequestQuery);
        queries.add(userRequestQuery);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.include(BucketRequest.KEY_BUCKET).include(BucketRequest.KEY_FROM_USER).include(BucketRequest.KEY_RECEIVER)
                .addDescendingOrder(BucketRequest.KEY_CREATED_AT)
                .findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Here" + String.valueOf(e));
                    return;
                }
                for (ParseObject object: objects){
                    bucketRequests.add((BucketRequest) object);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}