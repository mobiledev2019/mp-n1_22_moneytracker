package com.example.t2m.moneytracker.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Pair;

import java.util.List;

public class TransactionPagerAdapter extends FragmentStatePagerAdapter {
    public List<Pair<String,Fragment>> mFragmentList;

    public TransactionPagerAdapter(FragmentManager fm, List<Pair<String,Fragment>> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    public void updateValues( List<Pair<String,Fragment>> fragmentList) {
        mFragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i).second;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentList.get(position).first;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
