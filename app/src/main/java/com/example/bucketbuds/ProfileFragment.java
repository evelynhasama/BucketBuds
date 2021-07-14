package com.example.bucketbuds;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.bumptech.glide.Glide;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final String PHOTO_FILE_NAME = "profile_image.jpg";
    public final String APP_TAG = "BucketBuds";

    View view;
    File photoFile;
    TextView tvUsername;
    TextView tvFirst;
    TextView tvLast;
    TextView tvBio;
    TextView tvFriendCount;
    TextView tvBucketCount;
    ImageView ivProfileImage;
    Button btnEditProfile;
    TabLayout tabLayout;
    ViewPager viewPager;
    User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = new User(ParseUser.getCurrentUser());
        tvUsername = view.findViewById(R.id.tvUsernamePF);
        tvFirst = view.findViewById(R.id.tvFirstNamePF);
        tvLast = view.findViewById(R.id.tvLastNamePF);
        tvBio = view.findViewById(R.id.tvBioPF);
        tvFriendCount = view.findViewById(R.id.tvFriendCountPF);
        tvBucketCount = view.findViewById(R.id.tvBucketCountPF);
        ivProfileImage = view.findViewById(R.id.ivProfileImagePF);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tabLayout = view.findViewById(R.id.sliding_tabsPF);
        viewPager = view.findViewById(R.id.viewpagerPF);

        ParseFile image = user.getImage();
        if (image == null){
            Glide.with(view).load(R.drawable.profile_placeholder).transform(new CenterCrop(), new RoundedCorners(20)).into(ivProfileImage);
        } else {
            Glide.with(view).load(image.getUrl()).transform(new CenterCrop(), new RoundedCorners(20)).into(ivProfileImage);
        }
        tvFirst.setText(user.getFirstName());
        tvLast.setText(user.getLastName());
        tvUsername.setText("@"+user.getUsername());
        tvFriendCount.setText(String.valueOf(user.getFriendCount()));
        tvBucketCount.setText(String.valueOf(user.getBucketCount()));

        String bio = user.getBio();
        if (bio == null) {
            tvBio.setText(0);
        } else {
            tvBio.setText(bio);
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        return view;
    }

    public void showEditProfileDialog(){
        View messageView = LayoutInflater.from(getContext()).
                inflate(R.layout.dialog_edit_profile, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(messageView);
        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        EditText etUsername = messageView.findViewById(R.id.etUsernameDE);
        EditText etBio = messageView.findViewById(R.id.etBioDE);
        EditText etFirstName = messageView.findViewById(R.id.etFirstNameDE);
        EditText etLastName = messageView.findViewById(R.id.etLastNameDE);
        EditText etEmail = messageView.findViewById(R.id.etEmailDE);
        Button btnSave = messageView.findViewById(R.id.btnSaveDE);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDE);

        etBio.setText(user.getBio(), TextView.BufferType.EDITABLE);
        etUsername.setText(user.getUsername(), TextView.BufferType.EDITABLE);
        etFirstName.setText(user.getFirstName(), TextView.BufferType.EDITABLE);
        etLastName.setText(user.getLastName(), TextView.BufferType.EDITABLE);
        etEmail.setText(user.getEmail(), TextView.BufferType.EDITABLE);

        // Configure dialog buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsername.getText().toString();
                String bio = etBio.getText().toString();
                String first = etFirstName.getText().toString();
                String last = etLastName.getText().toString();
                String email = etEmail.getText().toString();

                if (username.isEmpty() || first.isEmpty() || last.isEmpty() || email.isEmpty()) {
                    Toast.makeText(getContext(), "Name, username, and email are required", Toast.LENGTH_SHORT);
                } else {
                    alertDialog.cancel();
                    user.setUsername(username);
                    user.setBio(bio);
                    user.setEmail(email);
                    user.setFirstName(first);
                    user.setLastName(last);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                alertDialog.dismiss();
                                tvBio.setText(bio);
                                tvUsername.setText("@"+username);
                                tvFirst.setText(first);
                                tvLast.setText(last);
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Failed to save information", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        // Display the dialog
        alertDialog.show();
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(PHOTO_FILE_NAME);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.bucketbuds", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Load the taken image into a preview
                user.setImage(new ParseFile(photoFile));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                            Glide.with(view).load(takenImage).transform(new CenterCrop(), new RoundedCorners(20)).into(ivProfileImage);
                        } else {
                            Toast.makeText(getContext(), "Image failed to save", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

}