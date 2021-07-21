package com.evelynhasama.bucketbuds;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

@ParseClassName("Activity")
public class ActivityObj extends ParseObject {

    public static final String TAG = "ActivityObj";
    public static final String KEY_BUCKET = "bucket";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DATE = "date";
    public static final String KEY_WEB = "web";
    public static final String KEY_UPDATED = "updatedAt";
    public static final String KEY_TIME = "time";


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

    public Date getDate(){
        String strDate = getString(KEY_DATE);
        if (strDate.isEmpty()) {
            return null;
        }
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        Date date = null;
        try {
            date = df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void setDate(Date date) {
        String strDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
        put(KEY_DATE, strDate);
    }

    public Date getTime(){
        String strTime = getString(KEY_TIME);
        if (strTime.isEmpty()) {
            return null;
        }
        DateFormat df = DateFormat.getTimeInstance(DateFormat.FULL);
        Date time = null;
        try {
            time = df.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public void setTime(Date time) {
        String strTime = DateFormat.getTimeInstance(DateFormat.FULL).format(time);
        put(KEY_TIME, strTime);
    }

    public String getWeb() {
        return getString(KEY_WEB);
    }

    public void setWeb(String web) {
        put(KEY_WEB, web);
    }

}
