package com.evelynhasama.bucketbuds;

public class BucketFriendItem extends BucketFriendListItem {

    private User user;

    private Boolean added;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getAdded() {
        return added;
    }

    public void setAdded(Boolean added) {
        this.added = added;
    }


    @Override
    public int getType() {
        return TYPE_FRIEND;
    }

}
