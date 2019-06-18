package com.example.t2m.moneytracker.sync;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.t2m.moneytracker.MainActivity;
import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.account.LoginActivity;
import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;
import com.example.t2m.moneytracker.utilities.WalletsManager;
import com.example.t2m.moneytracker.wallet.AddWalletActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SyncActivity extends AppCompatActivity implements SyncEvents {

    TextView textOnSync;
    TextView textTryAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        addControls();
        addEvents();
    }

    private void addControls() {
        textOnSync = findViewById(R.id.loading_text);
        textTryAgain = findViewById(R.id.btnTryAgain);
    }

    private void addEvents() {

        textTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSync();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        onSync();

    }

    private void onSync() {
        textTryAgain.setVisibility(View.INVISIBLE);
        textOnSync.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SyncCloudFirestore syncCloudFirestore = new SyncCloudFirestore(this);
        syncCloudFirestore.setSyncEvents(this);
        syncCloudFirestore.onPullWallet(user.getUid());
    }

    @Override
    public void onPullWalletComplete() {
        IWalletsDAO iWalletsDAO = new WalletsDAOImpl(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(iWalletsDAO.hasWallet(user.getUid()) ) {
            SyncCloudFirestore syncCloudFirestore = new SyncCloudFirestore(this);
            syncCloudFirestore.setSyncEvents(this);
            syncCloudFirestore.onPullTransactions(WalletsManager.getInstance(this).getCurrentWallet());
        }
        else {
            Intent intent = new Intent(SyncActivity.this, AddWalletActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPullWalletFailure() {
        textTryAgain.setVisibility(View.VISIBLE);
        textOnSync.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPullTransactionComplete() {
        Intent intent = new Intent(SyncActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPullTransactionFailure() {
        textTryAgain.setVisibility(View.VISIBLE);
        textOnSync.setVisibility(View.INVISIBLE);
    }
}
