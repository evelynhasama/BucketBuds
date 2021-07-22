package com.evelynhasama.bucketbuds;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CalendarHelper {


    public static final String TAG = "CalendarHelper";

    public static void createEventIntent(Context context, ActivityObj activityObj){

        Date startDate = activityObj.getStartDate();
        Date endDate = activityObj.getEndDate();
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(startDate.getYear() + 1900, startDate.getMonth(), startDate.getDate(), startDate.getHours(), startDate.getMinutes());
        Calendar endTime = Calendar.getInstance();
        endTime.set(endDate.getYear() + 1900, endDate.getMonth(), endDate.getDate(), endDate.getHours(), endDate.getMinutes());
        TimeZone tz = TimeZone.getDefault();

        List<String> attendeesEmails = new ArrayList<>();

        FindCallback<ParseUser> userFindCallback = new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                Log.d(TAG, "adding attendees "+ objects.size());
                for (ParseUser parseUser: objects){
                    User user = new User(parseUser);
                    UserPub userPub = user.getUserPubQuery();
                    attendeesEmails.add(userPub.getEmail());
                }

                String emailString = String.join(",", attendeesEmails);
                Log.d(TAG, "adding emails" + emailString);

                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, activityObj.getName())
                        .putExtra(CalendarContract.Events.DESCRIPTION, activityObj.getDescription())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, activityObj.getLocation())
                        .putExtra(CalendarContract.Events.EVENT_TIMEZONE, tz.getID())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE)
                        .putExtra(Intent.EXTRA_EMAIL, emailString);

                if (activityObj.getAllDayBool()) {
                    Log.d(TAG, "allDay");
                    intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                }
                context.startActivity(intent);
                activityObj.setEventCreated();
                SaveCallback activitySaveCallback = new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "activity obj saving event created", e);
                            return;
                        }
                    }
                };
                activityObj.saveInBackground(activitySaveCallback);
            }
        };

        activityObj.getBucket().getUsersRelation().getQuery()
                .whereNotEqualTo(User.KEY_OBJECT_ID, User.getCurrentUser().getObjectId())
                .findInBackground(userFindCallback);

    }
}
