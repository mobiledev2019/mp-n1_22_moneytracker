package com.example.t2m.moneytracker.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.Wallet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionsDAOImpl implements ITransactionsDAO {

    /**
     * Transaction table
     * int transactionId
     * Date transactionDate
     * String transactionNote
     * float moneyTrading
     * Currency currency
     * String location
     * int transactionType
     * int wallet
     */

    public static final String TABLE_TRANSACTION_NAME = "tbl_transactions";
    public static final String COLUMN_TRANSACTION_ID = "_id";
    public static final String COLUMN_TRANSACTION_DATE = "transaction_date";
    public static final String COLUMN_TRANSACTION_NOTE = "note";
    public static final String COLUMN_TRANSACTION_TRADING = "trading";
    public static final String COLUMN_TRANSACTION_CURRENCY = "currency";
    public static final String COLUMN_TRANSACTION_LOCATION = "location";
    public static final String COLUMN_CATEGORY_ID_FK = "categoryId";
    public static final String COLUMN_WALLET_ID_FK = "walletId";

    public static final String COLUMN_TRANSACTION_MEDIA_URI = "media_uri";

    MoneyTrackerDBHelper dbHelper;
    ICategoriesDAO iCategoriesDAO;
    IWalletsDAO iWalletsDAO;

    public TransactionsDAOImpl(Context context) {
        dbHelper = new MoneyTrackerDBHelper(context);
        iCategoriesDAO = new CategoriesDAOImpl(context);
        iWalletsDAO = new WalletsDAOImpl(context);
    }
    // Transaction

    public boolean insertTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANSACTION_CURRENCY, transaction.getCurrencyCode());
        values.put(COLUMN_TRANSACTION_TRADING, Math.abs(transaction.getMoneyTrading()));
        values.put(COLUMN_TRANSACTION_DATE, transaction.getTransactionDate().getTime());
        values.put(COLUMN_TRANSACTION_NOTE, transaction.getTransactionNote());
        //values.put(COLUMN_TRANSACTION_LOCATION,transaction.getLocation());
        values.put(COLUMN_TRANSACTION_MEDIA_URI,transaction.getMediaUri());
        values.put(COLUMN_CATEGORY_ID_FK, transaction.getCategory().getId());
        values.put(COLUMN_WALLET_ID_FK, transaction.getWallet().getWalletId());
        int id = (int) db.insert(TABLE_TRANSACTION_NAME, COLUMN_TRANSACTION_LOCATION, values);
        transaction.setTransactionId(id);
        db.close();
        return id != -1;
    }

    public boolean updateTransaction(Transaction transaction) {
        //    COLUMN_WALLET_ID_FK                   + " INTEGER NOT NULL," +

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANSACTION_CURRENCY, transaction.getCurrencyCode());
        values.put(COLUMN_TRANSACTION_TRADING, Math.abs(transaction.getMoneyTrading()));
        values.put(COLUMN_TRANSACTION_DATE, transaction.getTransactionDate().getTime());
        values.put(COLUMN_TRANSACTION_NOTE, transaction.getTransactionNote());
        //values.put(COLUMN_TRANSACTION_LOCATION,transaction.getLocation());
        values.put(COLUMN_CATEGORY_ID_FK, transaction.getCategory().getId());
        values.put(COLUMN_WALLET_ID_FK, transaction.getWallet().getWalletId());
        db.update(TABLE_TRANSACTION_NAME,values,COLUMN_TRANSACTION_ID + " = ?" ,new String[]{String.valueOf(transaction.getTransactionId())});
        db.close();
        return true;
    }

    @Override
    public boolean deleteTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_TRANSACTION_NAME,COLUMN_TRANSACTION_ID + " = ?",new String[]{String.valueOf(transaction.getTransactionId())});
        return true;
    }

    public Transaction getTransactionById(int transactionId) {
        Cursor data = getTransactionDataById(transactionId);
        if(data != null && data.getCount() > 0) {
            data.moveToFirst();
            return getTransactionFromData(data);
        }
        return null;
    }

    public List<Transaction> getAllTransactionByWalletId(int walletId) {
        Cursor data = getAllTransactionDataByWalletId(walletId);
        List<Transaction> list_result = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Transaction transaction = getTransactionFromData(data);
                list_result.add(transaction);
            }
            while (data.moveToNext());
        }
        return list_result;
    }

    @Override
    public List<Transaction> getAllTransactionByPeriod(int walletId, DateRange dateRange) {
        Cursor data = getAllTransactionDataByWalletId(walletId,dateRange);
        List<Transaction> list_result = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Transaction transaction = getTransactionFromData(data);
                list_result.add(transaction);
            }
            while (data.moveToNext());
        }
        return list_result;
    }



    private Cursor getAllTransactionDataByWalletId(int walletId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME +
                " WHERE " + COLUMN_WALLET_ID_FK + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(walletId)});
    }
    private Cursor getAllTransactionDataByWalletId(int walletId, DateRange dateRange) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME +
                " WHERE " + COLUMN_WALLET_ID_FK + " = ?" +
                " AND " + COLUMN_TRANSACTION_DATE + " >= ?" +
                " AND " + COLUMN_TRANSACTION_DATE + " <= ?";
        Cursor cursor = db.rawQuery(query,
                new String[]{
                        String.valueOf(walletId),
                        String.valueOf(dateRange.getDateFrom().getTime()),
                        String.valueOf(dateRange.getDateTo().getTime())});
        return cursor;
    }
    public List<Transaction> getAllTransaction() {
        Cursor data = getAllTransactionData();
        List<Transaction> list_result = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Transaction transaction = getTransactionFromData(data);
                list_result.add(transaction);
            }
            while (data.moveToNext());
        }
        return list_result;
    }

    private Cursor getAllTransactionData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME;
        return db.rawQuery(query,null);
    }

    private Transaction getTransactionFromData(Cursor data) {
        Transaction.TransactionBuilder builder = new Transaction.TransactionBuilder();
        //String location = data.getString(data.getColumnIndex(COLUMN_TRANSACTION_NOTE));
        int typeId = data.getInt(data.getColumnIndex(COLUMN_CATEGORY_ID_FK));
        int walletId = data.getInt(data.getColumnIndex(COLUMN_WALLET_ID_FK));
        Category category = iCategoriesDAO.getCategoryById(typeId);
        Wallet wallet = iWalletsDAO.getWalletById(walletId);
        String mediaUri = null;
        if(!data.isNull(data.getColumnIndex(COLUMN_TRANSACTION_MEDIA_URI))) {
            mediaUri = data.getString(data.getColumnIndex(COLUMN_TRANSACTION_MEDIA_URI));
        }

        builder
                .setTransactionId(data.getInt(data.getColumnIndex(COLUMN_TRANSACTION_ID)))
                .setMoneyTrading(data.getFloat(data.getColumnIndex(COLUMN_TRANSACTION_TRADING)))
                .setTransactionDate(new Date(data.getLong(data.getColumnIndex(COLUMN_TRANSACTION_DATE))))
                .setTransactionNote(data.getString(data.getColumnIndex(COLUMN_TRANSACTION_NOTE)))
                .setCurrencyCode(data.getString(data.getColumnIndex(COLUMN_TRANSACTION_CURRENCY)))
                .setCategory(category)
                .setMediaUri(mediaUri)
                .setWallet(wallet);
        return builder.build();
    }

    private Cursor getTransactionDataById(int transactionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME +
                " WHERE " + COLUMN_TRANSACTION_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(transactionId)});
    }

}
