package com.evelynhasama.bucketbuds;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("BucketRequest")
public class BucketRequest extends ParseObject{

    public static final String KEY_FROM_USER = "fromUser";
    public static final String KEY_RECEIVER = "receiver";
    public static final String KEY_BUCKET = "bucket";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String PENDING = "pending";
    public static final String APPROVED = "approved";
    public static final String DENIED = "denied";
    public static final String CLASS_NAME = "BucketRequest";

    public User getFromUser() {
        return new User(getParseUser(KEY_FROM_USER));
    }

    public void setFromUser(User fromUser) {
        put(KEY_FROM_USER, fromUser.getParseUser());
    }

    public User getReceiver() {
        return new User(getParseUser(KEY_RECEIVER));
    }

    public void setReceiver(User fromUser) {
        put(KEY_RECEIVER, fromUser.getParseUser());
    }

    public BucketList getBucket() {
        return (BucketList) getParseObject(KEY_BUCKET);
    }

    public void setBucket(BucketList bucket) {
        put(KEY_BUCKET, bucket);
    }

    public String getStatus() {
        return getString(KEY_STATUS);
    }

    public void setStatus(String status) {
        put(KEY_STATUS, status);
    }
}
