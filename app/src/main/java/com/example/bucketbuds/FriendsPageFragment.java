package com.example.bucketbuds;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

public class FriendsPageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_USER_PUB = "ARG_USER_PUB";
    public static final String TAG = "PageFragment";
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
    UserPub userPub;
    public List<String> friendIds;


    public static FriendsPageFragment newInstance(int page, UserPub userPub) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putParcelable(ARG_USER_PUB, userPub);
        FriendsPageFragment fragment = new FriendsPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendIds = new ArrayList<>();
        mPage = getArguments().getInt(ARG_PAGE);
        userPub = getArguments().getParcelable(ARG_USER_PUB);
        user = User.getCurrentUser();
        if (mPage == FRIENDS_PAGE) {
            friends = new ArrayList<>();
            return;
        }
        addFriends = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        List<String> friendIds = new ArrayList<>();
        if (mPage == FRIENDS_PAGE) {
            hideKeyboard();
            friendsAdapter.clear();
            getFriends();
            return;
        }
        addFriendsAdapter.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        RecyclerView rvUsers = view.findViewById(R.id.rvFriends);
        SearchView searchView = view.findViewById(R.id.svFriends);
        if (mPage == FRIENDS_PAGE) {
            friendsAdapter = new FriendsAdapter(getContext(), friends, userPub, true, this, new ArrayList<>());
            rvUsers.setAdapter(friendsAdapter);
            rvUsers.setLayoutManager(new LinearLayoutManager(context));
            searchView.setVisibility(View.GONE);
        } else {
            addFriendsAdapter = new FriendsAdapter(getContext(), addFriends, userPub,false, FriendsPageFragment.this, friendIds);
            rvUsers.setAdapter(addFriendsAdapter);
            rvUsers.setLayoutManager(new LinearLayoutManager(context));
            searchView.setVisibility(View.VISIBLE);
        }

        SearchView.OnQueryTextListener searchQueryTextListener = new SearchView.OnQueryTextListener() {
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
        };

        searchView.setOnQueryTextListener(searchQueryTextListener);
        return view;
    }

    // gets the users who is friends with the current user
    private void getFriends() {
        userPub.getFriendsRelation().getQuery().include(User.KEY_USER_PUB).findInBackground(getMyFindCallback(friendsAdapter, friends, true));
    }

    // gets the users who are not friends with the current user and who's username starts with the query
    private void addFriends(String queryPrefix) {
        friendIds.clear();
        FindCallback<ParseUser> addFriendsFindCallback =  new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "issue with adding friend ids", e);
                    return;
                }
                for (int i = 0; i < objects.size(); i++) {
                    friendIds.add(objects.get(i).getObjectId());
                }
                ParseQuery<ParseUser> query = ParseQuery.getQuery(User.USER_CLASS);
                query.whereNotEqualTo(User.KEY_OBJECT_ID, User.getCurrentUser().getObjectId());
                query.whereStartsWith(User.KEY_USERNAME, queryPrefix);
                query.include(User.KEY_USER_PUB);
                query.findInBackground(getMyFindCallback(addFriendsAdapter, addFriends, false));
            }
        };
        userPub.getFriendsRelation().getQuery().findInBackground(addFriendsFindCallback);
    }

    // helper method for adding all users when querying
    public FindCallback getMyFindCallback(FriendsAdapter adapter, List<User> users, Boolean friendsBool){
        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "issue with adding users to list", e);
                    return;
                }
                for (int i = 0; i < allUsers.size(); i++) {
                    users.add(new User(allUsers.get(i)));
                }
                // search result for friend is empty
                if (!friendsBool && users.isEmpty()) {
                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        };
    }


    public void hideKeyboard() {
        Activity activity = getActivity();
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}

