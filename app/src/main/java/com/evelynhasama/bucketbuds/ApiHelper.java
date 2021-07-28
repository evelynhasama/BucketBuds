package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.function.Function;

public class ApiHelper {

    public static final String TAG = "ApiHelper";

    private static ApiHelper mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private ApiHelper(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ApiHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void callApi(Context context, String url, Response.Listener<String> listener) { ;
        Request<String> request = new StringRequest(url, listener, getErrorListener());
        ApiHelper.getInstance(context).addToRequestQueue(request);
    }

    static Response.Listener<String> buildResponseListener(Function<JSONObject, JSONArray> eventListFn, Function<JSONObject, ActivityObj> parseFn, InspoActivitiesAdapter adapter) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray events = eventListFn.apply(responseObject);
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject event = events.getJSONObject(i);
                        adapter.addData(parseFn.apply(event));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        };
    }

    static Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, String.valueOf(error));
            }
        };
    }

}
