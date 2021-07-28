package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import com.android.volley.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TicketMasterHelper implements IEventAPI{

    public static final String TAG = "TicketMasterHelper";
    public static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json?";
    public static final String PARAM_LATLONG = "latlong=";
    public static final String PARAM_RADIUS = "&radius=";
    public static final String PARAM_UNIT = "&unit=miles";
    public static final String PARAM_SIZE = "&size=10";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm:ss");
    private static TicketMasterHelper mInstance;

    private TicketMasterHelper(){
    }

    public static synchronized TicketMasterHelper getInstance() {
        if (mInstance == null) {
            mInstance = new TicketMasterHelper();
        }
        return mInstance;
    }

    public void getEvents(Context context, Double latitude, Double longitude, InspoActivitiesAdapter adapter, String radiusFilter) {


        String latlong = latitude + "," + longitude;
        String apiKey = "&apikey=" + context.getString(R.string.ticket_master_api_key);
        String url = BASE_URL + PARAM_LATLONG + latlong + PARAM_RADIUS + radiusFilter + PARAM_UNIT + PARAM_SIZE + apiKey;


        Response.Listener<String> responseListener = ApiHelper.buildResponseListener(
            (responseObject -> {
                try {
                    return responseObject.getJSONObject("_embedded").getJSONArray("events");
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
