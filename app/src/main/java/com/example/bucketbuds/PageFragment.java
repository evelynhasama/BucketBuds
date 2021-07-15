package com.example.bucketbuds;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "PageFragment";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String USER_CLASS = "_User";
    public static final String KEY_USERNAME = "username";
    public static final int FRIENDS_PAGE = 0;
    public static final String KEY_FRIENDS = "friends";

    private int mPage;
    private List<User> friends;
    private List<User> addFriends;
    Context context;
    View view;
    FriendsAdapter friendsAdapter;
    FriendsAdapter addFriendsAdapter;
    User user;


    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        user = User.getCurrentUser();
        if (mPage == FRIENDS_PAGE) {
            friends = new ArrayList<>();
        } else {
            addFriends = new ArrayList<>();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPage == FRIENDS_PAGE) {
            friendsAdapter.clear();
            getFriends();
        } else {
            addFriendsAdapter.clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        RecyclerView rvUsers = view.findViewById(R.id.rvFriends);
        SearchView searchView = view.findViewById(R.id.svFriends);
        if (mPage == FRIENDS_PAGE) {
            friendsAdapter = new FriendsAdapter(getContext(), friends, true, this);
            rvUsers.setAdapter(friendsAdapter);
            rvUsers.setLayoutManager(new LinearLayoutManager(context));
            searchView.setVisibility(View.GONE);
        } else {
            addFriendsAdapter = new FriendsAdapter(getContext(), addFriends, false, this);
            rvUsers.setAdapter(addFriendsAdapter);
            rvUsers.setLayoutManager(new LinearLayoutManager(context));
            searchView.setVisibility(View.VISIBLE);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Reset SearchView
                addFriendsAdapter.clear();
                addFriends(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    // gets the users who is friends with the current user
    private void getFriends() {
        user.getFriendsQuery(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> objects, ParseException e) {
                Friends friendsObj = objects.get(0);
                friendsObj.getFriendsRelation().getQuery().findInBackground(getMyFindCallback(friendsAdapter, friends, true));
            }
        });
    }

    // gets the users who are not friends with the current user and who's username starts with the query
    private void addFriends(String queryPrefix) {
        List<String> friendIds = new ArrayList<>();
        user.getFriendsQuery(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> objects, ParseException e) {
                Friends friendsObj = objects.get(0);
                friendsObj.getFriendsRelation().getQuery().findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> friendUsers, ParseException e) {
                        if (e == null){
                            for (int i = 0; i < friendUsers.size(); i++) {
                                friendIds.add(friendUsers.get(i).getObjectId());
                            }
                            ParseQuery<ParseUser> query = ParseQuery.getQuery(USER_CLASS);
                            query.whereNotEqualTo(KEY_OBJECT_ID, User.getCurrentUser().getObjectId());
                            query.whereNotContainedIn(KEY_OBJECT_ID, friendIds);
                            query.whereStartsWith(KEY_USERNAME, queryPrefix);
                            query.include(KEY_FRIENDS);
                            query.findInBackground(getMyFindCallback(addFriendsAdapter, addFriends, false));
                        } else {
                            Log.d(TAG, "add friends, get friends relation failed: " + e);
                        }
                    }
                });
            }
        });
    }

    // helper method for adding all users when querying
    public FindCallback getMyFindCallback(FriendsAdapter adapter, List<User> users, Boolean friendsBool){
        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                if (e == null){
                    for (int i = 0; i < allUsers.size(); i++) {
                        users.add(new User(allUsers.get(i)));
                    }
                    // search result for friend is empty
                    if (!friendsBool && users.isEmpty()){
                        Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();;
                } else {
                    Log.d(TAG, "add friends failed: " + e);
                }
            }
        };
    }

}

