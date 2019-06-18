package com.example.t2m.moneytracker.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.widget.DatePicker;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.MTDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private Locale _locale = Locale.ENGLISH;

    public DateUtils() {

    }

    public DateUtils(Locale locale) {
        _locale = locale;
    }

    public Date from(int year, int monthOfYear, int dayOfMonth) {
        return new MTDate(year, monthOfYear, dayOfMonth).toDate();
    }

    public Date from(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        return new MTDate(year, month, day).toDate();
    }

    public String format(Date date, String format) {
        return getFormatterFor(format).format(date);
    }
    public DateRange getDateRangeForPeriod(Context context, int resourceId) {
        String value = context.getString(resourceId);
        return getDateRangeForPeriod(context, value);
    }

    /**
     * Creates a date range from the period name. Used when selecting a date range from the
     * localized menus.
     * @param period Period name in local language.
     * @return Date Range object.
     */
    public DateRange getDateRangeForPeriod(Context context, String period) {
        if (TextUtils.isEmpty(period)) return null;

        MTDate dateFrom = new MTDate();
        MTDate dateTo = new MTDate();

        // we ignore the minutes at the moment, since the field in the db only stores the date value.

        if (period.equalsIgnoreCase(context.getString(R.string.all_transaction)) ||
                period.equalsIgnoreCase(context.getString(R.string.all_time))) {
            // All transactions.
            dateFrom = dateFrom.today().minusYears(1000);
            dateTo = dateTo.today().plusYears(1000);
        } else if (period.equalsIgnoreCase(context.getString(R.string.today))) {
            dateFrom = dateFrom.today();
            dateTo = dateTo.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last7days))) {
            dateFrom = dateFrom.today().minusDays(7);
            dateTo = dateTo.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last15days))) {
            dateFrom = dateFrom.today().minusDays(14);
            dateTo = dateTo.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.current_month))) {
            dateFrom = dateFrom.today().firstDayOfMonth();
            dateTo = dateTo.today().lastDayOfMonth();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last30days))) {
            dateFrom = dateFrom.today().minusDays(30);
            dateTo = dateTo.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last3months))) {
            dateFrom = dateFrom.today().minusMonths(3)
                    .firstDayOfMonth();
            dateTo = dateTo.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last6months))) {
            dateFrom = dateFrom.today().minusMonths(6)
                    .firstDayOfMonth();
            dateTo = dateTo.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.current_year))) {
            dateFrom = dateFrom.today().firstMonthOfYear()
                    .firstDayOfMonth();
            dateTo = dateTo.today().lastMonthOfYear()
                    .lastDayOfMonth();
        } else if (period.equalsIgnoreCase(context.getString(R.string.future_transactions))) {
            // Future transactions
            dateFrom = dateFrom.today().plusDays(1);
            dateTo = dateTo.today().plusYears(1000);
        } else if(period.equalsIgnoreCase(context.getString(R.string.thisweek))) {
            dateFrom = dateFrom.today().firstDayOfWeek().setTimeToBeginningOfDay();
            dateTo = dateTo.today().firstDayOfWeek().plusDays(6).setTimeToEndOfDay();
        } else if(period.equalsIgnoreCase(context.getString(R.string.thismonth))) {
            dateFrom = dateFrom.today().firstDayOfMonth();
            dateTo = dateTo.today().lastDayOfMonth();
        } else if(period.equalsIgnoreCase(context.getString(R.string.thisquarter))) {
            DateRange quarter = getDateRangeQuarter(dateFrom.today());
            dateFrom = quarter.getDateFrom();
            dateTo = quarter.getDateTo();
        } else if(period.equalsIgnoreCase(context.getString(R.string.thisyear))) {
            dateFrom = dateFrom.today().firstMonthOfYear()
                    .firstDayOfMonth();
            dateTo = dateTo.today().lastMonthOfYear()
                    .lastDayOfMonth();
        } else if(period.equalsIgnoreCase(context.getString(R.string.nextweek))) {
            dateFrom = dateFrom.today().plusDays(7).firstDayOfWeek();
            dateTo = dateTo.today().plusDays(7).firstDayOfWeek().plusDays(6);
        } else if(period.equalsIgnoreCase(context.getString(R.string.nextmonth))) {
            dateFrom = dateFrom.today().plusMonths(1).firstDayOfMonth();
            dateTo = dateTo.today().plusMonths(1).lastDayOfMonth();
        } else if(period.equalsIgnoreCase(context.getString(R.string.nextquarter))) {
            DateRange quarter = getDateRangeQuarter(dateFrom.today().addMonth(3));
            dateFrom = quarter.getDateFrom();
            dateTo = quarter.getDateTo();
        } else if(period.equalsIgnoreCase(context.getString(R.string.nextyear))) {
            dateFrom = dateFrom.today().addYear(1).firstMonthOfYear()
                    .firstDayOfMonth();
            dateTo = dateTo.today().addYear(1).lastMonthOfYear()
                    .lastDayOfMonth();
        }
        else {
            dateFrom = null;
            dateTo = null;
        }

        DateRange result = new DateRange(dateFrom, dateTo);
        return result;
    }

    public DateRange getDateRangeQuarter(MTDate time) {
        int month = time.getMonth();
        MTDate dateFrom = new MTDate();
        MTDate dateTo = new MTDate();

        // Tháng 1 - 3
        if(month >= 0 && month < 3 ) {
            dateFrom = dateFrom.setMonth(Calendar.JANUARY).firstDayOfMonth();
            dateTo = dateTo.setMonth(Calendar.MARCH).lastDayOfMonth();
        }
        // Tháng 4 - 6
        else if(month >= 3 && month < 6) {
            dateFrom = dateFrom.setMonth(Calendar.APRIL).firstDayOfMonth();
            dateTo = dateTo.setMonth(Calendar.JUNE).lastDayOfMonth();
        }
        // Tháng 7 - 9
        else if(month >= 6 && month < 9) {
            dateFrom = dateFrom.setMonth(Calendar.JULY).firstDayOfMonth();
            dateTo = dateTo.setMonth(Calendar.SEPTEMBER).lastDayOfMonth();
        }
        // Tháng 10 - 12
        else {
            dateFrom = dateFrom.setMonth(Calendar.OCTOBER).firstDayOfMonth();
            dateTo = dateTo.setMonth(Calendar.DECEMBER).lastDayOfMonth();
        }

        return new DateRange(dateFrom,dateTo);
    }

    public String getDateStringFrom(Date dateTime, String pattern) {
        SimpleDateFormat format = getFormatterFor(pattern);
//        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
//        String result = format.print(dateTime);
        return format.format(dateTime);
//        return result;
    }

    public int getFirstDayOfWeek() {
        Calendar cal = Calendar.getInstance(_locale);
        return cal.getFirstDayOfWeek();
    }


    public Date now() {
        return new MTDate().toDate();
    }

    public void setDatePicker(Date date, DatePicker datePicker) {
        MTDate dateTime = new MTDate(date);
        datePicker.updateDate(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
    }

    /*
        Private
     */

    private SimpleDateFormat getFormatterFor(String format) {
        return new SimpleDateFormat(format, _locale);
    }

    // Kiểm tra ngày có nằm trong khoảng thời gian hay không
    public boolean isDateRangeContainDate(DateRange dateRange,MTDate date) {
        MTDate dateC = date;
        MTDate dateF = dateRange.getDateFrom();
        MTDate dateT = dateRange.getDateTo();
        return dateC.getCalendar().compareTo(dateF.getCalendar()) >= 0 && dateC.getCalendar().compareTo(dateT.getCalendar()) <= 0;
    }

    // Kiểm tra ngày có nằm trong khoảng thời gian hay không
    public boolean isDateRangeContainDate(DateRange dateRange,Date date) {
        MTDate dateC = new MTDate(date);
        MTDate dateF = dateRange.getDateFrom();
        MTDate dateT = dateRange.getDateTo();
        return dateC.getCalendar().compareTo(dateF.getCalendar()) >= 0 && dateC.getCalendar().compareTo(dateT.getCalendar()) <= 0;
    }

    public boolean isFutureDate(Date dateCompare,Date dateFuture) {
        MTDate dateC = new MTDate(dateCompare);
        MTDate dateT = new MTDate(dateFuture);
        return dateC.getCalendar().compareTo(dateT.getCalendar()) < 0;
    }

    public boolean isFutureDate(MTDate dateCompare,MTDate dateFuture) {
        return dateCompare.getCalendar().compareTo(dateFuture.getCalendar()) < 0;
    }
}
