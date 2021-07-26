package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;

public class BoredHelper {

    public static final String TAG = "BoredHelper";
    public static final String BASE_URL = "http://www.boredapi.com/api/activity/";

    public static void getActivity(Context context, Response.Listener<String> responseListener) {

        String url = BASE_URL;

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, String.valueOf(error));
            }
        };
        Log.d(TAG, "Calling api: " + url);
        ApiHelper.callApi(context, url, responseListener, errorListener);
    }

    public static ActivityObj parseActivity(JSONObject activity) throws JSONException, ParseException {

        ActivityObj activityObj = new ActivityObj();
        // set name
        activityObj.setName(activity.getString("activity"));
        // set web
        activityObj.setWeb(activity.getString("link"));
        // set all day bool
        activityObj.setAllDayBool(false);
        // set description
        activityObj.setDescription(activity.getString("type"));
        // set location
        activityObj.setLocation("");
        return activityObj;
    }

}
