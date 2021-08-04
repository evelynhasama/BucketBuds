package com.evelynhasama.bucketbuds;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import static android.app.Activity.RESULT_OK;

public class BucketActivitiesFragment extends Fragment {

    private static final String ARG_BUCKET_LIST = "bucketList";
    public static final String TAG = "BucketActivitiesFragment";
    public static final String PHOTO_FILE_NAME = "bucket_image.jpg";
    public final static int PICK_PHOTO_CODE = 111;
    private static int AUTOCOMPLETE_REQUEST_CODE = 21;
    public static final int LOCATION_REQUEST_PERMISSION_CODE = 133;

    Autocomplete.IntentBuilder intentBuilder;
    BucketList bucketList;
    View view;
    List<User> users;
    BucketUsersAdapter bucketUsersAdapter;
    BucketActivitiesAdapter activitiesAdapter;
    ImageView ivBucketImage;
    TextView tvBucketName;
    TextView tvBucketDescription;
    List<BucketActivityItem> allActivityItemsList;
    int completedHeaderPosition;
    Switch swCompleted;
    BucketActivityHeaderItem header_completed;
    KonfettiView konfettiView;
    ProgressBar progressBar;
    EditText etLocation;
    List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);
    String address;

    public BucketActivitiesFragment() {
    }

    public static BucketActivitiesFragment newInstance(BucketList bucketList) {
        BucketActivitiesFragment fragment = new BucketActivitiesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BUCKET_LIST, bucketList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            bucketList = getArguments().getParcelable(ARG_BUCKET_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bucket_activities, container, false);

        swCompleted = view.findViewById(R.id.swCompletedFBA);
        tvBucketName = view.findViewById(R.id.tvBucketNameFBA);
        tvBucketDescription = view.findViewById(R.id.tvBucketDescriptionFBA);
        ivBucketImage = view.findViewById(R.id.ivBucketImageFBA);
        RecyclerView rvBucketUsers = view.findViewById(R.id.rvBucketFriendsFBA);
        RecyclerView rvBucketActivities = view.findViewById(R.id.rvActivitiesFBA);
        konfettiView = view.findViewById(R.id.vKonfettiFBA);
        progressBar = view.findViewById(R.id.pbLoadingFBA);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        Glide.with(getContext()).load(bucketList.getImage().getUrl()).into(ivBucketImage);
        ivBucketImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });
        tvBucketName.setText(bucketList.getName());
        tvBucketDescription.setText(bucketList.getDescription());
        swCompleted.setChecked(bucketList.getCompleted() ? true : false);

        swCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bucketList.setCompleted(isChecked);
                if (isChecked){
                    completedHeaderPosition = allActivityItemsList.indexOf(header_completed);
                    // when the active section is not empty
                    if (completedHeaderPosition != 1) {
                        swCompleted.setChecked(false);
                        Toast.makeText(getContext(), "Complete all activities in your bucket list", Toast.LENGTH_LONG).show();
                        return;
                    }
                    konfettiView.build()
                            .addColors(getResources().getColor(R.color.cadet_blue), getResources().getColor(R.color.grape), getResources().getColor(R.color.lavender))
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(2000L)
                            .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                            .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                            .streamFor(200, 2000L);
                    }
                bucketList.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "saving switch completed", e);
                        }
                    }
                });
            }
        });

        users = new ArrayList<>();
        bucketUsersAdapter = new BucketUsersAdapter(getContext(), users);
        rvBucketUsers.setAdapter(bucketUsersAdapter);
        rvBucketUsers.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.HORIZONTAL, false));

        FindCallback<ParseUser> bucketUsersFindCallback = new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error bucket users find callback", e);
                    return;
                }
                for (ParseUser user: objects){
                    users.add(new User(user));
                    bucketUsersAdapter.notifyDataSetChanged();
                }
            }
        };

        bucketList.getUsersRelation().getQuery().findInBackground(bucketUsersFindCallback);

        allActivityItemsList = new ArrayList<>();

        // add headers to the list
        BucketActivityHeaderItem header_active = new BucketActivityHeaderItem();
        header_active.setSection("Active");
        allActivityItemsList.add(header_active);
        header_completed = new BucketActivityHeaderItem();
        header_completed.setSection("Completed");
        allActivityItemsList.add(header_completed);
        completedHeaderPosition = 1;

        activitiesAdapter = new BucketActivitiesAdapter(getContext(), allActivityItemsList, getActivity(), header_completed);
        rvBucketActivities.setAdapter(activitiesAdapter);
        rvBucketActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        bucketList.getActivitiesRelation().getQuery().addDescendingOrder(ActivityObj.KEY_UPDATED).findInBackground(getActivitiesFindCallback());

        return view;
    }

    private FindCallback<ActivityObj> getActivitiesFindCallback() {
        return new FindCallback<ActivityObj>() {
            @Override
            public void done(List<ActivityObj> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "adding activities", e);
                    return;
                }
                for (ActivityObj activityObj:objects) {
                    BucketActivityObjItem activityObjItem = new BucketActivityObjItem();
                    activityObjItem.setActivityObj(activityObj);
                    if (activityObj.getCompleted()){
                        // move to completed section
                        allActivityItemsList.add(activityObjItem);
                    } else {
                        // move to active section
                        allActivityItemsList.add(completedHeaderPosition, activityObjItem);
                        completedHeaderPosition ++;
                    }
                }
                activitiesAdapter.notifyDataSetChanged();
            }
        };
    }

    public void showAddActivityDialog(){
        View messageView = LayoutInflater.from(getContext()).
                inflate(R.layout.dialog_add_activity, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationCorner;
        EditText etTitle = messageView.findViewById(R.id.etTitleDAA);
        EditText etDescription = messageView.findViewById(R.id.etDescriptionDAA);
        etLocation = messageView.findViewById(R.id.etLocationDAA);
        EditText etWebsite = messageView.findViewById(R.id.etWebsiteDAA);
        Button btnSave = messageView.findViewById(R.id.btnSaveDAA);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDAA);
        ImageButton ibSearchPlaces = messageView.findViewById(R.id.ibSearchPlaces);

        ibSearchPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentBuilder = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields);
                getLocation();
            }
        });

        // Configure dialog buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                String location = etLocation.getText().toString();
                String web = etWebsite.getText().toString();

                ActivityObj activityObj = new ActivityObj();

                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT);
                    return;
                }
                activityObj.setName(title);
                activityObj.setDescription(description);
                activityObj.setBucket(bucketList);
                activityObj.setLocation(location);
                activityObj.setWeb(web);
                if (address != null){
                    activityObj.setAddress(address);
                }
                activityObj.saveInBackground(getNewActivitySaveCallback(activityObj, alertDialog));
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    // helper method for saving activity and bucket list
    public SaveCallback getNewActivitySaveCallback(ActivityObj activityObj, AlertDialog alertDialog){
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, String.valueOf(e));
                    return;
                }
                bucketList.addActivity(activityObj);
                bucketList.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, String.valueOf(e));
                            return;
                        }
                        BucketActivityObjItem activityObjItem = new BucketActivityObjItem();
                        activityObjItem.setActivityObj(activityObj);
                        completedHeaderPosition = allActivityItemsList.indexOf(header_completed);
                        allActivityItemsList.add(completedHeaderPosition, activityObjItem);
                        activitiesAdapter.notifyItemInserted(completedHeaderPosition);
                        alertDialog.dismiss();
                    }
                });
            }
        };
    }

    public void showEditBucketDialog(){
        View messageView = LayoutInflater.from(getContext()).
                inflate(R.layout.dialog_edit_bucket, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationCorner;
        EditText etName = messageView.findViewById(R.id.etBucketNameDEB);
        EditText etDescription = messageView.findViewById(R.id.etDescriptionDEB);
        Button btnSave = messageView.findViewById(R.id.btnSaveDEB);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDEB);

        etDescription.setText(bucketList.getDescription(), TextView.BufferType.EDITABLE);
        etName.setText(bucketList.getName(), TextView.BufferType.EDITABLE);

        // Configure dialog buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String description = etDescription.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getContext(), "Bucket List Name is required", Toast.LENGTH_SHORT);
                    return;
                }
                bucketList.setName(name);
                bucketList.setDescription(description);
                bucketList.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, String.valueOf(e));
                            return;
                        }
                        tvBucketDescription.setText(description);
                        tvBucketName.setText(name);
                        alertDialog.dismiss();
                        Glide.with(getContext()).load(bucketList.getImage().getUrl()).centerCrop().into(ivBucketImage);
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        Log.d(TAG, "onPickPhoto");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_PHOTO_CODE);
        } else {
            Log.d(TAG, "starting Intent");
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, status.getStatusMessage());
                return;
            }
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d(TAG, "Place: " + place.getName());
                etLocation.setText(place.getName(), TextView.BufferType.EDITABLE);
                address = "geo:0,0?q=" + place.getAddress();
            }
        }

        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            progressBar.bringToFront();
            progressBar.setVisibility(ProgressBar.VISIBLE);
            // convert to ParseFile and set to BucketList
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG,70,stream);
            byte[] byteArray = stream.toByteArray();

            ParseFile file = new ParseFile(PHOTO_FILE_NAME, byteArray);
            bucketList.setImage(file);
            bucketList.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "error saving image" + e);
                        return;
                    }
                    ivBucketImage.setImageBitmap(selectedImage);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Log.d(TAG, "done saving bucket image");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_PHOTO_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_PHOTO_CODE);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
            case LOCATION_REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // start intent without location bias
                    Intent intent = intentBuilder.build(getContext());
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        List<Integer> visibles = new ArrayList<>();
        visibles.add(MenuHelper.ADD);
        visibles.add(MenuHelper.EDIT);
        MenuHelper.onCreateOptionsMenu(menu, visibles);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MenuHelper.EDIT) {
            showEditBucketDialog();
        }
        else if (item.getItemId() == MenuHelper.ADD){
            showAddActivityDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocation() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            // check permissions
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_PERMISSION_CODE);
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
                LatLng northEast = new LatLng(latitude + 0.2, longitude + 0.2);
                LatLng southWest = new LatLng(latitude - 0.2, longitude - 0.2);
                Intent intent = intentBuilder.setLocationBias(RectangularBounds.newInstance(southWest, northEast)).build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        }
    }

}