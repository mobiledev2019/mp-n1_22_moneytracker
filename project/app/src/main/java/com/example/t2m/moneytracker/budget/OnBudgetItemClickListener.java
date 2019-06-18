package com.example.t2m.moneytracker.budget;

import android.view.View;

public interface OnBudgetItemClickListener {

    void onItemClick(View view, int position);
    void onItemLongClick(View view,int position);

}