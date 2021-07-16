package com.example.bucketbuds;

public class BucketFriendHeaderItem extends BucketFriendListItem {

    private String section;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

}
