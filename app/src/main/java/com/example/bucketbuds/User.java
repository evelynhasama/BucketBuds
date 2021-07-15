package com.example.bucketbuds;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class User{

    private ParseUser user;

    public User(ParseUser parseUser) {
        user = parseUser;
    }

    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_BIO = "bio";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_BUCKET_COUNT = "bucketCount";
    public static final String KEY_FRIENDS_CLASS = "Friends";
    public static final String TAG = "UserClass";

    public String getUsername(){
        return user.getUsername();
    }

    public void setUsername(String username){
        user.setEmail(username);
    }

    public String getEmail(){
        return user.getEmail();
    }

    public void setEmail(String email){
        user.setEmail(email);
    }

    public int getBucketCount() {
        return user.getInt(KEY_BUCKET_COUNT);
    }

    public void setBucketCount(int bucketCount) {
        user.put(KEY_BUCKET_COUNT, bucketCount);
    }

    public String getFirstName() {
        return user.getString(KEY_FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        user.put(KEY_FIRST_NAME, firstName);
    }

    public String getLastName() {
        return user.getString(KEY_LAST_NAME);
    }

    public void setLastName(String lastName) {
        user.put(KEY_LAST_NAME, lastName);
    }

    public String getBio() {
        return user.getString(KEY_BIO);
    }

    public void setBio(String bio) {
        user.put(KEY_BIO, bio);
    }

    public ParseFile getImage() {
        return user.getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        user.put(KEY_IMAGE, image);
    }

    public void getFriendsQuery(FindCallback<Friends> findCallback) {
        ParseQuery<Friends> query = ParseQuery.getQuery(KEY_FRIENDS_CLASS);
        query.whereEqualTo("user", user);
        query.findInBackground(findCallback);
    }

    public void saveInBackground(SaveCallback saveCallback) {
        user.saveInBackground(saveCallback);
    }

    public ParseUser getParseUser() {
        return user;
    }

    public String getObjectId() {
        return user.getObjectId();
    }

    public static User getCurrentUser() {
        return new User(ParseUser.getCurrentUser());
    }

}
