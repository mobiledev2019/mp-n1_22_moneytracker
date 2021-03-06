package com.example.t2m.moneytracker.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2m.moneytracker.model.Wallet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WalletsDAOImpl implements IWalletsDAO {

    /**
     * Wallet table
     * int walletId
     * String walletName;
     * float currentBalance;
     * Currency currency;
     * int walletType;
     * String imageSrc;
     * String userUID;
     */

    public static final String TABLE_WALLET_NAME = "tbl_wallets";
    public static final String COLUMN_WALLET_ID = "_id";
    public static final String COLUMN_WALLET_NAME = "name";
    public static final String COLUMN_WALLET_BALANCE = "balance";
    public static final String COLUMN_WALLET_CURRENCY = "currency";
    public static final String COLUMN_WALLET_TYPE = "walletType";
    public static final String COLUMN_WALLET_ICON = "icon";
    public static final String COLUMN_WALLET_USER_ID = "userId";

    public static final String COLUMN_TIME_STAMP = "timestamp";

    MoneyTrackerDBHelper dbHelper;
    public WalletsDAOImpl(Context context) {
        dbHelper = new MoneyTrackerDBHelper(context);
    }

    // Wallet data access

    public Wallet getWalletById(long id) {
        Cursor data = getWalletDataById(id);
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            Wallet wallet = getWalletFromData(data);
            return wallet;
        } else {
            return null;
        }
    }

    public boolean hasWallet(String userId) {
        Cursor data = getWalletDataByUser(userId);
        return data != null && data.getCount() > 0;
    }
    public List<Wallet> getAllWalletByUser(String userId) {
        Cursor data = getWalletDataByUser(userId);
        List<Wallet> list_wallet = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                Wallet wallet = getWalletFromData(data);
                list_wallet.add(wallet);
            }
            while (data.moveToNext());
        }
        return list_wallet;
    }

    public boolean insertWallet(Wallet wallet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long id = wallet.getWalletId();
        if(id <= 0)
             id= UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        values.put(COLUMN_WALLET_ID,id);
        values.put(COLUMN_WALLET_NAME, wallet.getWalletName());
        values.put(COLUMN_WALLET_BALANCE, wallet.getCurrentBalance());
        values.put(COLUMN_WALLET_CURRENCY, wallet.getCurrencyCode());
        values.put(COLUMN_WALLET_TYPE, wallet.getWalletType());
        values.put(COLUMN_WALLET_ICON, wallet.getImageSrc());
        values.put(COLUMN_WALLET_USER_ID, wallet.getUserUID());
        db.insert(TABLE_WALLET_NAME, null, values);
        db.close();
        wallet.setWalletId(id);
        updateTimeStamp(id, com.google.firebase.Timestamp.now().toDate().getTime());
        return true;
    }

    public boolean updateWallet(Wallet wallet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WALLET_NAME, wallet.getWalletName());
        values.put(COLUMN_WALLET_BALANCE, wallet.getCurrentBalance());
        values.put(COLUMN_WALLET_CURRENCY, wallet.getCurrencyCode());
        values.put(COLUMN_WALLET_TYPE, wallet.getWalletType());
        values.put(COLUMN_WALLET_ICON, wallet.getImageSrc());
        values.put(COLUMN_WALLET_USER_ID, wallet.getUserUID());
        db.update(TABLE_WALLET_NAME, values, COLUMN_WALLET_ID + " = ?", new String[]{String.valueOf(wallet.getWalletId())});
        db.close();

        updateTimeStamp(wallet.getWalletId(), com.google.firebase.Timestamp.now().toDate().getTime());
        return true;
    }

    public boolean deleteWallet(long walletId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_WALLET_NAME, COLUMN_WALLET_ID + " = ?", new String[]{String.valueOf(walletId)});
        db.close();
        return true;
    }

    private Cursor getWalletDataByUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_WALLET_NAME +
                " WHERE " + COLUMN_WALLET_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        return cursor;
    }

    private Cursor getWalletDataById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_WALLET_NAME +
                " WHERE " + COLUMN_WALLET_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        return cursor;
    }

    private Wallet getWalletFromData(Cursor data) {
        long id = data.getLong(data.getColumnIndex(COLUMN_WALLET_ID));
        String name = data.getString(data.getColumnIndex(COLUMN_WALLET_NAME));
        float balance = data.getFloat(data.getColumnIndex(COLUMN_WALLET_BALANCE));
        String currency = data.getString(data.getColumnIndex(COLUMN_WALLET_CURRENCY));
        int type = data.getInt(data.getColumnIndex(COLUMN_WALLET_TYPE));
        String icon = data.getString(data.getColumnIndex(COLUMN_WALLET_ICON));
        String userId = data.getString(data.getColumnIndex(COLUMN_WALLET_USER_ID));

        return new Wallet(id, name, balance, currency, type, icon, userId);
    }


    public void updateTimeStamp(long walletId, long timestamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "UPDATE " + TABLE_WALLET_NAME
                + " SET " + COLUMN_TIME_STAMP + " = " + timestamp
                + " WHERE " + COLUMN_WALLET_ID + " = " + walletId;

        db.execSQL(query);
        db.close();
    }
    //
}
