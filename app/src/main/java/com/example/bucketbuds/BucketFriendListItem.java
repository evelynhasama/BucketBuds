package com.example.bucketbuds;

public abstract class BucketFriendListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_FRIEND = 1;

    abstract public int getType();

}

