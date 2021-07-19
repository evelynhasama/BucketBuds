package com.evelynhasama.bucketbuds;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class User{

    private ParseUser user;

    public User(ParseUser parseUser) {
        user = parseUser;
    }

    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_BIO = "bio";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER_PUB = "userPub";
    public static final String KEY_USERPUB_CLASS = "UserPub";
    public static final String TAG = "UserClass";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String USER_CLASS = "_User";
    public static final String KEY_USERNAME = "username";

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

    public UserPub getUserPub() {
        return (UserPub) user.getParseObject(KEY_USER_PUB);
    }

    public UserPub getUserPubQuery() {
        ParseQuery<UserPub> query = ParseQuery.getQuery(KEY_USERPUB_CLASS);
        try {
            return query.get(getUserPub().getObjectId());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "returning getUserPubQuery() null");
            return null;
        }
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
