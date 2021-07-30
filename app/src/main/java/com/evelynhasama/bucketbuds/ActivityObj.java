package com.evelynhasama.bucketbuds;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_WEB = "web";
    public static final String KEY_UPDATED = "updatedAt";
    public static final String KEY_EVENT_CREATED = "eventCreated";
    public static final String KEY_ALL_DAY = "allDay";

    public static final int TICKETMASTER = 1;
    public static final int SEATGEEK = 2;
    public static final int MUSEMENT = 3;

    int mCompany;

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

    public Boolean getAllDayBool() {
        return getBoolean(KEY_ALL_DAY);
    }

    public void setAllDayBool(Boolean allDay) {
        put(KEY_ALL_DAY, allDay);
    }


    public void setStartDate(Date date) {
        put(KEY_START_DATE, date);
    }

    public Date getStartDate() {
        return getDate(KEY_START_DATE);
    }

    public void setEndDate(Date date) {
        put(KEY_END_DATE, date);
    }

    public Date getEndDate() {
        return getDate(KEY_END_DATE);
    }


    public String getWeb() {
        return getString(KEY_WEB);
    }

    public void setWeb(String web) {
        put(KEY_WEB, web);
    }

    public Boolean getEventCreated(){
        return getBoolean(KEY_EVENT_CREATED);
    }

    public void setEventCreated(){
       put(KEY_EVENT_CREATED, true);
    }

    public void setCompany(int company) {
        mCompany = company;
    }

    public int getCompany() {
        return mCompany;
    }

}
