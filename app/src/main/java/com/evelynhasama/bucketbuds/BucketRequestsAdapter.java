package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BucketRequestsAdapter extends RecyclerView.Adapter<BucketRequestsAdapter.ViewHolder> {

    public static final String TAG = "FriendBucketsAdapter";
    Context context;
    List<BucketRequest> bucketRequests;
    View view;

    public BucketRequestsAdapter(Context context, List<BucketRequest> bucketRequests) {
        this.context = context;
        this.bucketRequests = bucketRequests;
    }

    @NonNull
    @NotNull
    @Override
    public BucketRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_bucket_request, parent, false);
        return new BucketRequestsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BucketRequestsAdapter.ViewHolder holder, int position) {
        BucketRequest request = bucketRequests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return bucketRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBucketImage;
        TextView tvBucketName;
        TextView tvRequestStatus;
        TextView tvFirstName;
        TextView tvLastName;
        ImageView ivCheckMark;
        ImageView ivDeny;
        ImageView ivProfileImage;
        User fromUser;
        BucketList bucketList;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBucketImage = itemView.findViewById(R.id.ivBucketImageIBR);
            tvBucketName = itemView.findViewById(R.id.tvBucketNameIBR);
            tvRequestStatus = itemView.findViewById(R.id.tvRequestStatusIBR);
            tvFirstName = itemView.findViewById(R.id.tvFirstNameIBR);
            tvLastName = itemView.findViewById(R.id.tvLastNameIBR);
            ivCheckMark = itemView.findViewById(R.id.ivCheckMarkIBR);
            ivDeny = itemView.findViewById(R.id.ivDenyIBR);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImageIBR);
        }

        private void bind(BucketRequest request) {

            bucketList = request.getBucket();
            tvBucketName.setText(bucketList.getName());
            Glide.with(context).load(bucketList.getImage().getUrl()).centerCrop().into(ivBucketImage);

            fromUser = request.getFromUser();
            tvFirstName.setText(fromUser.getFirstName());
            tvLastName.setText(fromUser.getLastName());
            ParseFile image = fromUser.getImage();
            if (image == null){
                Glide.with(view).load(R.drawable.profile_placeholder).circleCrop().into(ivProfileImage);
            } else {
                Glide.with(view).load(image.getUrl()).circleCrop().into(ivProfileImage);
            }
            // the user's own request to join other buckets
            if (request.getFromUser().getObjectId().equals(User.getCurrentUser().getObjectId())){
                bindOwnRequest(request);
                return;
            }
            else if (request.getStatus().equals(BucketRequest.PENDING)) {
                bindPending(request, getAdapterPosition());
                return;
            }
            bindReceived(request);
        }

        private void bindPending(BucketRequest request, int position){
            ivCheckMark.setVisibility(View.VISIBLE);
            ivDeny.setVisibility(View.VISIBLE);
            tvRequestStatus.setText(R.string.requested_to_join);
            ivCheckMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   acceptJoinRequest(request, position);
                }
            });
            ivDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.setStatus(BucketRequest.DENIED);
                    request.setReceiver(User.getCurrentUser());
                    request.saveInBackground(getFinalSaveCallback(request, getAdapterPosition()));
                }
            });
        }

        private void bindOwnRequest(BucketRequest request){
            ivCheckMark.setVisibility(View.GONE);
            ivDeny.setVisibility(View.GONE);
            String requestText = "Request to join " + request.getStatus();
            if (!request.getStatus().equals(BucketRequest.PENDING)){
                requestText = requestText + " by " + request.getReceiver().getFirstName();
            }
            tvRequestStatus.setText(requestText);
        }

        private void bindReceived(BucketRequest request){
            ivCheckMark.setVisibility(View.GONE);
            ivDeny.setVisibility(View.GONE);
            User user = request.getReceiver();
            tvRequestStatus.setText(user.getFirstName() + " " + request.getStatus() + " request to join");
        }

        private void acceptJoinRequest(BucketRequest request, int position) {
            request.setStatus(BucketRequest.APPROVED);
            request.setReceiver(User.getCurrentUser());
            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null){
                        Log.e(TAG, String.valueOf(e));
                        return;
                    }
                    UserPub userPub = fromUser.getUserPubQuery();
                    userPub.addBucket(bucketList);
                    userPub.saveInBackground(getSimpleSaveCallback());
                    bucketList.addFriend(fromUser);
                    bucketList.saveInBackground(getFinalSaveCallback(request, getAdapterPosition()));
                }
            });
        }

        private SaveCallback getFinalSaveCallback(BucketRequest request, int position){
            return new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null){
                        Log.e(TAG, String.valueOf(e));
                        Toast.makeText(context, "Error saving", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(context, "Request responded", Toast.LENGTH_SHORT).show();
                    bindReceived(request);
                    notifyItemChanged(position);
                }
            };
        }

        private SaveCallback getSimpleSaveCallback() {
            return new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, String.valueOf(e));
                        Toast.makeText(context, "Error saving", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
    }

}
