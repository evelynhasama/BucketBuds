package com.evelynhasama.bucketbuds;

public class BucketActivityHeaderItem extends BucketActivityItem{

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
