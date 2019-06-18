package com.example.t2m.moneytracker.model;

import java.io.Serializable;

public class Budget implements Serializable {
    private int budgetId;
    private Wallet wallet;
    private Category category;
    private float amount;
    private float spent;
    private MTDate timeStart;
    private MTDate timeEnd;
    private boolean isLoop;
    private String status;


    public Budget() {
    }

    public Budget(int budgetId, Wallet wallet, Category category, float amount, MTDate timeStart, MTDate timeEnd, boolean isLoop, String status) {
        this.budgetId = budgetId;
        this.wallet = wallet;
        this.category = category;
        this.amount = amount;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.isLoop = isLoop;
        this.status = status;
    }

    public Budget(Wallet wallet, Category category, float budgetAmount, MTDate timeStart, MTDate timeEnd, boolean isLoop, String status) {
        this.wallet = wallet;
        this.category = category;
        this.amount = budgetAmount;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.isLoop = isLoop;
        this.status = status;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }


    public float getSpent() {
        return spent;
    }

    public void setSpent(float spent) {
        this.spent = spent;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public MTDate getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(MTDate timeStart) {
        this.timeStart = timeStart;
    }

    public MTDate getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(MTDate timeEnd) {
        this.timeEnd = timeEnd;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addSpent(float additionAmount) {
        spent += additionAmount;
    }
}
