package com.evelynhasama.bucketbuds;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder> {

    public static final String TAG = "BucketListAdapter";
    private static final String ARG_BUCKET_LIST = "bucketList";
    Context context;
    FragmentActivity activity;
    List<BucketList> buckets;
    View view;
    int selectedFilterId;

    public BucketListAdapter(Context context, List<BucketList> buckets, int selectedFilterId, FragmentActivity activity){
        this.context = context;
        this.buckets = buckets;
        this.selectedFilterId = selectedFilterId;
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

        public ViewHolder(View itemView) {
            super(itemView);
            ivBucketImage = itemView.findViewById(R.id.ivBucketImageIBL);
            tvBucketName = itemView.findViewById(R.id.tvBucketNameIBL);
        }

        public void bind(BucketList bucket) {
            Boolean bucketCompleted = bucket.getCompleted();
            if ((selectedFilterId == R.id.rbCompleted && !bucketCompleted) || (selectedFilterId == R.id.rbActive && bucketCompleted)) {
                itemView.setVisibility(View.GONE);
                return;
            }
            itemView.setVisibility(View.VISIBLE);

            Glide.with(context).load(bucket.getImage().getUrl()).centerCrop().into(ivBucketImage);
            tvBucketName.setText(bucket.getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment myFragment = BucketActivitiesFragment.newInstance(bucket);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
                }
            });

        }
    }

    public void clear(){
        buckets.clear();
    }

    public void filter(int id){
        selectedFilterId = id;
        notifyDataSetChanged();
    }

}
