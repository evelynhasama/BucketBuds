package com.evelynhasama.bucketbuds;

public abstract class BucketActivityItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ACTIVITY = 1;

    abstract public int getType();

}
