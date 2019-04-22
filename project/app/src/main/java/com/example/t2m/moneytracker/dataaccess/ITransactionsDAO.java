package com.example.t2m.moneytracker.dataaccess;

import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.Transaction;

import java.time.Period;
import java.util.List;

public interface ITransactionsDAO {
    public boolean insertTransaction(Transaction transaction);
    public boolean updateTransaction(Transaction transaction);
    public boolean deleteTransaction(Transaction transaction);

    public Transaction getTransactionById(int transactionId);

    public List<Transaction> getAllTransaction();
    public List<Transaction> getAllTransactionByWalletId(int walletId);
    public List<Transaction> getAllTransactionByPeriod(int walletId,DateRange dateRange);
}
