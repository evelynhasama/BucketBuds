package com.evelynhasama.bucketbuds;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

public class ApiTestHelper implements IEventAPI{

    private static ApiTestHelper mInstance;

    private ApiTestHelper() {

    }

    public static synchronized ApiTestHelper getInstance() throws IOException {
        if (mInstance == null) {
            mInstance = new ApiTestHelper();
        }
        return mInstance;
    }

    @Override
    public void getEvents(Context context, Double latitude, Double longitude, InspoActivitiesAdapter adapter, String radiusFilter){
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context));
            JSONArray events = jsonObject.getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                adapter.addData(parseEvent(event));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static ActivityObj parseEvent(JSONObject event) throws JSONException {
        ActivityObj activityObj = new ActivityObj();
        activityObj.setName(event.getString("name"));
        activityObj.setLocation(event.getString("location"));
        activityObj.setDescription(event.getString("description"));
        activityObj.setWeb(event.getString("web"));
        activityObj.setCompany(ActivityObj.TICKETMASTER);
        return activityObj;
    }

    public String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("test_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
