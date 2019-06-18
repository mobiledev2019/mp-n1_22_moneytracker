package com.example.t2m.moneytracker.transaction;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.adapter.TransactionListAdapter;
import com.example.t2m.moneytracker.model.MTDate;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.pinnedlistview.PinnedHeaderListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewTransactionListActivity extends AppCompatActivity {
    private static final String LOG_TAG = "TransactionListActivity";
    private PinnedHeaderListView mLViewTransaction;
    private TransactionListAdapter mAdapter;
    private LinearLayout mBlankLayout;
    List<Transaction> mItems;
    List<Pair<Date, List<Transaction>>> mFilterItems;

    public static final String BUNDLE_LIST_ITEM = "TransactionListFragment.bundle.list_items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            mItems = (ArrayList<Transaction>) getIntent().getSerializableExtra(BUNDLE_LIST_ITEM);
        } else {
            mItems = new ArrayList<>();
        }

        Log.d(LOG_TAG, "create view items " + mItems.size());

        addControls();
        addEvents();
        updateUI();
    }

    private void addEvents() {
        mFilterItems = new ArrayList<>();
        mAdapter = new TransactionListAdapter(this, mFilterItems);
        mLViewTransaction.setAdapter(mAdapter);
    }

    private void addControls() {
        mLViewTransaction = findViewById(R.id.list_view_transaction);
        mBlankLayout = findViewById(R.id.layout_transaction_empty);

    }

    private void updateUI() {
        if (mItems.size() == 0) {
            mLViewTransaction.setVisibility(View.INVISIBLE);
            mBlankLayout.setVisibility(View.VISIBLE);
        } else {
            mBlankLayout.setVisibility(View.INVISIBLE);

            // thuc hien update lai adapter
            mFilterItems.clear();
            filterPairTransactions(mItems);
            mAdapter.updateValues(mFilterItems);
            mLViewTransaction.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onClickedCancle();
        return true;
    }

    private void onClickedCancle() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void add(Transaction transaction) {
        mItems.add(transaction);
    }

    private void filterPairTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            filterPairTransaction(transaction);
        }
    }

    private void filterPairTransaction(Transaction transaction) {
        int index = -1;
        MTDate date = new MTDate(transaction.getTransactionDate());
        for (int i = 0; i < mFilterItems.size(); ++i) {
            MTDate dateI = new MTDate(mFilterItems.get(i).first);
            if (compareDate(date, dateI) == 0) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            MTDate dateI = new MTDate(mFilterItems.get(index).first);
            if (compareDate(date, dateI) == 0) {
                mFilterItems.get(index).second.add(0, transaction);
            }
        } else {
            index = 0;
            for (int i = 0; i < mFilterItems.size(); ++i) {
                MTDate dateI = new MTDate(mFilterItems.get(i).first);
                if (compareDate(date, dateI) > 0) {
                    index = i;
                    break;
                }
            }
            ArrayList<Transaction> trans = new ArrayList<>();
            trans.add(transaction);
            mFilterItems.add(index, new Pair<Date, List<Transaction>>(transaction.getTransactionDate(), trans));
        }
    }

    private int compareDate(MTDate date1, MTDate date2) {
        return date1.setTimeToBeginningOfDay().getCalendar().compareTo(date2.setTimeToBeginningOfDay().getCalendar());

    }

}
