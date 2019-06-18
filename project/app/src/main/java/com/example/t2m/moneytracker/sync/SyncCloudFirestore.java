package com.example.t2m.moneytracker.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.dataaccess.TransactionsDAOImpl;
import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.Wallet;
import com.example.t2m.moneytracker.utilities.TransactionsManager;
import com.example.t2m.moneytracker.utils.SharedPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class SyncCloudFirestore {

    FirebaseFirestore db;

    Context context;
    private String TAG_LOG = SyncCloudFirestore.class.getSimpleName();

    public void setSyncEvents(SyncEvents syncEvents) {
        this.syncEvents = syncEvents;
    }

    SyncEvents syncEvents;

    public SyncCloudFirestore(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }


    public boolean onSync(Wallet wallet) {
        onPullTransactions(wallet);
        onPushSync(wallet);
        return true;
    }

    public void onPullSync(Wallet wallet) {
        onPullWallet(wallet);
        onPullTransactions(wallet);
    }

    public void onPullWallet(String uid) {
        final IWalletsDAO iWalletsDAO = new WalletsDAOImpl(context);
        db.collection("users")
                .document(uid)
                .collection("wallets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG_LOG, document.getId() + " => " + document.getData());
                                Wallet wallet = Wallet.fronMap(document.getData());
                                iWalletsDAO.insertWallet(wallet);
                                //onPullTransactions(wallet);
                            }
                            if(syncEvents != null) {
                                syncEvents.onPullWalletComplete();
                            }

                        } else {
                            Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                            if(syncEvents != null) {
                                syncEvents.onPullWalletFailure();
                            }
                        }
                    }
                });
    }

    public void onPullTransactions(Wallet wallet) {

        long time_pull = SharedPrefs.getInstance().get(SharedPrefs.KEY_PULL_TIME,0);
        db.collection("users")
                .document(wallet.getUserUID())
                .collection("wallets")
                .document("wallet_" + wallet.getWalletId())
                .collection("transactions")
                .whereGreaterThan("timestamp",new Timestamp(time_pull,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG_LOG, document.getId() + " => " + document.getData());
                                Transaction transaction = Transaction.fromMap(document.getData());
                                TransactionsManager.getInstance(context).addTransaction(transaction);
                            }
                            long timestamp = Timestamp.now().getSeconds();
                            SharedPrefs.getInstance().put(SharedPrefs.KEY_PULL_TIME,timestamp);
                            if(syncEvents != null) {
                                syncEvents.onPullTransactionComplete();
                            }
                        } else {
                            Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                            if(syncEvents != null) {
                                syncEvents.onPullTransactionFailure();
                            }
                        }
                    }
                });
    }


    public void onPullWallet(Wallet wallet) {
        db.collection("users")
                .document(wallet.getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG_LOG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG_LOG, "No such document");
                            }
                        } else {
                            Log.d(TAG_LOG, "get failed with ", task.getException());
                        }
                    }
                });

    }

    public void onPushSync(Wallet wallet) {
        addWallet(wallet);
        long time_pull = SharedPrefs.getInstance().get(SharedPrefs.KEY_PUSH_TIME,0);
        List<Transaction> transactions = new TransactionsDAOImpl(context).getAllSyncTransaction(wallet.getWalletId(), time_pull);
        addTransactions(transactions,wallet);


//        WalletsManager.getInstance(context).updateTimestamp(wallet.getWalletId(),timestamp);
//        for(Transaction transaction : transactions) {
//            TransactionsManager.getInstance(context).updateTimestamp(transaction.getTransactionId(), timestamp);
//        }
    }

    public void addWallet(Wallet wallet) {
        db.collection("users").document(wallet.getUserUID())
                .collection("wallets")
                .document("wallet_"+ wallet.getWalletId())
                .set(wallet.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG_LOG,"Add wallet success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG_LOG,"Add wallet failure");
                        Log.d(TAG_LOG,e.getMessage());
                    }
                });
    }

    public void addTransactions(List<Transaction> transactions,Wallet wallet) {

        WriteBatch writeBatch = db.batch();

        CollectionReference transactionsRef = db.collection("users")
                .document(wallet.getUserUID())
                .collection("wallets")
                .document("wallet_" + wallet.getWalletId())
                .collection("transactions");

        for(Transaction transaction : transactions) {
            DocumentReference documentRef = transactionsRef.document("transaction_" + transaction.getTransactionId());
            writeBatch.set(documentRef,transaction.toMap());
        }

        writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG_LOG,"Add transactions success");
                // update push time
                long timestamp = Timestamp.now().getSeconds();
                SharedPrefs.getInstance().put(SharedPrefs.KEY_PUSH_TIME,timestamp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG_LOG,"Add transactions failure");
                Log.d(TAG_LOG,e.getMessage());
            }
        });
    }
}
