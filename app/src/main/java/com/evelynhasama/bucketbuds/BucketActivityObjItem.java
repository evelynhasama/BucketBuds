package com.evelynhasama.bucketbuds;

public class BucketActivityObjItem extends BucketActivityItem {

    private ActivityObj activityObj;

    public ActivityObj getActivityObj() {
        return activityObj;
    }

    public void setActivityObj(ActivityObj activityObj) {
        this.activityObj = activityObj;
    }

    @Override
    public int getType() {
        return TYPE_ACTIVITY;
    }

}
