package com.evelynhasama.bucketbuds;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;


public class InspoFragment extends Fragment {

    private static final int REQUEST_PERMISSION_CODE = 234;
    public static final String TAG = "InspoFragment";

    View mView;
    RecyclerView mRvActivities;
    ArrayList<ActivityObj> mActivites;
    InspoActivitiesAdapter mAdapter;

    public static InspoFragment newInstance() {
        InspoFragment fragment = new InspoFragment();
        return fragment;
    }

    public InspoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_inspo, container, false);
        mRvActivities = mView.findViewById(R.id.rvActivitiesFI);

        mRvActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        mActivites = new ArrayList<>();
        mAdapter = new InspoActivitiesAdapter(getContext(), mActivites, getActivity());
        mRvActivities.setAdapter(mAdapter);
        getData();

        return mView;
    }

    private void getData() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            // check permissions
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_CODE);
                return;
            }
            // getting GPS status
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(getContext(), "Enable GPS", Toast.LENGTH_SHORT).show();
                return;
            };

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Double latitude;
            Double longitude;
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                TicketMasterHelper.getEvents(getContext(), latitude, longitude, mAdapter);
                SeatGeekHelper.getEvents(getContext(), latitude, longitude, mAdapter);
                MusementHelper.getEvents(getContext(), latitude, longitude, mAdapter);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getData();
                return;
            }
            Toast.makeText(getContext(), "Please accept calendar permission requests to create events", Toast.LENGTH_LONG).show();
        }
    }

}