package com.example.bucketbuds;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CreateBucketFragment extends Fragment {

    public static final String TAG = "CreateBucketFragment";
    public static final String PHOTO_FILE_NAME = "bucket_image.jpg";
    public final static int PICK_PHOTO_CODE = 111;
    View view;
    EditText etBucketName;
    EditText etBucketDescription;
    RecyclerView rvBucketFriends;
    ImageView ivBucketImage;
    List<BucketFriendListItem> allItemsList;
    UserPub myUserPub;
    BucketList bucketList;
    BucketFriendHeaderItem header_suggest;

    public CreateBucketFragment() {
        // Required empty public constructor
    }

    public static CreateBucketFragment newInstance() {
        CreateBucketFragment fragment = new CreateBucketFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bucketList = new BucketList();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_bucket, container, false);
        etBucketName = view.findViewById(R.id.etBucketNameFCB);
        etBucketDescription = view.findViewById(R.id.etBucketDescriptionFCB);
        rvBucketFriends = view.findViewById(R.id.rvBucketFriendsFCB);
        ivBucketImage = view.findViewById(R.id.ivBucketImageFCB);

        Glide.with(getContext()).load(R.drawable.bucket_placeholder).centerCrop().into(ivBucketImage);

        myUserPub = User.getCurrentUser().getUserPubQuery();
        allItemsList = new ArrayList<>();

        // add headers to the list
        BucketFriendHeaderItem header_added = new BucketFriendHeaderItem();
        header_added.setSection("Added Friends");
        allItemsList.add(header_added);
        header_suggest = new BucketFriendHeaderItem();
        header_suggest.setSection("Suggested Friends");
        allItemsList.add(header_suggest);

        BucketFriendsAdapter adapter = new BucketFriendsAdapter(getContext(), allItemsList);
        rvBucketFriends.setAdapter(adapter);
        rvBucketFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        FindCallback<ParseUser> friendsSaveCallback = new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "issue with adding friends to list", e);
                    return;
                }
                for (int i = 0; i < friends.size(); i++) {
                    BucketFriendItem friendItem = new BucketFriendItem();
                    friendItem.setUser(new User(friends.get(i)));
                    friendItem.setAdded(false);
                    allItemsList.add(friendItem);
                }
                adapter.notifyDataSetChanged();
            }
        };
        myUserPub.getFriendsRelation().getQuery().findInBackground(friendsSaveCallback);

        ivBucketImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem miLogout = menu.findItem(R.id.logout);
        MenuItem miCreate = menu.findItem(R.id.miCreate);
        miLogout.setVisible(false);
        miCreate.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.miCreate) {
            // find index of Suggested header
            int i = allItemsList.indexOf(header_suggest);
            List<User> bucketFriends = new ArrayList<>();
            while (allItemsList.get(i).getType() != BucketFriendListItem.TYPE_HEADER) {
                bucketFriends.add(((BucketFriendItem) allItemsList.get(i)).getUser());
                i ++;
            }
            if (etBucketName.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Give your bucket list a name", Toast.LENGTH_SHORT).show();
            } else if (bucketFriends.size() == 0) {
                Toast.makeText(getContext(), "Add at least 1 friend", Toast.LENGTH_SHORT).show();
            } else {
                bucketList.setName(etBucketName.getText().toString());
                bucketList.setDescription(etBucketDescription.getText().toString());
                bucketList.saveInBackground(getBucketSaveCallback(bucketFriends));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public SaveCallback getStandardSaveCallback(String errorDescription){
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, errorDescription + e);
                }
            }
        };
    }

    public void addSaveBucketList(UserPub userPub){
        userPub.addBucket(bucketList);
        userPub.saveInBackground(getStandardSaveCallback("addSaveBucketList "));
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        Log.d(TAG, "onPickPhoto");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_PHOTO_CODE);
        } else {
            Log.d(TAG, "startng Intent");
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
            ivBucketImage.setImageBitmap(selectedImage);

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

    // helper function for saving the created bucket
    public SaveCallback getBucketSaveCallback(List<User> bucketFriends) {
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Failed to create bucket list", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (User friend : bucketFriends) {
                    UserPub userPub = friend.getUserPubQuery();
                    addSaveBucketList(userPub);
                }
                addSaveBucketList(myUserPub);
                bucketList.addFriends(bucketFriends);
                bucketList.saveInBackground(getStandardSaveCallback("bucketList add friends "));
                FragmentActivity activity = getActivity();
                activity.getSupportFragmentManager().popBackStack();
            }
        };
    }

}