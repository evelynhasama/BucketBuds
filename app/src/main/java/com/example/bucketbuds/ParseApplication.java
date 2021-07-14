package com.example.bucketbuds;

import android.app.Application;

import com.parse.Parse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    public static final String MY_APP_ID = "xpYL3sNh2Sn5pRcnRjuV2nTPSNq9uePvZj2y7OVs";
    public static final String MY_CLIENT_KEY = "rTXulgUrk1qTpKgP2bTbeBvtHdHIyDOdD3R8jjuY";

    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See https://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the back4app settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(MY_APP_ID) // should correspond to Application Id env variable
                .clientKey(MY_CLIENT_KEY)  // should correspond to Client key env variable
                .server("https://parseapi.back4app.com").build());
    }
}
