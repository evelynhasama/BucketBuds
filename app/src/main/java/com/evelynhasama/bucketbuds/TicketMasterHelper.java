package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TicketMasterHelper {

    public static final String TAG = "TicketMasterHelper";
    public static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json?";
    public static final String PARAM_LATLONG = "latlong=";
    public static final String PARAM_RADIUS_UNIT = "&radius=50&unit=miles";
    public static final String PARAM_SORT = "&sort=distance,date,asc";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm:ss");

    public static void getEvents(Context context, Double latitude, Double longitude, InspoActivitiesAdapter adapter) {

        List<ActivityObj> tmActivities = new ArrayList<>();

        String latlong = latitude + "," + longitude;
        String apiKey = "&apikey=" + context.getString(R.string.ticket_master_api_key);
        String url = BASE_URL + PARAM_LATLONG + latlong + PARAM_RADIUS_UNIT + PARAM_SORT + apiKey;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray events = responseObject.getJSONObject("_embedded").getJSONArray("events");
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject event = events.getJSONObject(i);
                        ActivityObj activityObj = parseEvent(event);
                        tmActivities.add(activityObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "done parsing " + tmActivities.size());
                adapter.addData(tmActivities);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, String.valueOf(error));
            }
        };
        Log.d(TAG, "Calling api: " + url);
        ApiHelper.callApi(context, url, responseListener, errorListener);
    }

    public static ActivityObj parseEvent(JSONObject event) throws JSONException, ParseException {

        ActivityObj activityObj = new ActivityObj();
        // set name
        activityObj.setName(event.getString("name"));
        // set web
        activityObj.setWeb(event.getString("url"));
        // set start date
        JSONObject startDateObj =  event.getJSONObject("dates").getJSONObject("start");
        String strDate = startDateObj.getString("localDate");
        Date startDate = SIMPLE_DATE_FORMAT.parse(strDate);
        Boolean allDay = true;
        if (startDateObj.has("localTime")) {
            String strTime = startDateObj.getString("localTime");
            Date startTime = SIMPLE_DATE_FORMAT_TIME.parse(strTime);
            startDate.setHours(startTime.getHours());
            startDate.setMinutes(startTime.getMinutes());
            allDay = false;
        }
        activityObj.setAllDayBool(allDay);
        activityObj.setStartDate(startDate);
        // set location
        JSONArray venues = event.getJSONObject("_embedded").getJSONArray("venues");
        if (venues.length() > 0) {
            activityObj.setLocation(venues.getJSONObject(0).getString("name"));
        }
        // set description
        ArrayList<String> descriptors = new ArrayList<>();
        JSONObject classifications = event.getJSONArray("classifications").getJSONObject(0);
        ArrayList<String> classObjs = new ArrayList<>(
                Arrays.asList("segment", "genre", "subGenre"));
        for (String classObj: classObjs) {
            descriptors.add(classifications.getJSONObject(classObj).getString("name"));
        }
        String description = String.join(", ", descriptors);
        activityObj.setDescription(description);
        // set company to Ticketmaster
        activityObj.setCompany(ActivityObj.TICKETMASTER);
        return activityObj;
    }

}