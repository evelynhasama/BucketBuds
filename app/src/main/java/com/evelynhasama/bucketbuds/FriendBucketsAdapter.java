package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class FriendBucketsAdapter extends RecyclerView.Adapter<FriendBucketsAdapter.ViewHolder> {

    public static final String TAG = "FriendBucketsAdapter";
    Context context;
    List<BucketList> friendBuckets;
    View view;
    List<String> userBucketIds;

    public FriendBucketsAdapter(Context context, List<BucketList> friendBuckets) {
        this.context = context;
        this.friendBuckets = friendBuckets;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_bucket_list, parent, false);
        return new FriendBucketsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FriendBucketsAdapter.ViewHolder holder, int position) {
        BucketList bucket = friendBuckets.get(position);
        holder.bind(bucket);
    }
    
    @Override
    public int getItemCount() {
        return friendBuckets.size();
    }

    public void setMyBuckets(List<String> bucketIds){
        this.userBucketIds = bucketIds;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBucketImage;
        TextView tvBucketName;
        TextView tvUserCount;
        TextView tvActivityCount;
        ImageView ivCheckBox;
        Button btnJoinBucket;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBucketImage = itemView.findViewById(R.id.ivBucketImageIBL);
            tvBucketName = itemView.findViewById(R.id.tvBucketNameIBL);
            tvUserCount = itemView.findViewById(R.id.tvUserCountIBL);
            tvActivityCount = itemView.findViewById(R.id.tvActivityCountIBL);
            ivCheckBox = itemView.findViewById(R.id.ivCheckBoxIBL);
            btnJoinBucket = itemView.findViewById(R.id.btnJoinBucketIBL);
        }

        public void bind(BucketList bucket) {

            tvBucketName.setText(bucket.getName());
            Glide.with(context).load(bucket.getImage().getUrl()).centerCrop().into(ivBucketImage);
            tvUserCount.setText(String.valueOf(bucket.getUserCount()));
            tvActivityCount.setText(String.valueOf(bucket.getActivityCount()));

            int checkImage = bucket.getCompleted() ? R.drawable.ic_checked_box : R.drawable.ic_unchecked_box;
            ivCheckBox.setImageResource(checkImage);

            Boolean mutualBucket = userBucketIds.contains(bucket.getObjectId());
            String btnText = mutualBucket ? "Mutual" : "Join";
            btnJoinBucket.setClickable(!mutualBucket);
            btnJoinBucket.setText(btnText);

            if (!mutualBucket) {
                FindCallback<BucketRequest> bucketRequestFindCallback = new FindCallback<BucketRequest>() {
                    @Override
                    public void done(List<BucketRequest> objects, ParseException e) {
                        if (!objects.isEmpty()){
                            Toast.makeText(context, "Request " + objects.get(0).getStatus(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        showRequestDialog(bucket);
                    }
                };
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseQuery<BucketRequest> query = new ParseQuery<BucketRequest>(BucketRequest.class);
                        query.whereEqualTo(BucketRequest.KEY_FROM_USER, ParseUser.getCurrentUser()).whereEqualTo(BucketRequest.KEY_BUCKET, bucket);
                        query.findInBackground(bucketRequestFindCallback);
                    }
                };
                btnJoinBucket.setOnClickListener(clickListener);
            }
        }
    }

    private void showRequestDialog(BucketList bucketList){
        View messageView = LayoutInflater.from(context).
                inflate(R.layout.dialog_request_to_join, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(messageView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvBucketName = messageView.findViewById(R.id.tvBucketNameDRJ);
        Button btnSave = messageView.findViewById(R.id.btnSaveDRJ);
        Button btnCancel = messageView.findViewById(R.id.btnCancelDRJ);

        tvBucketName.setText(bucketList.getName());

        // Configure dialog buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BucketRequest request = new BucketRequest();
                request.setBucket(bucketList);
                request.setFromUser(User.getCurrentUser());
                request.setStatus(BucketRequest.PENDING);
                SaveCallback requestSaveCallback = new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (hasError(e)){
                            return;
                        }
                        bucketList.addBucketRequest(request);
                        bucketList.saveInBackground(getBucketListSaveCallback());
                    }
                };
                request.saveInBackground(requestSaveCallback);
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

    private SaveCallback getBucketListSaveCallback() {
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (hasError(e)){
                    return;
                }
                Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private Boolean hasError(ParseException e){
        if (e != null){
            Log.d(TAG, "error saving request "+ e);
            Toast.makeText(context, "Error sending request", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}
