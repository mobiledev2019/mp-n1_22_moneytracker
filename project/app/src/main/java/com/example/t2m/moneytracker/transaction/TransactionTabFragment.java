package com.example.t2m.moneytracker.transaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.t2m.moneytracker.R;

import com.example.t2m.moneytracker.adapter.TransactionPagerAdapter;
import com.example.t2m.moneytracker.common.Constants;
import com.example.t2m.moneytracker.dataaccess.ITransactionsDAO;

import com.example.t2m.moneytracker.dataaccess.MoneyTrackerDBHelper;
import com.example.t2m.moneytracker.dataaccess.TransactionsDAOImpl;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.MTDate;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.Wallet;

import com.example.t2m.moneytracker.utilities.DateUtils;
import com.example.t2m.moneytracker.utilities.TransactionsManager;
import com.example.t2m.moneytracker.utilities.WalletsManager;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;

import java.util.List;

public class TransactionTabFragment extends Fragment {

    public static final int FAB_ADD_TRANSACTION_REQUEST_CODE = 0;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TransactionPagerAdapter mAdapter;

    private FloatingActionButton mFabAddTransaction;
    List<Pair<String, Fragment>> mTabFragment;
    List<Transaction> mListTransaction;
    ITransactionsDAO iTransactionsDAO;
    Wallet mCurrentWallet = null;

