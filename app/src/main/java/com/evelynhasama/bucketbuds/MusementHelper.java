package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;

public class MusementHelper implements IEventAPI {
    public static final String TAG = "MusementHelper";
    public static final String BASE_URL = "https://sandbox.musement.com/api/v3/activities?";
    public static final String PARAM_COORDS = "coordinates=";
    public static final String PARAM_DISTANCE = "&distance=";
    public static final String UNIT="M";
    private static MusementHelper mInstance;

    private MusementHelper(){
    }

    public static synchronized MusementHelper getInstance() {
        if (mInstance == null) {
            mInstance = new MusementHelper();
        }
        return mInstance;
    }

    public void getEvents(Context context, Double latitude, Double longitude, InspoActivitiesAdapter adapter, String radiusFilter) {

        String coords = latitude + "," + longitude;
        String url = BASE_URL + PARAM_COORDS + coords + PARAM_DISTANCE + radiusFilter + UNIT;

        Response.Listener<String> responseListener = ApiHelper.buildResponseListener(
            (responseObject -> {
                try {
                    return responseObject.getJSONArray("data");
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
        // set all day boolean
        activityObj.setAllDayBool(false);
        // set location
        if (event.has("meeting_point")){
            activityObj.setLocation(event.getString("meeting_point"));
        }
        // set description
        activityObj.setDescription(event.getString("about"));
        // set company to Musement
        activityObj.setCompany(ActivityObj.MUSEMENT);
        return activityObj;
    }

}