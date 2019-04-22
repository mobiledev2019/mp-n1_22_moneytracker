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
    List<Pair<String,Fragment>> mTabFragment;
    List<Transaction> mListTransaction;
    ITransactionsDAO iTransactionsDAO;
    Wallet mCurrentWallet = null;

    DateUtils dateUtils;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transaction_tab,container,false);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.page_view);
        mFabAddTransaction = view.findViewById(R.id.fab_add_transaction);
        addEvents();
        dateUtils = new DateUtils();
        mTabFragment = new ArrayList<>();
        mCurrentWallet = WalletsManager.getInstance(this.getContext()).getCurrentWallet();
        iTransactionsDAO = new TransactionsDAOImpl(this.getContext());
        mListTransaction = iTransactionsDAO.getAllTransactionByWalletId(mCurrentWallet.getWalletId());
        mAdapter = new TransactionPagerAdapter(getFragmentManager(),mTabFragment);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        new loadTabs().execute();
        return view;
    }

    private void scrollToCurrentMonth() {
        for(int  i = mTabFragment.size()- 1; i >= 0;--i) {
            if(mTabFragment.get(i).first.compareTo(getString(R.string.current_month)) == 0) {
                scrollToTabIndex(i,0.0f,true);
                break;
            }
        }
    }

    private void scrollToTabIndex(int index,float positionOffset,boolean updateSelectedText) {
        mTabLayout.setScrollPosition(index,positionOffset,updateSelectedText);
        mViewPager.setCurrentItem(index);

    }


    @Override
    public void onStart() {
        super.onStart();
    }


    private void addEvents() {
        mFabAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddTransactionActivity.class);
                startActivityForResult(intent,FAB_ADD_TRANSACTION_REQUEST_CODE);
            }
        });
    }

    private void addTabs() {
        MTDate currentDate = new MTDate();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonth();
        int year,month = 0;
        for (year = 2017; year <= currentYear; ++year) {
            for( month = 0; month < 12 ; ++month ) {
                if(year == currentYear && month > currentMonth) break;
                MTDate mtDate = new MTDate(year,month,1);
                DateRange dateRange = new DateRange(
                        mtDate.firstDayOfMonth().setTimeToBeginningOfDay().toDate(),
                        mtDate.lastDayOfMonth().setTimeToEndOfDay().toDate());
                addTab(dateRange);
            }
        }
        if(month == 12) {
            month = 0;
            ++year;
        }
        MTDate mtDate = new MTDate(year,month,1);
        DateRange dateRange = new DateRange(mtDate.firstDayOfMonth().toDate(),mtDate.lastDayOfMonth().toDate());
        addTab(dateRange);
    }

    private void addTab(DateRange dateRange) {
        List<Transaction> transactions = filterTransactions(dateRange,mListTransaction);

        String title = getTitle(dateRange.getDateFrom());
        Date currentDate = Calendar.getInstance().getTime();
        Fragment fragment =  TransactionListFragment.newInstance(transactions);
        mTabFragment.add(new Pair<>(title,fragment));

    }

    private String getTitle(Date date) {
        MTDate mtDate = new MTDate(date);
        String title = String.format("%d/%d",mtDate.getMonth() + 1,mtDate.getYear());
        MTDate currentDate = new MTDate();
        DateRange dateRange = new DateRange(
                currentDate.firstDayOfMonth().setTimeToBeginningOfDay().toDate(),
                currentDate.lastDayOfMonth().setTimeToEndOfDay().toDate());
        if(dateUtils.isDateRangeContainDate(dateRange,date)) {
            title = getString(R.string.current_month);
        }
        else if (dateUtils.isFutureDate(dateRange.getDateTo(),date)) {
            title = getString(R.string.future_transactions);
        }
        return title;
    }

    private List<Transaction> filterTransactions(DateRange dateRange, List<Transaction> transactions) {
        List<Transaction> filter = new ArrayList<>();
        for(Transaction t : transactions) {
            if(dateUtils.isDateRangeContainDate(dateRange,t.getTransactionDate())) {
                filter.add(t);
            }
        }
        return filter;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FAB_ADD_TRANSACTION_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Transaction transaction = (Transaction) data.getSerializableExtra(AddTransactionActivity.EXTRA_TRANSACTION);
                this.addTransaction(transaction);
            }
        }
    }


    public void addTransaction(Transaction transaction) {
        mListTransaction.add(transaction);
        String title = getTitle(transaction.getTransactionDate());
        for(Pair<String,Fragment> tab : mTabFragment) {
            if(title.compareTo(tab.first) ==0) {
                if(tab.second instanceof TransactionListFragment) {
                    ((TransactionListFragment)tab.second).add(transaction);

                }
            }
        }
    }

    // =====================================================
    private Dialog mDialog;

    private class loadTabs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mDialog = new Dialog(getActivity());
            // chu y phai dat truoc setcontentview
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.loading_view);

            mDialog.setCancelable(false);
            mDialog.show();
        }

        protected Void doInBackground(Void... unused) {

            // su dung phuong thuc de update lai adapter
            getActivity().runOnUiThread(runnableUdapteAdapter);

            return (null);
        }

        protected void onPostExecute(Void unused) {
            mDialog.dismiss();
            scrollToCurrentMonth();
        }
    }

    private Runnable runnableUdapteAdapter = new Runnable() {

        @Override
        public void run() {
            // thuc hien update lai adapter
            try {
                mTabFragment.clear();
                addTabs();
                mAdapter.updateValues(mTabFragment);

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    };


}
