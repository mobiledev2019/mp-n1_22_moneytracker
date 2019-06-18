package com.example.t2m.moneytracker.sync;

public interface SyncEvents {
    void onPullWalletComplete();
    void onPullWalletFailure();
    void onPullTransactionComplete();
    void onPullTransactionFailure();

}
