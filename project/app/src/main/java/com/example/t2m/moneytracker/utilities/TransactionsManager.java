package com.example.t2m.moneytracker.utilities;


import android.content.Context;

import com.example.t2m.moneytracker.dataaccess.BudgetDAOImpl;
import com.example.t2m.moneytracker.dataaccess.CategoriesDAOImpl;
import com.example.t2m.moneytracker.dataaccess.IBudgetDAO;
import com.example.t2m.moneytracker.dataaccess.ICategoriesDAO;
import com.example.t2m.moneytracker.dataaccess.ITransactionsDAO;
import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.dataaccess.MoneyTrackerDBHelper;
import com.example.t2m.moneytracker.dataaccess.TransactionsDAOImpl;
import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;
import com.example.t2m.moneytracker.model.Budget;
import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.TransactionTypes;
import com.example.t2m.moneytracker.model.Wallet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionsManager {

    private Wallet mWallet;
    private List<Transaction> transactions;
    private ITransactionsDAO iTransactionsDAO;
    private IWalletsDAO iWalletsDAO;
    private IBudgetDAO iBudgetDAO;

    Context mContext;
    private static final TransactionsManager ourInstance = new TransactionsManager();

    public static TransactionsManager getInstance(Context context) {
        ourInstance.setContext(context);
        return ourInstance;
    }

    private TransactionsManager() {
        mWallet = null;
        transactions = new ArrayList<>();
    }
    private void setContext(Context context) {
        if(context != null) {
            this.mContext = context;
            iTransactionsDAO = new TransactionsDAOImpl(context);
        }
    }

    public void setCurrentWallet(Wallet wallet) {
        this.mWallet = wallet;
        getAllTransactions(wallet);
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    public List<Transaction> getAllTransactionByTime(DateRange dateRange) {
        List<Transaction> filterTransaction = new ArrayList<>();

        DateUtils dateUtils = new DateUtils();
        for(Transaction tran : transactions) {
            Date date = tran.getTransactionDate();
            if(dateUtils.isDateRangeContainDate(dateRange,date)) {
                filterTransaction.add(tran);
            }
        }
        return filterTransaction;

    }

    public boolean addTransaction(Transaction transaction) {
        if(transaction == null) return  false;
        transactions.add(transaction);
        iTransactionsDAO.insertTransaction(transaction);

        Transaction tranInfo = iTransactionsDAO.getTransactionById(transaction.getTransactionId());
        if(tranInfo == null) return false;
        // update wallet
        tranInfo.getWallet().setCurrentBalance(tranInfo.getWallet().getCurrentBalance() + tranInfo.getMoneyTradingWithSign());
        WalletsManager.getInstance(mContext).updateWallet(tranInfo.getWallet());
        if(tranInfo.getCategory().getType() == TransactionTypes.EXPENSE || tranInfo.getCategory().getType() == TransactionTypes.DEBIT) {
            BudgetsManager.getInstance(mContext).updateBudget(tranInfo, 1,true);
        }
        return true;
    }
    public boolean updateTransaction(Transaction transaction, Transaction oldTransaction) {
        if(transaction == null) return  false;
        iTransactionsDAO.updateTransaction(transaction);

        float walletBalance = transaction.getWallet().getCurrentBalance() - oldTransaction.getMoneyTradingWithSign() + transaction.getMoneyTradingWithSign();
        transaction.getWallet().setCurrentBalance(walletBalance);
        WalletsManager.getInstance(mContext).updateWallet(transaction.getWallet());
        if(oldTransaction.getCategory().getType() == TransactionTypes.EXPENSE || oldTransaction.getCategory().getType() == TransactionTypes.DEBIT) {
            BudgetsManager.getInstance(mContext).updateBudget(oldTransaction, -1,false);
        }
        if(transaction.getCategory().getType() == TransactionTypes.EXPENSE || transaction.getCategory().getType() == TransactionTypes.DEBIT) {
            BudgetsManager.getInstance(mContext).updateBudget(transaction, 1,true);
        }
        return true;
    }

    public boolean deleteTransaction(Transaction transaction) {
        if(transaction == null) return  false;
        iTransactionsDAO.deleteTransaction(transaction);
        transaction.getWallet().setCurrentBalance(transaction.getWallet().getCurrentBalance() - transaction.getMoneyTradingWithSign());
        WalletsManager.getInstance(mContext).updateWallet(transaction.getWallet());
        if(transaction.getCategory().getType() == TransactionTypes.EXPENSE || transaction.getCategory().getType() == TransactionTypes.DEBIT) {
            BudgetsManager.getInstance(mContext).updateBudget(transaction, -1,false);
        }
        return true;
    }

    private void getAllTransactions(Wallet wallet) {
        transactions.clear();
        if(wallet != null){
            transactions = iTransactionsDAO.getAllTransactionByWalletId(wallet.getWalletId());
        }
    }

    public void updateTimestamp(int transactionId, long timestamp) {
        iTransactionsDAO.updateTimeStamp(transactionId,timestamp);
    }
}
