package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.view.HapticFeedbackConstants;
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

public class FriendBucketsAdapter extends RecyclerView.Adapter<FriendBucketsAdapter.ViewHolder> {

    public static final String TAG = "FriendBucketsAdapter";
    Context context;
    List<BucketList> buckets;
    View view;

    public FriendBucketsAdapter(Context context, List<BucketList> buckets) {
        this.context = context;
        this.buckets = buckets;
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
        ImageView ivCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBucketImage = itemView.findViewById(R.id.ivBucketImageIBL);
            tvBucketName = itemView.findViewById(R.id.tvBucketNameIBL);
            tvUserCount = itemView.findViewById(R.id.tvUserCountIBL);
            tvActivityCount = itemView.findViewById(R.id.tvActivityCountIBL);
            ivCheckBox = itemView.findViewById(R.id.ivCheckBoxIBL);
        }

        public void bind(BucketList bucket) {

            tvBucketName.setText(bucket.getName());
            Glide.with(context).load(bucket.getImage().getUrl()).centerCrop().into(ivBucketImage);
            tvUserCount.setText(String.valueOf(bucket.getUserCount()));
            tvActivityCount.setText(String.valueOf(bucket.getActivityCount()));

            int checkImage = bucket.getCompleted()? R.drawable.ic_checked_box : R.drawable.ic_unchecked_box;
            ivCheckBox.setImageResource(checkImage);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    // join bucket
                    return false;
                }
            });
        }
    }

}
