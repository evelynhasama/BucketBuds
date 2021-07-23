package com.evelynhasama.bucketbuds;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BucketActivitiesFragment extends Fragment {

    private static final String ARG_BUCKET_LIST = "bucketList";
    public static final String TAG = "BucketActivitiesFragment";
    public static final String PHOTO_FILE_NAME = "bucket_image.jpg";
    public final static int PICK_PHOTO_CODE = 111;

    BucketList bucketList;
    View view;
    List<User> users;
    BucketUsersAdapter bucketUsersAdapter;
    BucketActivitiesAdapter activitiesAdapter;
    ImageView ivBucketImageDEB;
    ImageView ivBucketImage;
    TextView tvBucketName;
    TextView tvBucketDescription;
    List<BucketActivityItem> allActivityItemsList;
    int completedHeaderPosition;
    BucketActivityHeaderItem header_completed;

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
        if (getArguments() != null) {
            bucketList = getArguments().getParcelable(ARG_BUCKET_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bucket_activities, container, false);

        Switch swCompleted = view.findViewById(R.id.swCompletedFBA);
        tvBucketName = view.findViewById(R.id.tvBucketNameFBA);
        tvBucketDescription = view.findViewById(R.id.tvBucketDescriptionFBA);
        ivBucketImage = view.findViewById(R.id.ivBucketImageFBA);
        Button btnEditBucket = view.findViewById(R.id.btnEditBucketFBA);
        Button btnAddActivity = view.findViewById(R.id.btnAddActivityFBA);
        RecyclerView rvBucketUsers = view.findViewById(R.id.rvBucketFriendsFBA);
        RecyclerView rvBucketActivities = view.findViewById(R.id.rvActivitiesFBA);

        Glide.with(getContext()).load(bucketList.getImage().getUrl()).centerCrop()
                .transform(new RoundedCorners(10)).into(ivBucketImage);
        tvBucketName.setText(bucketList.getName());
        tvBucketDescription.setText(bucketList.getDescription());
        swCompleted.setChecked(bucketList.getCompleted() ? true : false);

        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnAddActivity clicked");
                showAddActivityDialog();
            }
        });

        btnEditBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditBucketDialog();
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
        EditText etTitle = messageView.findViewById(R.id.etTitleDAA);
        EditText etDescription = messageView.findViewById(R.id.etDescriptionDAA);
        EditText etLocation = messageView.findViewById(R.id.etLocationDAA);
        EditText etWebsite = messageView.findViewById(R.id.etWebsiteDAA);
        Button btnSave = messageView.findViewById(R.id.btnSaveDAA);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDAA);

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
        EditText etName = messageView.findViewById(R.id.etBucketNameDEB);
        EditText etDescription = messageView.findViewById(R.id.etDescriptionDEB);
        ivBucketImageDEB = messageView.findViewById(R.id.ivBucketImageDEB);
        Button btnSave = messageView.findViewById(R.id.btnSaveDEB);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDEB);

        etDescription.setText(bucketList.getDescription(), TextView.BufferType.EDITABLE);
        etName.setText(bucketList.getName(), TextView.BufferType.EDITABLE);
        Glide.with(getContext()).load(bucketList.getImage().getUrl()).centerCrop().into(ivBucketImageDEB);

        ivBucketImageDEB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

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
                        Glide.with(getContext()).load(bucketList.getImage().getUrl()).centerCrop()
                                .transform(new RoundedCorners(10)).into(ivBucketImage);
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
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            ivBucketImageDEB.setImageBitmap(selectedImage);

            // convert to ParseFile and set to BucketList
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray = stream.toByteArray();

            ParseFile file = new ParseFile(PHOTO_FILE_NAME, byteArray);
            bucketList.setImage(file);
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
        }
    }

}