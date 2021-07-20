package com.evelynhasama.bucketbuds;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.Date;

@ParseClassName("Activity")
public class ActivityObj extends ParseObject {

    public static final String KEY_BUCKET = "bucket";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DATE = "date";
    public static final String KEY_WEB = "web";


    public BucketList getBucket() {
        return (BucketList) getParseObject(KEY_BUCKET);
    }

    public void setBucket(BucketList bucket) {
        put(KEY_BUCKET, bucket);
    }

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

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public String getWeb() {
        return getString(KEY_WEB);
    }

    public void setWeb(String web) {
        put(KEY_WEB, web);
    }

}
