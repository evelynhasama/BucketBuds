package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.parse.ParseException;
import com.parse.SaveCallback;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class BucketActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    FragmentActivity activity;
    List<BucketActivityItem> allItemsList;
    public static final String TAG = "BucketActivitiesAdapter";
    public static final int CHECKED = R.drawable.ic_checked_box;
    public static final int UNCHECKED = R.drawable.ic_unchecked_box;
    BucketActivityHeaderItem completedHeader;

    public BucketActivitiesAdapter(Context context, List<BucketActivityItem> allItemsList, FragmentActivity activity, BucketActivityHeaderItem completedHeader) {
        this.allItemsList = allItemsList;
        this.context = context;
        this.activity = activity;
        this.completedHeader = completedHeader;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        View view;

        switch (viewType) {
            case BucketActivityObjItem.TYPE_ACTIVITY:
                view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
                viewHolder = new BucketActivitiesAdapter.ActivityObjViewHolder(view);
                break;

            case BucketActivityHeaderItem.TYPE_HEADER:
                view = LayoutInflater.from(context).inflate(R.layout.item_bucket_friend_header, parent, false);
                viewHolder = new BucketActivitiesAdapter.HeaderViewHolder(view);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case BucketActivityItem.TYPE_ACTIVITY:

                BucketActivityObjItem activityItem = (BucketActivityObjItem) allItemsList.get(position);
                BucketActivitiesAdapter.ActivityObjViewHolder activityObjViewHolder= (BucketActivitiesAdapter.ActivityObjViewHolder) holder;
                ActivityObj activityObj = activityItem.getActivityObj();
                activityObjViewHolder.tvTitle.setText(activityObj.getName());

                Boolean completed = activityObj.getCompleted();
                int checkBox = completed ? CHECKED : UNCHECKED;
                activityObjViewHolder.ivCheckBox.setImageResource(checkBox);

                View.OnClickListener updateCompletedClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int temp_position = holder.getAdapterPosition();
                        int addPosition = allItemsList.indexOf(completedHeader);
                        int checkId = completed ? UNCHECKED : CHECKED;
                        activityObjViewHolder.ivCheckBox.setImageResource(checkId);
                        allItemsList.remove(temp_position);
                        allItemsList.add(addPosition, activityItem);
                        activityObj.setCompleted(!completed);
                        activityObj.saveInBackground(getActivitySaveCallback(temp_position, addPosition));
                    }
                };
                activityObjViewHolder.ivCheckBox.setOnClickListener(updateCompletedClickListener);

                View.OnClickListener activityDetailsClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // navigate to activity details screen
                        Fragment myFragment = ActivityDetailsFragment.newInstance(activityObj);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
                    }
                };
                activityObjViewHolder.view.setOnClickListener(activityDetailsClickListener);
                break;

            case  BucketActivityItem.TYPE_HEADER:

                BucketActivityHeaderItem headerItem = (BucketActivityHeaderItem) allItemsList.get(position);
                BucketActivitiesAdapter.HeaderViewHolder headerViewHolder = (BucketActivitiesAdapter.HeaderViewHolder) holder;
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

    // ViewHolder for activity row item
    class ActivityObjViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivCheckBox;
        View view;

        public ActivityObjViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitleIA);
            ivCheckBox = v.findViewById(R.id.ivCheckBoxIA);
            view = v;
        }
    }

    // View holder for header row item
    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader;

        public HeaderViewHolder(View v) {
            super(v);
            tvHeader = v.findViewById(R.id.tvHeaderIBFH);
        }
    }

    public SaveCallback getActivitySaveCallback(int temp_position, int addPosition){
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "saving activity", e);
                    return;
                }
                notifyItemMoved(temp_position, addPosition);
                notifyItemChanged(addPosition, null);
            }
        };
    }

}

