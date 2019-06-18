package com.example.t2m.moneytracker.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.t2m.moneytracker.common.Constants;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class MTDate implements Serializable {
    public static MTDate fromIso8601(String dateString) {
        if (dateString.length() < 28) {
            // manually handle short time-zone offset, i.e. 2016-10-22T02:36:46.000+02
            if (dateString.charAt(23) == '+' || dateString.charAt(23) == '-') {
                // append two zeroes
                dateString = dateString.concat("00");
            }

            // handle invalid format 2016-10-21T18:42:18.000Z
            if (dateString.charAt(23) == 'Z') {
                dateString = dateString.substring(0, 23);
                // append the current time zone time
                DateFormat offsetFormat = new SimpleDateFormat("Z");
                String offsetString = offsetFormat.format(new MTDate().toDate());
                dateString += offsetString;
            }
        }

        return new MTDate(dateString, Constants.ISO_8601_FORMAT);
    }

    public static Date from(String dateString, String pattern) {
        if (TextUtils.isEmpty(dateString)) return null;

        try {
            return getFormatterFor(pattern).parse(dateString);
        } catch (ParseException e) {
            Log.e("MTDATE", "parsing date string");
            return null;
        }
    }

    public static MTDate newDate() {
        MTDate result = new MTDate()
                .setTimeToBeginningOfDay();
        return result;
    }

    /*
        Instance
     */

    /**
     * The default constructor uses the current time instance by default.
     */
    public MTDate() {
        mCalendar = Calendar.getInstance();
    }

    public MTDate(Calendar calendar) {
        mCalendar = calendar;
    }

    public MTDate(Date date) {
        mCalendar = new GregorianCalendar();

        if (date != null) {
            mCalendar.setTime(date);
        }
    }

    /**
     * Creates a date/time object from an ISO date string.
     * @param isoString ISO date string
     */
    public MTDate(@NonNull String isoString) {
        String pattern = Constants.ISO_DATE_FORMAT;
        Date date = from(isoString, pattern);

        mCalendar = new GregorianCalendar();
        mCalendar.setTime(date);
    }

    public MTDate(String dateString, String pattern) {
        Date date = from(dateString, pattern);

        mCalendar = new GregorianCalendar();
        mCalendar.setTime(date);
    }

    public MTDate(int year, int month, int day) {
        mCalendar = new GregorianCalendar(year, month, day);
    }

    public MTDate(long ticks) {
        mCalendar = new GregorianCalendar();
        mCalendar.setTimeInMillis(ticks);
    }

    private Calendar mCalendar;

    public MTDate addDays(int value) {
        mCalendar.add(Calendar.DATE, value);
        return this;
    }

    public MTDate addMonth(int value) {
        mCalendar.add(Calendar.MONTH, value);

        return this;
    }

    public MTDate addYear(int value) {
        mCalendar.add(Calendar.YEAR, value);
        return this;
    }

    /**
     * Sets the calendar to the first day of the month to which the calendar points to.
     * I.e. if the calendar is 2015-08-20, this will return 2015-08-01 00:00:00.000
     * @return The first day of the month in which the Calendar is set.
     */
    public MTDate firstDayOfMonth() {
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return this;
    }

    public MTDate firstMonthOfYear() {
        mCalendar.set(Calendar.MONTH, mCalendar.getActualMinimum(Calendar.MONTH));
        return this;
    }

    public MTDate firstDayOfWeek() {
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        mCalendar.set(Calendar.DAY_OF_WEEK,mCalendar.getFirstDayOfWeek());
        return this;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public int getDayOfMonth() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfWeek() {
        return mCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public int getHour() {
        return getHourOfDay();
    }

    public int getHourOfDay() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }


    public long getMillis() {
        return mCalendar.getTimeInMillis();
    }

    public int getMinute() {
        return getMinuteOfHour();
    }

    public int getMinuteOfHour() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public int getYear() { return mCalendar.get(Calendar.YEAR); }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH);
    }

    public int getMonthOfYear() {
        return getMonth();
    }

    /**
     * Converts the date/time value to the destination time zone.
     * @param timeZone The name of the time zone. I.e. "Europe/Berlin".
     * @return Date/Time value in the destination time zone.
     */
    public MTDate inTimeZone(String timeZone) {
        // Keep the original value for conversion.
        long currentValue = mCalendar.getTimeInMillis();

        // now create the calendar in the destination time zone and convert the value.
        mCalendar = new GregorianCalendar(TimeZone.getTimeZone(timeZone));

        mCalendar.setTimeInMillis(currentValue);

        return this;
    }

    public MTDate lastDayOfMonth() {
        mCalendar.set(Calendar.DAY_OF_MONTH,
                mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return this;
    }

    public MTDate lastMonthOfYear() {
        mCalendar.set(Calendar.MONTH, mCalendar.getActualMaximum(Calendar.MONTH));
        return this;
    }

    public MTDate minusDays(int value) {
        return addDays(-value);
    }

    public MTDate minusMonths(int value) {
        return addMonth(-value);
    }

    public MTDate minusYears(int value) {
        return addYear(-value);
    }

    public MTDate plusDays(int value) {
        return addDays(value);
    }

    public MTDate plusMonths(int value) {
        return addMonth(value);
    }

    public MTDate plusWeeks(int value) {
        mCalendar.add(Calendar.WEEK_OF_YEAR, value);
        return this;
    }

    public MTDate plusYears(int value) {
        return addYear(value);
    }

    public MTDate setCalendar(Calendar calendar) {
        mCalendar = calendar;
        return this;
    }

    public MTDate setDate(int dayOfMonth) {
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return this;
    }

    public MTDate setHour(int hour) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        return this;
    }

    public MTDate setMinute(int minute) {
        mCalendar.set(Calendar.MINUTE, minute);
        return this;
    }

    public MTDate setMilisecond(int milisecond) {
        mCalendar.set(Calendar.MILLISECOND, milisecond);
        return this;
    }

    /**
     * Set the month for the current calendar.
     * @param month Month value i.e. Calendar.January. NOT ordinal, i.e. November != 11.
     * @return
     */
    public MTDate setMonth(int month) {
        mCalendar.set(Calendar.MONTH, month);
        return this;
    }

    public MTDate setSecond(int second) {
        mCalendar.set(Calendar.SECOND, second);
        return this;
    }

    public MTDate setYear(int year) {
        mCalendar.set(Calendar.YEAR, year);
        return this;
    }

