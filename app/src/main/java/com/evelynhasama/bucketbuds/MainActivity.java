package com.evelynhasama.bucketbuds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        final Fragment fragment_profile = new ProfileFragment();
        final Fragment fragment_buckets = new BucketsFragment();
        final Fragment fragment_inspo = new InspoFragment();
      
        // Initialize the Places SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        ActionBar actionBar = getSupportActionBar();
        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_profile:
                                fragment = fragment_profile;
                                actionBar.setTitle("Hi " + User.getCurrentUser().getFirstName());
                                break;
                            case R.id.action_inspo:
                                fragment = fragment_inspo;
                                actionBar.setTitle("Discover");
                                break;
                            case R.id.action_buckets:
                            default:
                                fragment = fragment_buckets;
                                actionBar.setTitle("Bucket Buds");
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_buckets);
    }

}