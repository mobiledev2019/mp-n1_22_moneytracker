package com.example.t2m.moneytracker.model;

import java.util.Date;

public class DateRange {
    private Date dateFrom;
    private Date dateTo;

    public DateRange(Date dateFrom, Date dateTo) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

}
