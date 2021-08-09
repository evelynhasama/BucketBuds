package com.evelynhasama.bucketbuds;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;

public class LocationHelper {

    public static final String TAG = "LocationHelper";
    public static final int LOCATION_REQUEST_PERMISSION_CODE = 133;
    public static final int AUTOCOMPLETE_REQUEST_CODE = 21;
    // distance in latitude and longitude location rectangle bounds are from current location coordinates
    public static final double LOCATION_RECTANGLE_DISTANCE = 0.2;

    public static Intent getLocationPlacesAutocomplete(FragmentActivity activity, Context context, Autocomplete.IntentBuilder intentBuilder){
        Log.d(TAG, "getting location places autocomplete");
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return null;
        }
        // check permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_PERMISSION_CODE);
            return null;
        }
        // getting GPS status
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(context, "Enable GPS", Toast.LENGTH_SHORT).show();
            return null;
        };
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Double latitude;
        Double longitude;
        if (location == null) {
            return null;
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng northEast = new LatLng(latitude + LOCATION_RECTANGLE_DISTANCE, longitude + LOCATION_RECTANGLE_DISTANCE);
        LatLng southWest = new LatLng(latitude - LOCATION_RECTANGLE_DISTANCE, longitude - LOCATION_RECTANGLE_DISTANCE);
        Intent intent = intentBuilder.setLocationBias(RectangularBounds.newInstance(southWest, northEast)).build(context);
        return intent;
    }

}
