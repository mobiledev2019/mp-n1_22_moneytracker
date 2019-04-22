package com.example.t2m.moneytracker.data;

public interface ItemClickListener<T> {
    void onClickItem(int position, T item);
}
