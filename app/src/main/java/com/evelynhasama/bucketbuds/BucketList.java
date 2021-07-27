package com.evelynhasama.bucketbuds;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("BucketList")
public class BucketList extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_USERS = "users";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_BUCKET_UPDATED = "updatedAt";
    public static final String KEY_BUCKET_CREATED = "createdAt";
    public static final String KEY_ACTIVITIES = "activities";
    public static final String KEY_ACTIVITY_COUNT = "activityCount";
    public static final String KEY_USER_COUNT = "userCount";
    public static final String TAG = "BucketListClass";

    public BucketList(){}

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public Boolean getCompleted() {
        return getBoolean(KEY_COMPLETED);
    }

    public void setCompleted(Boolean completed) {
        put(KEY_COMPLETED, completed);
    }

    public ParseRelation<ParseUser> getUsersRelation() {
        return getRelation(KEY_USERS);
    }

    public void addFriends(List<User> friends) {
        ParseRelation<ParseUser> relation = getUsersRelation();
        for (User friend: friends) {
            relation.add(friend.getParseUser());
        }
        relation.add(ParseUser.getCurrentUser());
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public ParseRelation<ActivityObj> getActivitiesRelation() {
        return getRelation(KEY_ACTIVITIES);
    }

    public void addActivity(ActivityObj activityObj) {
        ParseRelation<ActivityObj> relation = getActivitiesRelation();
        relation.add(activityObj);
        setActivityCount(getActivityCount()+1);
    }

    public void removeActivity(ActivityObj activityObj) {
        ParseRelation<ActivityObj> relation = getActivitiesRelation();
        relation.remove(activityObj);
        setActivityCount(getActivityCount()-1);
    }

    public int getUserCount() {
        return getInt(KEY_USER_COUNT);
    }

    public void setUserCount(int userCount) {
        put(KEY_USER_COUNT, userCount);
    }

    public int getActivityCount() {
        return getInt(KEY_ACTIVITY_COUNT);
    }

    public void setActivityCount(int activityCount) {
        put(KEY_ACTIVITY_COUNT, activityCount);
    }

}
