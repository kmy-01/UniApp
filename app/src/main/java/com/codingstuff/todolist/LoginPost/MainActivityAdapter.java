package com.codingstuff.todolist.LoginPost;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainActivityAdapter extends FragmentPagerAdapter {
    private Context context;
    int totalTabs;

    // These titles will be displayed as tab headers
    private String[] tabTitles = new String[]{"Feed", "Post"};

    private static final String TAG = "MainActivityAdapter";

    public MainActivityAdapter(@NonNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new FeedFragment();
            case 1 :
                return new PostFragment();
            default:
                throw new RuntimeException("Invalid tab position");
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}