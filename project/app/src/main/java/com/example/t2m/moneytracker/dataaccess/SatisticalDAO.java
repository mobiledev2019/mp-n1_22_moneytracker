package com.example.t2m.moneytracker.dataaccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.Statistical;
import com.example.t2m.moneytracker.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SatisticalDAO implements ITStatistical{

    MoneyTrackerDBHelper dbHelper ;
    ICategoriesDAO iCategoriesDAO;

    public SatisticalDAO(Context context) {
        dbHelper = new MoneyTrackerDBHelper(context);
    }
    @Override
    public List<Statistical> getAllStatistical() {
        return null;
    }

    @Override
    public List<Statistical> getAllStatisticalByWalletId(int walletId) {
        return null;
    }

    @Override
    public List<Statistical> getAllStatisticalByPeriod(int walletId, DateRange dateRange) {
        return null;
    }



}
