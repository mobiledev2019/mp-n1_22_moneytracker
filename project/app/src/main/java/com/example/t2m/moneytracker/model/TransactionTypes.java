package com.example.t2m.moneytracker.model;

public enum TransactionTypes {
    EXPENSE(1), INCOME(2), DEBIT(3), LOAN(4);

    private int value;
    private TransactionTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static TransactionTypes from(int value) {
        for(TransactionTypes type : TransactionTypes.values()) {
            if(type.value == value) return type;
        }
        return null;
    }
}
