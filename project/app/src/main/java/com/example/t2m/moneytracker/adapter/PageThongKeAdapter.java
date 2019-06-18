package com.example.t2m.moneytracker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.t2m.moneytracker.statistical.Fragment_ThongKe_Tab0;

public class PageThongKeAdapter extends FragmentStatePagerAdapter {
    public PageThongKeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag=new Fragment_ThongKe_Tab0();
                break;


        }
        return frag;
    }
    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title="Số liệu";

        }
        return title;
    }
}