//    public MTDate setNow() {
//        mCalendar = Calendar.getInstance();
//        return this;
//    }

    public MTDate setTimeToBeginningOfDay() {
        setHour(0);
        setMinute(0);
        setSecond(0);
        setMilisecond(0);
        return this;
    }

    public MTDate setTimeToEndOfDay() {
        setHour(23);
        setMinute(59);
        setMinute(59);
        setMilisecond(999);
        return this;
    }

    public MTDate setTime(Date date) {
        mCalendar.setTime(date);
        return this;
    }

    public MTDate setTimeZone(String timeZone) {
        mCalendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        return this;
    }

    public Date toDate() {
        return mCalendar.getTime();
    }

    public MTDate today() {
        return setTimeToBeginningOfDay();
    }

    public String toString(String format) {
        return getFormatterFor(format).format(toDate());
    }

    public String toIsoDateString() {
        SimpleDateFormat format = new SimpleDateFormat(Constants.ISO_DATE_FORMAT, Locale.ENGLISH);
        return format.format(toDate());
    }

    public String toIsoString() {
        SimpleDateFormat format = new SimpleDateFormat(Constants.ISO_8601_FORMAT, Locale.ENGLISH);
        return format.format(toDate());
    }

    public String toIsoDateShortTimeString() {
        SimpleDateFormat format = new SimpleDateFormat(Constants.ISO_DATE_SHORT_TIME_FORMAT, Locale.ENGLISH);
        return format.format(toDate());
    }

    /*
        Private
     */

    private static SimpleDateFormat getFormatterFor(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH);
    }
}
