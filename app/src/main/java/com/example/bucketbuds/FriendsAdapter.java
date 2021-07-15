package com.example.bucketbuds;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public static final String TAG = "FriendsAdapter";

    Context context;
    List<User> users;
    Boolean friendsBool;
    Fragment fragment;
    List<String> friendsId;
    UserPub currentUserPub;
    public static final User currentUser = User.getCurrentUser();

    // pass in context and list of users
    public FriendsAdapter(Context context, List<User> users, UserPub currentUserPub, Boolean friendsBool, Fragment fragment, List<String> friendsId){
        this.friendsBool = friendsBool;
        this.context = context;
        this.users = users;
        this.currentUserPub = currentUserPub;
        this.fragment = fragment;
        this.friendsId = friendsId;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (friendsBool) {
            view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_add_friend, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, friendsBool);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        ImageView ivAddFriend;
        ImageView ivRemoveFriend;
        TextView tvUsername;
        TextView tvFirstName;
        TextView tvLastName;
        TextView tvBucketCount;
        TextView tvFriendCount;

        public ViewHolder(View itemView) {
            super(itemView);
            if (friendsBool) {
                ivProfileImage = itemView.findViewById(R.id.ivProfileImageIF);
                tvUsername = itemView.findViewById(R.id.tvUsernameIF);
                tvFirstName = itemView.findViewById(R.id.tvFirstNameIF);
                tvLastName = itemView.findViewById(R.id.tvLastNameIF);
                tvFriendCount = itemView.findViewById(R.id.tvFriendCountIF);
                tvBucketCount = itemView.findViewById(R.id.tvBucketCountIF);
                ivRemoveFriend = itemView.findViewById(R.id.ivRemoveFriendIF);
            } else {
                ivProfileImage = itemView.findViewById(R.id.ivProfileImageIAF);
                tvUsername = itemView.findViewById(R.id.tvUsernameIAF);
                tvFirstName = itemView.findViewById(R.id.tvFirstNameIAF);
                tvLastName = itemView.findViewById(R.id.tvLastNameIAF);
                ivAddFriend = itemView.findViewById(R.id.ivAddFriendIAF);
            }
        }

        public void bind(User user, Boolean friendsBool){
            if (friendsBool) {
                UserPub otherUserPub = user.getUserPubQuery();
                tvBucketCount.setText(String.valueOf(otherUserPub.getBucketCount()));
                tvFriendCount.setText(String.valueOf(otherUserPub.getFriendCount()));
                // user selects a friend to remove
                ivRemoveFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        otherUserPub.removeFriend(currentUser);
                        currentUserPub.removeFriend(user);
                        users.remove(user);
                        updateFriends(false, getAdapterPosition(), otherUserPub);
                    }
                });
            } else {
                // user is already added as a friend but appears in search
                if (friendsId.contains(user.getObjectId())){
                    ivAddFriend.setVisibility(View.GONE);
                } else {
                    ivAddFriend.setVisibility(View.VISIBLE);
                    // user selects a friend to add
                    ivAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserPub otherUserPub = user.getUserPubQuery();
                            otherUserPub.addFriend(currentUser);
                            currentUserPub.addFriend(user);
                            ivAddFriend.setVisibility(View.GONE);
                            updateFriends(true, getAdapterPosition(), otherUserPub);
                        }
                    });
                }
            }
            if (user.getImage() != null) {
                Glide.with(context).load(user.getImage().getUrl()).circleCrop().into(ivProfileImage);
            } else {
                Glide.with(context).load(R.drawable.profile_placeholder).circleCrop().into(ivProfileImage);
            }
            tvUsername.setText("@"+user.getUsername());
            tvFirstName.setText(user.getFirstName());
            tvLastName.setText(user.getLastName());
        }
    }

    public void clear(){
        users.clear();
    }

    // Helper method for saving added or removed friend
    public void updateFriends(Boolean added, int position, UserPub otherUserPub){
        currentUserPub.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    otherUserPub.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (added) {
                                    Toast.makeText(context, "Friend added", Toast.LENGTH_SHORT).show();
                                    // notifyItemChanged(position);
                                } else {
                                    Toast.makeText(context, "Friend removed", Toast.LENGTH_SHORT).show();
                                    notifyItemRemoved(position);
                                }
                                ((ProfileFragment) fragment.getParentFragment()).updateFriendCount();
                            } else {
                                Toast.makeText(context, "Friend action failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Friend action failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
