package com.evelynhasama.bucketbuds;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("UserPub")
public class UserPub extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FRIEND_COUNT = "friendCount";
    public static final String KEY_BUCKETS = "buckets";
    public static final String KEY_BUCKET_COUNT = "bucketCount";
    public static final String TAG = "UserPubClass";

    public UserPub(){}

    public ParseRelation<ParseUser> getFriendsRelation() {
        return getRelation(KEY_FRIENDS);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public void addFriend(User friend) {
        getFriendsRelation().add(friend.getParseUser());
        setFriendCount(getFriendCount() + 1);
    }

    public void removeFriend(User friend) {
        getFriendsRelation().remove(friend.getParseUser());
        setFriendCount(getFriendCount() - 1);
    }

    public int getFriendCount() {
        return getInt(KEY_FRIEND_COUNT);
    }

    public void setFriendCount(int friendCount) {
        put(KEY_FRIEND_COUNT, friendCount);
    }

    public ParseRelation<BucketList> getBucketsRelation() {
        return getRelation(KEY_BUCKETS);
    }

    public void addBucket(BucketList bucketList) {
        getBucketsRelation().add(bucketList);
        setBucketCount(getBucketCount() + 1);
    }

    public void removeBucket(BucketList bucketList) {
        getBucketsRelation().remove(bucketList);
        setBucketCount(getBucketCount() - 1);
    }

    public int getBucketCount() {
        return getInt(KEY_BUCKET_COUNT);
    }

    public void setBucketCount(int bucketCount) {
        put(KEY_BUCKET_COUNT, bucketCount);
    }

    public String getEmail() {
        return getString(KEY_EMAIL);
    }

    public void setEmail(String email) {
        put(KEY_EMAIL, email);
    }

}
