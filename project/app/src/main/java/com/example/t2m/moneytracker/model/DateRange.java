package com.example.t2m.moneytracker.model;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class DateRange implements Serializable {
    private MTDate dateFrom;
    private MTDate dateTo;

    public DateRange(MTDate dateFrom, MTDate dateTo) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public MTDate getDateFrom() {
        return dateFrom;
    }

    public MTDate getDateTo() {
        return dateTo;
    }

    @NonNull
    @Override
    public String toString() {
        String strDateFrom = "--";
        String strDateTo = "--";
        if(dateFrom != null) {
            strDateFrom = dateFrom.toString("dd/MM/yyyy");
        }
        if(dateTo!= null) {
            strDateTo = dateTo.toString("dd/MM/yyyy");
        }
        return  strDateFrom + " - " + strDateTo;
    }
}
