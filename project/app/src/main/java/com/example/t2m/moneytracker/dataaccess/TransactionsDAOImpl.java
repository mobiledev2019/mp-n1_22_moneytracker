package com.example.t2m.moneytracker.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.Wallet;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private static final String COLUMN_TIME_STAMP = "timestamp";

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
        long id = transaction.getTransactionId();
        if(id <= 0)
            id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        values.put(COLUMN_TRANSACTION_ID,id);
        values.put(COLUMN_TRANSACTION_CURRENCY, transaction.getCurrencyCode());
        values.put(COLUMN_TRANSACTION_TRADING, Math.abs(transaction.getMoneyTrading()));
        values.put(COLUMN_TRANSACTION_DATE, transaction.getTransactionDate().getTime());
        values.put(COLUMN_TRANSACTION_NOTE, transaction.getTransactionNote());
        //values.put(COLUMN_TRANSACTION_LOCATION,transaction.getLocation());
        values.put(COLUMN_TRANSACTION_MEDIA_URI,transaction.getMediaUri());
        values.put(COLUMN_CATEGORY_ID_FK, transaction.getCategory().getId());
        values.put(COLUMN_WALLET_ID_FK, transaction.getWallet().getWalletId());
        db.insert(TABLE_TRANSACTION_NAME, COLUMN_TRANSACTION_LOCATION, values);
        transaction.setTransactionId(id);
        db.close();

        updateTimeStamp(id, com.google.firebase.Timestamp.now().toDate().getTime());
        return true;
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
        values.put(COLUMN_TRANSACTION_MEDIA_URI,transaction.getMediaUri());
        values.put(COLUMN_CATEGORY_ID_FK, transaction.getCategory().getId());
        values.put(COLUMN_WALLET_ID_FK, transaction.getWallet().getWalletId());
        db.update(TABLE_TRANSACTION_NAME,values,COLUMN_TRANSACTION_ID + " = ?" ,new String[]{String.valueOf(transaction.getTransactionId())});
        db.close();
        updateTimeStamp(transaction.getTransactionId(), com.google.firebase.Timestamp.now().toDate().getTime());
        return true;
    }

    @Override
    public boolean deleteTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_TRANSACTION_NAME,COLUMN_TRANSACTION_ID + " = ?",new String[]{String.valueOf(transaction.getTransactionId())});

        return true;
    }

    public Transaction getTransactionById(long transactionId) {
        Cursor data = getTransactionDataById(transactionId);
        if(data != null && data.getCount() > 0) {
            data.moveToFirst();
            return getTransactionFromData(data);
        }
        return null;
    }

    public List<Transaction> getAllTransactionByWalletId(long walletId) {
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
    public List<Transaction> getAllTransactionByPeriod(long walletId, DateRange dateRange) {
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



    private Cursor getAllTransactionDataByWalletId(long walletId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME +
                " WHERE " + COLUMN_WALLET_ID_FK + " = ?" +
                " ORDER BY " + COLUMN_TRANSACTION_DATE;
        return db.rawQuery(query, new String[]{String.valueOf(walletId)});
    }
    private Cursor getAllTransactionDataByWalletId(long walletId, DateRange dateRange) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME +
                " WHERE " + COLUMN_WALLET_ID_FK + " = ?" +
                " AND " + COLUMN_TRANSACTION_DATE + " >= ?" +
                " AND " + COLUMN_TRANSACTION_DATE + " <= ?";
        Cursor cursor = db.rawQuery(query,
                new String[]{
                        String.valueOf(walletId),
                        String.valueOf(dateRange.getDateFrom().toDate().getTime()),
                        String.valueOf(dateRange.getDateTo().toDate().getTime())});
        return cursor;
    }
    public List<Transaction> getAllTransactionDataByDate(long milisStart, long milisEnd){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME +
                " WHERE " + COLUMN_TRANSACTION_DATE + " >= ?" +
                " AND " + COLUMN_TRANSACTION_DATE + " <= ?" ;
        Cursor cursor = db.rawQuery(query,new String[]{
                String.valueOf(milisStart),
                String.valueOf(milisEnd)});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Transaction transaction = getTransactionFromData(cursor);
                list.add(transaction);
            }
            while (cursor.moveToNext());
        }
        return list;
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
        long walletId = data.getLong(data.getColumnIndex(COLUMN_WALLET_ID_FK));
        Category category = iCategoriesDAO.getCategoryById(typeId);
        Wallet wallet = iWalletsDAO.getWalletById(walletId);
        String mediaUri = null;
        if(!data.isNull(data.getColumnIndex(COLUMN_TRANSACTION_MEDIA_URI))) {
            mediaUri = data.getString(data.getColumnIndex(COLUMN_TRANSACTION_MEDIA_URI));
        }

        builder
                .setTransactionId(data.getLong(data.getColumnIndex(COLUMN_TRANSACTION_ID)))
                .setMoneyTrading(data.getFloat(data.getColumnIndex(COLUMN_TRANSACTION_TRADING)))
                .setTransactionDate(new Date(data.getLong(data.getColumnIndex(COLUMN_TRANSACTION_DATE))))
                .setTransactionNote(data.getString(data.getColumnIndex(COLUMN_TRANSACTION_NOTE)))
                .setCurrencyCode(data.getString(data.getColumnIndex(COLUMN_TRANSACTION_CURRENCY)))
                .setCategory(category)
                .setMediaUri(mediaUri)
                .setWallet(wallet);
        return builder.build();
    }

    private Cursor getTransactionDataById(long transactionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME +
                " WHERE " + COLUMN_TRANSACTION_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(transactionId)});
    }

    public List<Transaction> getStatisticalByCategory(int categoryId , int type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT "
                + " tblt.trading AS trading "
                + " ,tblc._id AS categoryId "
                + " , tblc.type AS type "
                + " FROM tbl_transactions tblt "
                + " INNER JOIN tbl_categories tblc ON tblc._id = tblt.categoryId "
                + " WHERE tblt.categoryId = " + categoryId
                + " AND tblc.type = "+ type;
        List<Transaction> list = new ArrayList<Transaction>();
        Cursor cursor = db.rawQuery(sql,null);

        while(cursor.moveToNext()) {
            Category cat = new Category();
            Float column1 = cursor.getFloat(cursor.getColumnIndex("trading"));
            int column2 = cursor.getInt(cursor.getColumnIndex("categoryId"));
            int column3 = cursor.getInt(cursor.getColumnIndex("type"));
            Transaction data = new Transaction();
            data.setMoneyTrading(column1);
            cat.setId(column2);
            cat.setType(column3);
            data.setCategory(cat);
            list.add(data);
        }
        return list;
    }
    public List<Transaction> getAllTransactionDataByType(int type){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT "
                + " tblt.trading AS trading "
                + " ,tblc._id AS categoryId "
                + " , tblc.type AS type "
                + " , tblt._id AS _id"
                + " , tblt.transaction_date AS transaction_date"
                + " , tblt.note AS note"
                + " , tblt.currency AS currency"
                + " , tblt.location AS location"
                + " , tblt.walletId AS walletId"
                + " , tblt.media_uri AS media_uri"
                + " FROM tbl_transactions tblt "
                + " INNER JOIN tbl_categories tblc ON tblc._id = tblt.categoryId "
                + " WHERE tblc.type = "+ type;
        List<Transaction> list_result = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql,null);


        if (cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            do {
                Transaction transaction = getTransactionFromData(cursor);
                list_result.add(transaction);
            }while (cursor.moveToNext());
        }
        return list_result;
    }

    public List<Transaction> getStatisticalByCategoryInRange(long wallet_id ,int categoryId , DateRange dateRange) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT "
                + " tblt.trading AS trading "
                + " ,tblc._id AS categoryId "
                + " ,tblc.type AS type "
                + " ,tblt.transaction_date as t_date"
                + " FROM tbl_transactions tblt "
                + " INNER JOIN tbl_categories tblc ON tblc._id = tblt.categoryId "
                + " WHERE (tblt.categoryId = " + categoryId + " OR tblc.parentId = " + categoryId +" )"
                + " AND tblt.walletId = "+ wallet_id
                + " AND tblt.transaction_date >= " + dateRange.getDateFrom().getMillis()
                + " AND tblt.transaction_date <= " + dateRange.getDateTo().getMillis();
        List<Transaction> list = new ArrayList<Transaction>();
        Cursor cursor = db.rawQuery(sql,null);

        while(cursor.moveToNext()) {
            Category cat = new Category();
            Float column1 = cursor.getFloat(cursor.getColumnIndex("trading"));
            int column2 = cursor.getInt(cursor.getColumnIndex("categoryId"));
            int column3 = cursor.getInt(cursor.getColumnIndex("type"));
            long column4 = cursor.getLong(cursor.getColumnIndex("t_date"));
            Transaction data = new Transaction();
            data.setMoneyTrading(column1);
            cat.setId(column2);
            cat.setType(column3);
            data.setCategory(cat);
            data.setTransactionDate(new Date(column4));
            list.add(data);
        }
        return list;
    }


    public List<Transaction> getAllTransactionByCategoryInRange(long wallet_id ,int categoryId , DateRange dateRange) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT *"
                + " FROM tbl_transactions tblt "
                + " INNER JOIN tbl_categories tblc ON tblc._id = tblt.categoryId "
                + " WHERE (tblt.categoryId = " + categoryId + " OR tblc.parentId = " + categoryId +" )"
                + " AND tblt.walletId = "+ wallet_id
                + " AND tblt.transaction_date >= " + dateRange.getDateFrom().getMillis()
                + " AND tblt.transaction_date <= " + dateRange.getDateTo().getMillis();
        List<Transaction> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql,null);

        while(cursor.moveToNext()) {
            list.add(getTransactionFromData(cursor));
        }
        return list;
    }

    public void updateTimeStamp(long transactionId, long timestamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "UPDATE " + TABLE_TRANSACTION_NAME
                + " SET " + COLUMN_TIME_STAMP + " = " + timestamp
                + " WHERE " + COLUMN_TRANSACTION_ID + " = " + transactionId;

        db.execSQL(query);
        db.close();
    }

    @Override
    public List<Transaction> getAllSyncTransaction(long walletId, long timestamp) {
        Cursor data = getAllSyncTransactionData(walletId,timestamp);
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

    private Cursor getAllSyncTransactionData(long walletId, long timestamp) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSACTION_NAME +
                " WHERE " + COLUMN_WALLET_ID_FK + " = ?" +
                " AND (" + COLUMN_TIME_STAMP + " > ?" + " OR " + COLUMN_TIME_STAMP + " IS NULL)";
        return db.rawQuery(query, new String[]{String.valueOf(walletId),String.valueOf(timestamp)});
    }
}
