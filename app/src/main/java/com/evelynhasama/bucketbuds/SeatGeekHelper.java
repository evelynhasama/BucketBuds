package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SeatGeekHelper implements IEventAPI{
    public static final String TAG = "SeatGeekHelper";
    public static final String BASE_URL = "https://api.seatgeek.com/2/events?";
    public static final String PARAM_LAT = "lat=";
    public static final String PARAM_LON = "&lon=";
    public static final String PARAM_RANGE = "&range=";
    public static final String UNIT = "mi";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static SeatGeekHelper mInstance;

    private SeatGeekHelper(){
    }

    public static synchronized SeatGeekHelper getInstance() {
        if (mInstance == null) {
            mInstance = new SeatGeekHelper();
        }
        return mInstance;
    }

    public void getEvents(Context context, Double latitude, Double longitude, InspoActivitiesAdapter adapter, String radiusFilter) {

        String latlong = PARAM_LAT + latitude + PARAM_LON + longitude;
        String apiKey = "&client_id=" + context.getString(R.string.seat_geek_client_id);
        String url = BASE_URL + latlong + PARAM_RANGE + radiusFilter + UNIT +  apiKey;

        Response.Listener<String> responseListener = ApiHelper.buildResponseListener(
            (responseObject -> {
                try {
                    return responseObject.getJSONArray("events");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null; }),
            (jsonObject -> {
                try {
                    return parseEvent(jsonObject);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                return null; }),
            adapter);

        Log.d(TAG, "Calling api: " + url);
        ApiHelper.callApi(context, url, responseListener);
    }

    public static ActivityObj parseEvent(JSONObject event) throws JSONException, ParseException {

        ActivityObj activityObj = new ActivityObj();
        // set name
        activityObj.setName(event.getString("title"));
        // set web
        activityObj.setWeb(event.getString("url"));
        // set start date
        String strDate = event.getString("datetime_local");
        Date startDate = SIMPLE_DATE_FORMAT.parse(strDate);
        activityObj.setAllDayBool(false);
        activityObj.setStartDate(startDate);
        // set location
        activityObj.setLocation(event.getJSONObject("venue").getString("name"));
        // set description
        activityObj.setDescription(event.getString("type"));
        // set company to Ticketmaster
        activityObj.setCompany(ActivityObj.SEATGEEK);
        return activityObj;
    }

}

