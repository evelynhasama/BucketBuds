package com.example.bucketbuds;

import android.app.usage.NetworkStats;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder> {

    Context context;
    List<BucketList> buckets;
    View view;

    public BucketListAdapter(Context context, List<BucketList> buckets){
        this.context = context;
        this.buckets = buckets;
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
            Glide.with(context).load(bucket.getImage().getUrl()).centerCrop().into(ivBucketImage);
            tvBucketName.setText(bucket.getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                    // TODO: navigate to Bucket List Activities screen
                }
            });

        }
    }

    public void clear(){
        buckets.clear();
    }

}
