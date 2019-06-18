package com.example.t2m.moneytracker.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.pinnedlistview.SectionedBaseAdapter;
import com.example.t2m.moneytracker.utilities.CurrencyUtils;
import com.example.t2m.moneytracker.utils.LanguageUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TransactionListAdapter extends SectionedBaseAdapter {

    private Context mContext;
    public List<Pair<Date,List<Transaction>>> mItems;

    public TransactionListAdapter(Context context, List<Pair<Date,List<Transaction>>> items) {
        mContext = context;
        mItems = items;
    }

    public CurrencyUtils currencyUtils;


    public void updateValues(List<Pair<Date,List<Transaction>>> items) {
        mItems = items;
        notifyDataSetChanged();
    }
    @Override
    public Object getItem(int section, int position) {
        return mItems.get(section).second.get(position);
    }

    @Override
    public long getItemId(int section, int position) {

        int res = 0;
        for (int i = 0; i < section - 1; i++) {
            res += mItems.get(i).second.size();
            res += 1;
        }
        return res + position;
    }

    @Override
    public int getSectionCount() {
        return mItems.size();
    }

    @Override
    public int getCountForSection(int section) {
        return mItems.get(section).second.size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (RelativeLayout) inflator.inflate(R.layout.snippet_transaction_item, null);
        } else {
            layout = (RelativeLayout) convertView;
        }
        ItemViewHolder holder = new ItemViewHolder();

        holder.item_image = layout.findViewById(R.id.image_transaction_item);
        holder.item_label = layout.findViewById(R.id.text_transaction_label);
        holder.item_note = layout.findViewById(R.id.text_transaction_note);
        holder.item_money_trading = layout.findViewById(R.id.text_item_money_trading);

        Transaction transaction = mItems.get(section).second.get(position);
        holder.item_label.setText(transaction.getCategory().getCategory());
        holder.item_note.setText(transaction.getTransactionNote());

        holder.item_money_trading.setText(String.valueOf(transaction.getMoneyTrading()));


        // lấy ảnh từ asset
        String base_path = "category/";
        try {
            Drawable img = Drawable.createFromStream(mContext.getAssets().open(base_path + transaction.getCategory().getIcon()),null);
            holder.item_image.setImageDrawable(img);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (transaction.getMoneyTradingWithSign() >= 0)
            holder.item_money_trading.setTextColor(parent.getResources().getColor(R.color.colorMoneyTradingPositive));
        else holder.item_money_trading.setTextColor(parent.getResources().getColor(R.color.colorMoneyTradingNegative));
        return layout;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        RelativeLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (RelativeLayout) inflator.inflate(R.layout.snippet_transaction_header, null);
        } else {
            layout = (RelativeLayout) convertView;
        }

        HeaderViewHolder holder = new HeaderViewHolder();

        holder.header_date = layout.findViewById(R.id.text_date_header);
        holder.header_day = layout.findViewById(R.id.text_day_header);
        holder.header_month_year = layout.findViewById(R.id.text_month_year_header);
        holder.header_money_trading = layout.findViewById(R.id.text_money_trading);

        Locale locale = new Locale(LanguageUtils.getCurrentLanguage().getCode());
        Date date = mItems.get(section).first;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd",locale);
        holder.header_date.setText(simpleDateFormat.format(date));

        simpleDateFormat = new SimpleDateFormat("EEEE",locale);
        holder.header_day.setText(simpleDateFormat.format(date));
        simpleDateFormat = new SimpleDateFormat("MM/yyyy",locale);
        holder.header_month_year.setText(simpleDateFormat.format(date));

        float moneyTrading = 0;
        List<Transaction> transactions = mItems.get(section).second;
        for(Transaction tran : transactions) {
            moneyTrading += tran.getMoneyTradingWithSign();
        }
        String moneyHeader = String.valueOf(moneyTrading);
        holder.header_money_trading.setText(moneyHeader);

//        if (moneyTrading >= 0)
//            holder.header_money_trading.setTextColor(parent.getResources().getColor(R.color.colorMoneyTradingPositive));
//        else holder.header_money_trading.setTextColor(parent.getResources().getColor(R.color.colorMoneyTradingNegative));


        return layout;
    }

    public static class HeaderViewHolder {
        public TextView header_date;
        public TextView header_day;
        public TextView header_month_year;
        public TextView header_money_trading;

    }

    public static class ItemViewHolder {
        public ImageView item_image;
        public TextView item_label;
        public TextView item_note;
        public TextView item_money_trading;
    }

}
