package com.codingstuff.todolist.LoginPost;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LogInAdapter extends FragmentPagerAdapter {
    private Context context;
    int totalTabs;
    private String[] tabTitles = new String[]{"Log In", "Sign Up"};

    public LogInAdapter(@NonNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    // overriding getPageTitle()
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public Fragment getItem(int position){
        switch (position){
            case 0 :
                return new LogInFragment();
            case 1 :
                return new SignUpFragment();
            default:
                throw new RuntimeException("Invalid tab position");
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
