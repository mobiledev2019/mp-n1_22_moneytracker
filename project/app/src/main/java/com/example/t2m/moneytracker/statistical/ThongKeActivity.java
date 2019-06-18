package com.example.t2m.moneytracker.statistical;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.adapter.PageThongKeAdapter;

public class ThongKeActivity extends AppCompatActivity implements View.OnClickListener{
    ViewPager pager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_le_ct);
        // thêm các page vào activity ThongKe (tab 3)
        AddPager();
    }

    private void AddPager(){
        try{
            pager = (ViewPager) findViewById(R.id.view_pager);
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            FragmentManager manager = getSupportFragmentManager();
            PageThongKeAdapter adapter = new PageThongKeAdapter(manager);
            pager.setAdapter(adapter);
            tabLayout.setupWithViewPager(pager);
            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setTabsFromPagerAdapter(adapter);
        }catch (Exception e){
            Log.e("lỗi","dfdf");
        }
    }
    @Override
    public void onClick(View v) {

    }
}
