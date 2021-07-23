package com.evelynhasama.bucketbuds;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    public static void callApi(Context context, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) { ;
        Request<String> request = new StringRequest(url, listener, errorListener);
        ApiHelper.getInstance(context).addToRequestQueue(request);
    }

}
