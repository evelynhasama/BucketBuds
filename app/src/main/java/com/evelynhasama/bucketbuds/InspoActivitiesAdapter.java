package com.evelynhasama.bucketbuds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class InspoActivitiesAdapter extends RecyclerView.Adapter<InspoActivitiesAdapter.ViewHolder> {

    public static final String TAG = "BucketUsersAdapter";
    public static final int TICKETMASTER_LOGO = R.drawable.ticketmaster_logo;
    public static final int SEATGEEK_LOGO = R.drawable.seatgeek_logo;
    public static final int MUSEMENT_LOGO = R.drawable.musement_logo;
    Context mContext;
    List<ActivityObj> mActivities;
    FragmentActivity mFragmentActivity;

    public InspoActivitiesAdapter(Context context, List<ActivityObj> activities, FragmentActivity fragmentActivity) {
        this.mContext = context;
        this.mActivities = activities;
        this.mFragmentActivity = fragmentActivity;
    }

    public void clear() {
        mActivities.clear();
        notifyDataSetChanged();
    }

    public void addData(List<ActivityObj> activityObjs) {
        mActivities.addAll(activityObjs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityObj activityObj = mActivities.get(position);
        holder.bind(activityObj);
    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCompany;
        TextView tvTitle;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCompany = itemView.findViewById(R.id.ivCheckBoxIA);
            tvTitle = itemView.findViewById(R.id.tvTitleIA);
            this.itemView = itemView;
        }

        public void bind(ActivityObj activityObj) {
            tvTitle.setText(activityObj.getName());
            int logo;
            switch (activityObj.getCompany()){
                case ActivityObj.TICKETMASTER:
                    logo = TICKETMASTER_LOGO;
                    break;
                case ActivityObj.SEATGEEK:
                    logo = SEATGEEK_LOGO;
                    break;
                case ActivityObj.MUSEMENT:
                    logo = MUSEMENT_LOGO;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + activityObj.getCompany());
            }
            Glide.with(mContext).load(logo).centerCrop().into(ivCompany);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment myFragment = InspoActivityDetailsFragment.newInstance(activityObj);
                    mFragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, myFragment).addToBackStack(null).commit();
                }
            });
        }
    }

}
