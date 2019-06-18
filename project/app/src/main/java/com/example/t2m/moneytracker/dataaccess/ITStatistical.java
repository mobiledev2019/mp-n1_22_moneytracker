package com.example.t2m.moneytracker.dataaccess;

import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.Statistical;
import com.example.t2m.moneytracker.model.Transaction;

import java.util.List;

public interface ITStatistical {

    public List<Statistical> getAllStatistical();
    public List<Statistical> getAllStatisticalByWalletId(int walletId);
    public List<Statistical> getAllStatisticalByPeriod(int walletId,DateRange dateRange);
}
