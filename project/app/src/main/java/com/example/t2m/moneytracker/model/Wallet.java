package com.example.t2m.moneytracker.model;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class Wallet implements Serializable {

    public static final int BASIC_WALLET = 1;
    public static final int CREDIT_WALLET = 2;
    public static final int GOAL_WALLET = 3;
    public static final int LINKED_WALLET = 4;

    private long walletId;
    private int walletType;
    private String walletName;
    private float currentBalance;
    private String currencyCode;
    private String imageSrc;
    private String userUID;

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public Wallet() {
    }

    public Wallet(long walletId, String walletName, float currentBalance, String currencyCode, int walletType, String imageSrc,String userUID) {
        this.walletId = walletId;
        this.walletName = walletName;
        this.currentBalance = currentBalance;
        this.currencyCode = currencyCode;
        this.walletType = walletType;
        this.imageSrc = imageSrc;
        this.userUID = userUID;
    }

    public Wallet(WalletBuilder builder) {
        this.walletId = builder.walletId;
        this.walletName = builder.walletName;
        this.currentBalance = builder.currentBalance;
        this.currencyCode = builder.currencyCode;
        this.walletType = builder.walletType;
        this.imageSrc = builder.imageSrc;
        this.userUID = builder.userUID;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public float getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(float currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currency) {
        this.currencyCode = currency;
    }

    public int getWalletType() {
        return walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public Map<String,Object> toMap() {
        Map<String,Object> values = new HashMap<>();

        values.put("_id", walletId);
        values.put("uid",userUID);
        values.put("name",walletName);
        values.put("balance",0.0); 
        values.put("currencyCode",currencyCode);
        values.put("type",walletType);
        values.put("logo",imageSrc);
        values.put("timestamp",Timestamp.now());
        return values;
    }

    public static Wallet fronMap(Map<String,Object> data) {
        WalletBuilder builder = new WalletBuilder()
                .setWalletId((Long) data.get("_id"))
                .setUserUID((String) data.get("uid"))
                .setWalletName((String) data.get("name"))
                .setCurrentBalance(((Double) data.get("balance")).floatValue())
                .setCurrencyCode((String) data.get("currencyCode"))
                .setWalletType(((Long) data.get("type")).intValue())
                .setImageSrc((String) data.get("logo"));
        return builder.build();
    }
    public static class WalletBuilder {
        private long walletId;
        private String walletName;
        private float currentBalance;
        private String currencyCode;
        private int walletType;
        private String imageSrc;
        private String userUID;

        public WalletBuilder() {
        }

        public WalletBuilder setWalletId(long walletId) {
            this.walletId = walletId;
            return this;
        }

        public WalletBuilder setWalletName(String walletName) {
            this.walletName = walletName;
            return this;
        }

        public WalletBuilder setCurrentBalance(float currentBalance) {
            this.currentBalance = currentBalance;
            return this;
        }

        public WalletBuilder setCurrencyCode(String currency) {
            this.currencyCode = currency;
            return this;
        }

        public WalletBuilder setWalletType(int walletType) {
            this.walletType = walletType;
            return this;
        }

        public WalletBuilder setImageSrc(String imageSrc) {
            this.imageSrc = imageSrc;
            return this;
        }

        public WalletBuilder setUserUID(String userUID) {
            this.userUID = userUID;
            return this;
        }

        public Wallet build() {
            return new Wallet(this);
        }
    }
}
