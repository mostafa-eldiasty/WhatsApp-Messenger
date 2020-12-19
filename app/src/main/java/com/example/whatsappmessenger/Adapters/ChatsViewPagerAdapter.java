package com.example.whatsappmessenger.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.whatsappmessenger.Fragments.CallsFragment;
import com.example.whatsappmessenger.Fragments.ChatsFragment;
import com.example.whatsappmessenger.Fragments.StatusFragment;

public class ChatsViewPagerAdapter extends FragmentPagerAdapter {
    int numOfTabs;

    public ChatsViewPagerAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatsFragment();
            case 1:
                return new StatusFragment();
            case 2:
                return new CallsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
