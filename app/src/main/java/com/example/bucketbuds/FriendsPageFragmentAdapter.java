package com.example.bucketbuds;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

public class FriendsPageFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Friends", "Add Friends" };
    public static final String TAG = "PageFragmentAdapter";
    Context context;
    UserPub userPub;

    public FriendsPageFragmentAdapter(@NonNull @NotNull FragmentManager fm, int behavior, Context context, UserPub userPub) {
        super(fm, behavior);
        this.context = context;
        this.userPub = userPub;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return FriendsPageFragment.newInstance(position, userPub);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
