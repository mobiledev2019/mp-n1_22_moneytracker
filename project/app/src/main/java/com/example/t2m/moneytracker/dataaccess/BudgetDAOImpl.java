package com.example.t2m.moneytracker.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2m.moneytracker.model.Budget;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.MTDate;

import java.util.ArrayList;
import java.util.List;

public class BudgetDAOImpl implements IBudgetDAO {

    public static final String TABLE_BUDGET = "tbl_budgets";
    public static final String BUDGET_ID = "_id";
    public static final String CATEGORY_ID = "categoryId";
    public static final String WALLET_ID = "walletId";
    public static final String AMOUNT = "amount";
    public static final String SPENT = "spent";
    public static final String TIME_START = "timeStart";
    public static final String TIME_END = "timeEnd";
    public static final String LOOP = "loop";
    public static final String STATUS = "status";

    MoneyTrackerDBHelper dbHelper;
    ICategoriesDAO iCategoriesDAO;
    IWalletsDAO iWalletsDAO;

    public BudgetDAOImpl(Context context) {
        dbHelper = new MoneyTrackerDBHelper(context);
        iCategoriesDAO = new CategoriesDAOImpl(context);
        iWalletsDAO = new WalletsDAOImpl(context);
    }

    @Override
    public boolean insertBudget(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORY_ID, budget.getCategory().getId());
        values.put(WALLET_ID, budget.getWallet().getWalletId());
        values.put(AMOUNT, budget.getAmount());
        values.put(SPENT,budget.getSpent());
        values.put(TIME_START, budget.getTimeStart().toDate().getTime());
        values.put(TIME_END,budget.getTimeEnd().toDate().getTime());
        values.put(LOOP, budget.isLoop());
        values.put(STATUS, budget.getStatus());
        int id = (int) db.insert(TABLE_BUDGET,null,values);
        budget.setBudgetId(id);
        db.close();
        return id != -1;
    }

    @Override
    public boolean updateBudget(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORY_ID, budget.getCategory().getId());
        values.put(WALLET_ID, budget.getWallet().getWalletId());
        values.put(AMOUNT, budget.getAmount());
        values.put(SPENT,budget.getSpent());
        values.put(TIME_START, budget.getTimeStart().toDate().getTime());
        values.put(TIME_END,budget.getTimeEnd().toDate().getTime());
        values.put(LOOP, budget.isLoop());
        values.put(STATUS, budget.getStatus());
        db.update(TABLE_BUDGET,values,BUDGET_ID + " = ?",new String[]{String.valueOf(budget.getBudgetId())});
        db.close();
        return true;
    }

    @Override
    public boolean deleteBudget(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_BUDGET, BUDGET_ID + " = ?", new String[]{String.valueOf(budget.getBudgetId())});
        db.close();
        return true;
    }

    @Override
    public List<Budget> getAllBudget() {
        List<Budget> list_result = new ArrayList<>();
        Cursor data = getAllBudgetData();
        if(data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                list_result.add(getBudgetFromData(data));
            }while (data.moveToNext());
        }
        return list_result;
    }

    @Override
    public List<Budget> getAllBudget(long walletId) {
        List<Budget> list_result = new ArrayList<>();
        Cursor data = getAllBudgetData(walletId);
        if(data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                list_result.add(getBudgetFromData(data));
            }while (data.moveToNext());
        }
        return list_result;
    }

    @Override
    public List<Budget> getBudgetByPeriod(long walletId,DateRange dateRange) {
        List<Budget> list_result = new ArrayList<>();
        Cursor data = getAllBudgetDataByPeriod(walletId,dateRange);
        if(data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                list_result.add(getBudgetFromData(data));
            }while (data.moveToNext());
        }
        return list_result;
    }



    @Override
    public List<Budget> getBudgetByCategory(long walletId,int categoryId) {
        List<Budget> list_result = new ArrayList<>();
        Cursor data = getAllBudgetDataByCategory(walletId,categoryId);
        if(data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                list_result.add(getBudgetFromData(data));
            }while (data.moveToNext());
        }
        return list_result;
    }

    private Cursor getAllBudgetData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGET;
        return db.rawQuery(query,new String[]{});
    }

    private Cursor getAllBudgetData(long walletId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGET +
                " WHERE " + WALLET_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(walletId)});
    }
    private Cursor getAllBudgetDataByCategory(long walletId, int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGET +
                " WHERE " + WALLET_ID + " = ?"+
                " AND " + CATEGORY_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(walletId),
                String.valueOf(categoryId)});
    }
    private Cursor getAllBudgetDataByPeriod(long walletId, DateRange dateRange) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGET +
                " WHERE " + WALLET_ID + " = ?"+
                " AND " + TIME_START + " >= ?" +
                " AND " + TIME_END + " <= ?";
        return db.rawQuery(query, new String[]{String.valueOf(walletId),
                String.valueOf(dateRange.getDateFrom().toDate().getTime()),
                String.valueOf(dateRange.getDateTo().toDate().getTime())});
    }

    private Budget getBudgetFromData(Cursor data) {
        Budget budget = new Budget();
        budget.setBudgetId(data.getInt(data.getColumnIndex(BUDGET_ID)));
        long walletId = data.getLong(data.getColumnIndex(WALLET_ID));
        budget.setWallet(iWalletsDAO.getWalletById(walletId));
        int categoryId = data.getInt(data.getColumnIndex(CATEGORY_ID));
        budget.setCategory(iCategoriesDAO.getCategoryById(categoryId));
        budget.setAmount(data.getFloat(data.getColumnIndex(AMOUNT)));
        budget.setSpent(data.getFloat(data.getColumnIndex(SPENT)));
        budget.setTimeStart(new MTDate(data.getLong(data.getColumnIndex(TIME_START))));
        budget.setTimeEnd(new MTDate(data.getLong(data.getColumnIndex(TIME_END))));
        budget.setLoop(data.getInt(data.getColumnIndex(LOOP)) != 0);
        budget.setStatus(data.getString(data.getColumnIndex(STATUS)));
        return budget;
    }

    public void updateBudgetSpent(Budget budget) {
        long walletId = budget.getWallet().getWalletId();
        int categoryId = budget.getCategory().getId();
        long timeStart = budget.getTimeStart().toDate().getTime();
        long timeEnd = budget.getTimeEnd().toDate().getTime();
        String query =
                        "SELECT SUM(trading)" +
                        " FROM tbl_transactions as t" +
                        " INNER JOIN tbl_categories as c" +
                        " ON t.categoryId = c._id" +
                        " WHERE t.walletId = " + walletId +
                        " AND transaction_date >= " + timeStart + " AND transaction_date <= " + timeEnd +
                        " AND (c._id = " + categoryId +" OR c.parentId = " + categoryId + ")" ;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        float spent = 0;
        if(cursor.moveToFirst()) {
            spent = cursor.getFloat(0);
        }
        budget.setSpent(spent);
        updateBudget(budget);
    }

}