    DateUtils dateUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transaction_tab, container, false);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.page_view);
        mFabAddTransaction = view.findViewById(R.id.fab_add_transaction);

        dateUtils = new DateUtils();
        mTabFragment = new ArrayList<>();
        mCurrentWallet = WalletsManager.getInstance(this.getContext()).getCurrentWallet();
        iTransactionsDAO = new TransactionsDAOImpl(this.getContext());
        mListTransaction = iTransactionsDAO.getAllTransactionByWalletId(mCurrentWallet.getWalletId());
        mAdapter = new TransactionPagerAdapter(getChildFragmentManager(), mTabFragment);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        addEvents();

        //new loadTabs(getActivity()).execute();
        return view;
    }

    private void scrollToCurrentMonth() {
        for (int i = mTabFragment.size() - 1; i >= 0; --i) {
            if (mTabFragment.get(i).first.compareTo(getString(R.string.current_month)) == 0) {
                scrollToTabIndex(i, 0.0f, true);
                break;
            }
        }
    }

    private void scrollToTabIndex(int index, float positionOffset, boolean updateSelectedText) {
        mTabLayout.setScrollPosition(index, positionOffset, updateSelectedText);
        mViewPager.setCurrentItem(index, true);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    private void addEvents() {
        mFabAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTransactionActivity.class);
                startActivityForResult(intent, FAB_ADD_TRANSACTION_REQUEST_CODE);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mAdapter.getItem(i);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void addTabs(DateRange dateRange) {

        int year, month = 0;
        int from_month = dateRange.getDateFrom().getMonth();
        int from_year = dateRange.getDateFrom().getYear();
        int to_month = dateRange.getDateTo().getMonth();
        int to_year = dateRange.getDateTo().getYear();

        List<DateRange> periods = new ArrayList<>();
        for (year = from_year; year <= to_year; ++year) {
            for (month = 0; month < 12; ++month) {
                if (year == from_year && month < from_month) continue;
                if (year == to_year && month > to_month) break;
                MTDate firstDay = new MTDate(year, month, 1).firstDayOfMonth().setTimeToBeginningOfDay();
                MTDate lastDay = new MTDate(year, month, 1).lastDayOfMonth().setTimeToEndOfDay();
                DateRange period = new DateRange(firstDay, lastDay);
                addTab(period);
            }
        }
        if (month == 12) {
            month = 0;
            ++year;
        }
        MTDate firstFuture = new MTDate(year, month, 1).firstDayOfMonth();
        MTDate lastFuture = new MTDate(year + 100, month, 1).lastDayOfMonth();
        DateRange period = new DateRange(firstFuture, lastFuture);
        addTab(period);
    }

    private void addTab(DateRange dateRange) {
        List<Transaction> transactions = filterTransactions(dateRange, mListTransaction);

        String title = getTitle(dateRange.getDateFrom().toDate());
//        boolean isNeedAdd = true;
//        for (Pair<String, Fragment> tab : mTabFragment) {
//            if (tab.first.equals(title)) {
//                ((TransactionListFragment) tab.second).setItems(transactions);
//                isNeedAdd = false;
//                break;
//            }
//        }
//        if (isNeedAdd) {
            Fragment fragment = TransactionListFragment.newInstance(transactions);
            mTabFragment.add(new Pair<>(title, fragment));
            //       }

    }

    // lay title ngay theo range
    private String getTitle(DateRange range) {
        MTDate mtDate = range.getDateFrom();
        String title = String.format("%d/%d", mtDate.getMonth() + 1, mtDate.getYear());
        MTDate firstDayOfMonth = new MTDate().firstDayOfMonth().setTimeToBeginningOfDay();
        MTDate lastDayOfMonth = new MTDate().lastDayOfMonth().setTimeToEndOfDay();
        DateRange currentMonth = new DateRange(firstDayOfMonth, lastDayOfMonth);
        if (dateUtils.isDateRangeContainDate(currentMonth, mtDate)) {
            title = getString(R.string.current_month);
        } else if (dateUtils.isFutureDate(currentMonth.getDateTo(), mtDate)) {
            title = getString(R.string.future_transactions);
        }
        return title;
    }

    private String getTitle(Date date) {
        MTDate mtDate = new MTDate(date);
        String title = String.format("%d/%d", mtDate.getMonth() + 1, mtDate.getYear());
        MTDate firstDayOfMonth = new MTDate().firstDayOfMonth().setTimeToBeginningOfDay();
        MTDate lastDayOfMonth = new MTDate().lastDayOfMonth().setTimeToEndOfDay();
        DateRange currentMonth = new DateRange(firstDayOfMonth, lastDayOfMonth);
        if (dateUtils.isDateRangeContainDate(currentMonth, mtDate)) {
            title = getString(R.string.current_month);
        } else if (dateUtils.isFutureDate(currentMonth.getDateTo(), mtDate)) {
            title = getString(R.string.future_transactions);
        }
        return title;
    }

    private List<Transaction> filterTransactions(DateRange dateRange, List<Transaction> transactions) {
        List<Transaction> filter = new ArrayList<>();
        for (Transaction t : transactions) {
            if (dateUtils.isDateRangeContainDate(dateRange, t.getTransactionDate())) {
                filter.add(t);
            }
        }
        return filter;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TransactionTabFragment.class.getSimpleName(), "On Activity Result");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FAB_ADD_TRANSACTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Transaction transaction = (Transaction) data.getSerializableExtra(AddTransactionActivity.EXTRA_TRANSACTION);
                mListTransaction.add(transaction);
            }
        }
    }

    @Override
    public void onResume() {
        Log.d(TransactionTabFragment.class.getSimpleName(), "On Resume");
        super.onResume();
        new loadTabs(this.getActivity()).execute();
    }

    // =====================================================
    private Dialog mDialog;

    private class loadTabs extends AsyncTask<Void, Void, Void> {

        public loadTabs(Activity activity) {
            this.activity = activity;
        }

        private Activity activity;

        @Override
        protected void onPreExecute() {
            mDialog = new Dialog(activity);
            // chu y phai dat truoc setcontentview
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.loading_view);
            mListTransaction.clear();
            mListTransaction.addAll(iTransactionsDAO.getAllTransactionByWalletId(mCurrentWallet.getWalletId()));
            mDialog.setCancelable(false);
            mDialog.show();
        }

        protected Void doInBackground(Void... unused) {

            // su dung phuong thuc de update lai adapter
            activity.runOnUiThread(runnableUpdateAdapter);

            return (null);
        }

        protected void onPostExecute(Void unused) {

            mDialog.dismiss();
            scrollToCurrentMonth();
        }
    }

    private Runnable runnableUpdateAdapter = new Runnable() {

        @Override
        public void run() {
            // thuc hien update lai adapter
            try {
                mTabFragment.clear();
                DateRange dateRange = new DateRange(new MTDate(2018, 0, 1).firstDayOfMonth().setTimeToBeginningOfDay(),
                        new MTDate());
                addTabs(dateRange);
                mAdapter.updateValues(mTabFragment);


            } catch (Exception e) {
                // TODO: handle exception
                Log.d(TransactionTabFragment.class.getSimpleName(), "Update adapter failed");
            }

        }
    };


}
