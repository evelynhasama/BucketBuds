package com.evelynhasama.bucketbuds;

import android.content.Context;

public interface IEventAPI {
    void getEvents(Context context, Double latitude, Double longitude, InspoActivitiesAdapter adapter, String radiusFilter);
}
