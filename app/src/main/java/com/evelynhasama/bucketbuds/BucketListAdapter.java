package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.jetbrains.annotations.NotNull;
import java.util.List;


public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder> {

    public static final String TAG = "BucketListAdapter";
    Context context;
    FragmentActivity activity;
    List<BucketList> buckets;
    View view;
    int ivWidth;

    public BucketListAdapter(Context context, List<BucketList> buckets, FragmentActivity activity){
        this.context = context;
        this.buckets = buckets;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_bucket_list, parent, false);
        return new BucketListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BucketListAdapter.ViewHolder holder, int position) {
        BucketList bucket = buckets.get(position);
        holder.bind(bucket);
    }

    @Override
    public int getItemCount() {
        return buckets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBucketImage;
        TextView tvBucketName;
        TextView tvUserCount;
        TextView tvActivityCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBucketImage = itemView.findViewById(R.id.ivBucketImageIBL);
            tvBucketName = itemView.findViewById(R.id.tvBucketNameIBL);
            tvUserCount = itemView.findViewById(R.id.tvUserCountIBL);
            tvActivityCount = itemView.findViewById(R.id.tvActivityCountIBL);
        }

        public void bind(BucketList bucket) {

            tvBucketName.setText(bucket.getName());
            tvBucketName.measure(0, 0);
            ivWidth = tvBucketName.getMeasuredWidth();
            loadImage(bucket.getImage(), ivBucketImage);
            tvUserCount.setText(String.valueOf(bucket.getUserCount()));
            tvActivityCount.setText(String.valueOf(bucket.getActivityCount()));

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    showDeleteBucketDialog(bucket, getAdapterPosition());
                    return true;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment myFragment = BucketActivitiesFragment.newInstance(buckets.get(getAdapterPosition()));
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
                }
            });
        }
    }

    private void showDeleteBucketDialog(BucketList bucketList, int position) {
        View messageView = LayoutInflater.from(context).
                inflate(R.layout.dialog_delete_bucket, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(messageView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvBucketName = messageView.findViewById(R.id.tvBucketNameDDB);
        Button btnSave = messageView.findViewById(R.id.btnSaveDDB);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDDB);

       tvBucketName.setText(bucketList.getName());

        // Configure dialog buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bucketList.getActivitiesRelation().getQuery().findInBackground(getActivityObjFindCallback());
                bucketList.getUsersRelation().getQuery().findInBackground(getBucketUsersFindCallback(bucketList, position));
                alertDialog.dismiss();
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

    public FindCallback<ActivityObj> getActivityObjFindCallback(){
        return new FindCallback<ActivityObj>() {
            @Override
            public void done(List<ActivityObj> objects, ParseException e) {
                if (e!= null){
                    Log.d(TAG, String.valueOf(e));
                    return;
                }
                DeleteCallback deleteCallback = new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, String.valueOf(e));
                            return;
                        }
                    }
                };
                // deletes all of the activities
                for (ActivityObj activityObj: objects){
                    activityObj.deleteInBackground(deleteCallback);
                }
            }
        };
    }

    public FindCallback<ParseUser> getBucketUsersFindCallback(BucketList bucketList, int position){
        return new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e!= null){
                    Log.d(TAG, String.valueOf(e));
                    return;
                }
                // delete the bucket from each user's relation
                for (ParseUser parseUser: objects){
                    User user = new User(parseUser);
                    removeBucketUsers(user, bucketList);
                }
                deleteBucket(bucketList, position);
            }
        };
    }

    public void deleteBucket(BucketList bucketList, int position) {
        bucketList.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.d(TAG, String.valueOf(e));
                    return;
                }
                buckets.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Bucket list deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeBucketUsers(User user, BucketList bucketList) {
        UserPub userPub = user.getUserPubQuery();
        userPub.removeBucket(bucketList);
        userPub.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.d(TAG, String.valueOf(e));
                    return;
                }
            }
        });

    }

    public void changeBuckets(List<BucketList> bucketLists){
        buckets.clear();
        buckets.addAll(bucketLists);
        Log.d(TAG, "addBuckets "+ buckets.size());
        notifyDataSetChanged();
    }

    private void loadImage(ParseFile parseFile, ImageView imageView) {
        parseFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e != null) {
                    Log.d(TAG,"failure loading image " + e);
                    return;
                }
                // resizes image keeping aspect ratio
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                int width = bmp.getWidth();
                int height = bmp.getHeight();
                Glide.with(context).load(parseFile.getUrl()).placeholder(R.drawable.white_placeholder).override(ivWidth, (height * (width/ivWidth))).into(imageView);
            }
        });
    }


}
