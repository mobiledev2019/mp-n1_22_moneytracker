package com.example.t2m.moneytracker.budget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.adapter.TimeRangeListAdapter;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.utilities.DateUtils;

import java.util.ArrayList;

public class SelectBudgetTimeRangeActivity extends AppCompatActivity {

    public static final String EXTRA_BUDGET_TIME_RANGE = "com.example.t2m.money_tracker.extra.budget";

    ListView mListTime;
    TimeRangeListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_time_range);
        addControls();
        addEvents();
    }
    private void addControls() {
        mListTime = findViewById(R.id.list_time_range);
    }
    private void addEvents() {
        ArrayList<String> timeRanges = getBudgetTimeRange();
        mAdapter = new TimeRangeListAdapter(this,R.layout.item_picker_date_range,timeRanges);
        mListTime.setAdapter(mAdapter);
        mListTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickTimeRange(parent,view,position,id);
            }
        });
    }

    private ArrayList<String> getBudgetTimeRange() {
        ArrayList<String> timeRanges = new ArrayList<>();
        timeRanges.add(getString(R.string.thisweek));
        timeRanges.add(getString(R.string.thismonth));
        timeRanges.add(getString(R.string.thisquarter));
        timeRanges.add(getString(R.string.thisyear));
        timeRanges.add(getString(R.string.nextweek));
        timeRanges.add(getString(R.string.nextmonth));
        timeRanges.add(getString(R.string.nextquarter));
        timeRanges.add(getString(R.string.nextyear));
        timeRanges.add(getString(R.string.time_from));
        return timeRanges;
    }

    private void onClickTimeRange(AdapterView<?> parent, View view, int position, long id) {
        String timeRange = (String) parent.getAdapter().getItem(position);
        // time tùy chỉnh
        if(timeRange == getString(R.string.time_from)) {

        }
        else {
            Intent data = new Intent();
            DateRange dateRange = new DateUtils().getDateRangeForPeriod(this,timeRange);
            data.putExtra(EXTRA_BUDGET_TIME_RANGE,dateRange);
            setResult(RESULT_OK,data);
            finish();
        }

    }


}
