package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BucketFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    List<BucketFriendListItem> allItemsList;
    public static final String TAG = "BucketFriendsAdapter";

    public BucketFriendsAdapter(Context context, List<BucketFriendListItem> allItemsList) {
        this.allItemsList = allItemsList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        View view;

        switch (viewType) {
            case BucketFriendListItem.TYPE_FRIEND:
                view = LayoutInflater.from(context).inflate(R.layout.item_add_friend, parent, false);
                viewHolder = new FriendViewHolder(view);
                break;

            case BucketFriendListItem.TYPE_HEADER:
                view = LayoutInflater.from(context).inflate(R.layout.item_bucket_friend_header, parent, false);
                viewHolder = new HeaderViewHolder(view);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case BucketFriendListItem.TYPE_FRIEND:

                BucketFriendItem friendItem   = (BucketFriendItem) allItemsList.get(position);
                FriendViewHolder friendViewHolder= (FriendViewHolder) holder;
                User user = friendItem.getUser();
                friendViewHolder.tvFirstName.setText(user.getFirstName());
                friendViewHolder.tvLastName.setText(user.getLastName());
                friendViewHolder.tvUsername.setText(user.getUsername());

                if (user.getImage() != null) {
                    Glide.with(context).load(user.getImage().getUrl()).circleCrop().into(friendViewHolder.ivProfileImage);
                } else {
                    Glide.with(context).load(R.drawable.profile_placeholder).circleCrop().into(friendViewHolder.ivProfileImage);
                }

                Boolean added = friendItem.getAdded();
                if (added) {
                    friendViewHolder.ivUpdateFriend.setImageResource(R.drawable.ic_person_remove);
                } else {
                    friendViewHolder.ivUpdateFriend.setImageResource(R.drawable.ic_person_add);
                }
                View.OnClickListener updateFriendClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allItemsList.remove(position);
                        int movePosition;
                        // move item to suggested section
                        if (added) {
                            movePosition = allItemsList.size();
                            friendViewHolder.ivUpdateFriend.setImageResource(R.drawable.ic_person_remove);
                            Log.d(TAG, "from added to suggested " + movePosition);
                        }
                        // move item to added section
                        else {
                            // find index of Suggested header
                            movePosition = 1;
                            while (allItemsList.get(movePosition).getType() != BucketFriendListItem.TYPE_HEADER) {
                                movePosition ++;
                            }
                        }
                        allItemsList.add(movePosition, friendItem);
                        friendItem.setAdded(!added);
                        notifyItemMoved(position, movePosition);
                        notifyItemChanged(movePosition, null);
                    }
                };
                friendViewHolder.ivUpdateFriend.setOnClickListener(updateFriendClickListener);

                break;

            case  BucketFriendListItem.TYPE_HEADER:

                BucketFriendHeaderItem headerItem   = (BucketFriendHeaderItem) allItemsList.get(position);
                HeaderViewHolder headerViewHolder= (HeaderViewHolder) holder;
                headerViewHolder.tvHeader.setText(headerItem.getSection());

                break;
        }
    }

    @Override
    public int getItemCount() {
        return allItemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return allItemsList.get(position).getType();
    }

    // ViewHolder for section row item
    class FriendViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        TextView tvFirstName;
        TextView tvLastName;
        ImageView ivUpdateFriend;
        ImageView ivProfileImage;

        public FriendViewHolder(View v) {
            super(v);
            ivProfileImage = v.findViewById(R.id.ivProfileImageIAF);
            tvUsername = v.findViewById(R.id.tvUsernameIAF);
            tvFirstName = v.findViewById(R.id.tvFirstNameIAF);
            tvLastName = v.findViewById(R.id.tvLastNameIAF);
            ivUpdateFriend = v.findViewById(R.id.ivAddFriendIAF);

        }
    }

    // View holder for general row item
    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader;

        public HeaderViewHolder(View v) {
            super(v);
            tvHeader = v.findViewById(R.id.tvHeaderIBFH);

        }
    }

}
