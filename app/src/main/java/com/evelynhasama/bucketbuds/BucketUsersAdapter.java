package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class BucketUsersAdapter extends RecyclerView.Adapter<BucketUsersAdapter.ViewHolder> {

    public static final String TAG = "BucketUsersAdapter";
    Context context;
    List<User> users;

    public BucketUsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    public void clear() {
        users.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bucket_friend_circle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvFirstName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImageIBFC);
            tvFirstName = itemView.findViewById(R.id.tvFirstNameIBFC);
        }

        public void bind(User user) {
            tvFirstName.setText(user.getFirstName());
            if (user.getImage() != null) {
                Glide.with(context).load(user.getImage().getUrl()).circleCrop().into(ivProfileImage);
                return;
            }
            Glide.with(context).load(R.drawable.profile_placeholder).circleCrop().into(ivProfileImage);

        }
    }

}

