package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;

public class MusementHelper {
    public static final String TAG = "MusementHelper";
    public static final String BASE_URL = "https://sandbox.musement.com/api/v3/activities?";
    public static final String PARAM_COORDS = "coordinates=";
    public static final String PARAM_DISTANCE = "&distance=";
    public static final String UNIT="M";

    public static void getEvents(Context context, Double latitude, Double longitude, InspoActivitiesAdapter adapter, String radiusFilter) {

        String coords = latitude + "," + longitude;
        String url = BASE_URL + PARAM_COORDS + coords + PARAM_DISTANCE + radiusFilter + UNIT;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray events = responseObject.getJSONArray("data");
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject event = events.getJSONObject(i);
                        ActivityObj activityObj = parseEvent(event);
                        adapter.addData(activityObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "done parsing at radius " + radiusFilter);
                adapter.notifyDataSetChanged();
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
        activityObj.setName(event.getString("title"));
        // set web
        activityObj.setWeb(event.getString("url"));
        // set all day boolean
        activityObj.setAllDayBool(false);
        // set location
        activityObj.setLocation(event.getString("meeting_point"));
        // set description
        activityObj.setDescription(event.getString("about"));
        // set company to Ticketmaster
        activityObj.setCompany(ActivityObj.MUSEMENT);
        return activityObj;
    }

}