package com.example.t2m.moneytracker.transaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.t2m.moneytracker.R;

import com.example.t2m.moneytracker.adapter.TransactionListAdapter;
import com.example.t2m.moneytracker.common.Constants;
import com.example.t2m.moneytracker.model.MTDate;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.pinnedlistview.PinnedHeaderListView;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionListFragment extends Fragment {

    private static final String LOG_TAG = "TransactionList.LOG_TAG";
    private PinnedHeaderListView mLViewTransaction;
    private TransactionListAdapter mAdapter;
    List<Transaction> mItems;
    List<Pair<Date,List<Transaction>>> mFilterItems;
    View headerView;

    private static final String BUNDLE_LIST_ITEM = "TransactionListFragment.bundle.list_items";
    private Runnable runableUpdateAdapter;

    public static TransactionListFragment newInstance(List<Transaction> items) {
        Log.d(LOG_TAG,"create new instance "+ items.size());
        TransactionListFragment fragment = new TransactionListFragment();
        fragment.setItems(items);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_LIST_ITEM,(ArrayList<Transaction>)items);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mItems == null){
            if(savedInstanceState != null) {
                mItems =(ArrayList<Transaction>) savedInstanceState.getSerializable(BUNDLE_LIST_ITEM);
            }
            else {
                mItems = new ArrayList<>();
            }
        }
        Log.d(LOG_TAG,"create view items " + mItems.size());

        View view = inflater.inflate(R.layout.fragment_list_transaction,container,false);
        mLViewTransaction = view.findViewById(R.id.list_view_transaction);
        mFilterItems = new ArrayList<>();
        mAdapter = new TransactionListAdapter(this.getContext(),mFilterItems);
        mLViewTransaction.setAdapter(mAdapter);
        headerView = inflater.inflate(
                R.layout.header_transaction_statistics, null, false);
        mLViewTransaction.addHeaderView(headerView);
        mLViewTransaction.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
                Transaction transaction = (Transaction) mAdapter.getItem(section,position);
                onClickItem(transaction);
            }

            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {

            }
            @Override
            public void onHeaderClick(AdapterView<?> adapterView, View view, int section, long id) {

            }
        });
        new loadTransactions().execute();
        return view;
    }

    private void onClickItem(Transaction transaction) {
        Intent data = new Intent(TransactionListFragment.this.getContext(),ViewTransactionDetailActivity.class);
        data.putExtra(ViewTransactionDetailActivity.EXTRA_TRANSACTION,transaction);
        startActivity(data);
    }

    public void setItems(List<Transaction> items) {
        mItems = items;
    }

    public void add(Transaction transaction) {
        mItems.add(transaction);
        new loadTransactions().execute();
    }

    private void filterPairTransactions(List<Transaction> transactions) {
        for(Transaction transaction : transactions) {
            filterPairTransaction(transaction);
        }
    }

    private void filterPairTransaction(Transaction transaction) {
        int index = -1;
        MTDate date = new MTDate(transaction.getTransactionDate());
        for (int i = 0; i < mFilterItems.size(); ++i) {
            MTDate dateI = new MTDate(mFilterItems.get(i).first);
            if (compareDate(date,dateI) == 0) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            MTDate dateI = new MTDate(mFilterItems.get(index).first);
            if (compareDate(date,dateI) == 0) {
                mFilterItems.get(index).second.add(0, transaction);
            }
        }
        else {
            index = 0;
            for (int i = 0; i < mFilterItems.size(); ++i) {
                MTDate dateI = new MTDate(mFilterItems.get(i).first);
                if (compareDate(date,dateI) > 0) {
                    index = i;
                    break;
                }
            }
            ArrayList<Transaction> trans = new ArrayList<>();
            trans.add(transaction);
            mFilterItems.add(index, new Pair<Date, List<Transaction>>(transaction.getTransactionDate(), trans));
        }
    }

    private int compareDate(MTDate date1,MTDate date2) {
        return date1.setTimeToBeginningOfDay().getCalendar().compareTo(date2.setTimeToBeginningOfDay().getCalendar());

    }

    // =====================================================
    private Dialog mDialog;

    private class loadTransactions extends AsyncTask<Void, Void, Void> {

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
        }
    }

    private Runnable runnableUdapteAdapter = new Runnable() {

        @Override
        public void run() {
            // thuc hien update lai adapter
            try {
                mFilterItems.clear();
                filterPairTransactions(mItems);
                mAdapter.updateValues(mFilterItems);
                //mAdapter.notifyDataSetChanged();
                updateHeaderView();

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    };

    private void updateHeaderView() {

        float tienChi = 0;
        float tienTieu = 0;

        if(headerView != null) {
            for(Transaction tran : mItems) {
                if(tran.getMoneyTrading() < 0) {
                    tienTieu += Math.abs(tran.getMoneyTrading());
                }
                else {
                    tienChi += Math.abs(tran.getMoneyTrading());
                }
            }
        }

        TextView textChi = headerView.findViewById(R.id.fts_so_du_dau);
        TextView textTieu = headerView.findViewById(R.id.fts_so_du_cuoi);
        TextView textConLai = headerView.findViewById(R.id.fts_con_lai);

        textChi.setText(String.format(Constants.PRICE_FORMAT,tienChi));
        textTieu.setText(String.format(Constants.PRICE_FORMAT,tienTieu));
        textConLai.setText(String.format(Constants.PRICE_FORMAT,tienChi - tienTieu));
    }

}
