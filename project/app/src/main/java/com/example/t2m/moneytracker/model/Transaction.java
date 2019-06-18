package com.example.t2m.moneytracker.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction implements Serializable {

    private Wallet wallet;
    private long transactionId;
    private Date transactionDate;
    private String transactionNote;
    private float moneyTrading;
    private String currencyCode;
    private String location;
    private Category category;
    private CategoryBean categoryBean;

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public CategoryBean getCategoryBean() {
        return categoryBean;
    }

    public void setCategoryBean(CategoryBean categoryBean) {
        this.categoryBean = categoryBean;
    }

    public void setMediaUri(String mediaUri) {
        this.mediaUri = mediaUri;
    }

    //---------------------------
    // update database v2
    private String mediaUri;

    public Transaction() {
        this.transactionDate = new MTDate().toDate();
        category = new Category();
    }

    public Transaction(TransactionBuilder builder) {
        this.transactionId = builder.transactionId;
        this.transactionDate = builder.transactionDate;
        this.transactionNote = builder.transactionNote;
        this.moneyTrading = builder.moneyTrading;
        this.currencyCode = builder.currencyCode;
        this.location = builder.location;
        this.category = builder.category;
        this.wallet = builder.wallet;
        this.mediaUri = builder.mediaUri;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionNote() {
        return transactionNote;
    }

    public void setTransactionNote(String transactionNote) {
        this.transactionNote = transactionNote;
    }

    public float getMoneyTrading() {
        return moneyTrading;
    }

    public float getMoneyTradingWithSign() {
        int type = category.getType().getValue();
        if (type == 1 || type == 3) {
            return -1 * Math.abs(moneyTrading);
        } else {
            return Math.abs(moneyTrading);
        }
    }

    public void setMoneyTrading(float moneyTrading) {
        this.moneyTrading = moneyTrading;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyId(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getMediaUri() {
        return mediaUri;
    }

    public Map<String,Object> toMap() {
        Map<String,Object> values = new HashMap<>();

        values.put("_id",transactionId);
        values.put("date",transactionDate.getTime());
        values.put("note",transactionNote);
        values.put("amount",moneyTrading);
        values.put("currencyCode",currencyCode);
        values.put("categoryId",category.getId());
        values.put("walletId",wallet.getWalletId());
        values.put("mediaUri",mediaUri);
        values.put("timestamp",Timestamp.now());

        return values;
    }

    public static Transaction fromMap(Map<String,Object> data) {
        TransactionBuilder builder = new TransactionBuilder();
        Category category = new Category();
        category.setId(((Long) data.get("categoryId")).intValue());

        Wallet wallet = new Wallet();
        wallet.setWalletId(((Long) data.get("walletId")).intValue());

        builder
                .setTransactionId((Long) data.get("_id"))
                .setTransactionDate(new Date((Long) data.get("date")))
        .setTransactionNote((String) data.get("note"))
        .setMoneyTrading(((Double) data.get("amount")).floatValue())
        .setCurrencyCode((String) data.get("currencyCode"))
        .setCategory(category)
        .setWallet(wallet )
        .setMediaUri((String) data.get("mediaUri"));


        return builder.build();
    }
    public static class TransactionBuilder {
        long transactionId;
        private Date transactionDate;
        private String transactionNote;
        private float moneyTrading;
        private String currencyCode;
        private String location;
        private Category category;
        Wallet wallet;
        // update database v2
        private String mediaUri;

        public TransactionBuilder() {
        }

        public TransactionBuilder setTransactionId(long transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionBuilder setTransactionDate(Date transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public TransactionBuilder setTransactionNote(String transactionNote) {
            this.transactionNote = transactionNote;
            return this;
        }

        public TransactionBuilder setMoneyTrading(float moneyTrading) {
            this.moneyTrading = moneyTrading;
            return this;
        }

        public TransactionBuilder setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public TransactionBuilder setLocation(String location) {
            this.location = location;
            return this;
        }

        public TransactionBuilder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public TransactionBuilder setWallet(Wallet wallet) {
            this.wallet = wallet;
            return this;
        }

        public TransactionBuilder setMediaUri(String mediaUri) {
            this.mediaUri = mediaUri;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

}
