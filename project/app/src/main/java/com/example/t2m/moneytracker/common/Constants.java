package com.example.t2m.moneytracker.common;

public class Constants {
   public static final String AppPreferences = "MoneyTracker.App.SharedPreferences";
   public static final String SettingsPreferences = "MoneyTracker.Settings.SharedPreferences";

    // Date/Time
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_DATE_SHORT_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    //public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String LONG_DATE_PATTERN = "EEEE, dd MMMM yyyy";
    public static final String LONG_DATE_MEDIUM_DAY_PATTERN = "EEE, dd MMMM yyyy";

    // Database
    public static final String DEFAULT_DB_FILENAME = "money_tracker.sqlite";

    // Amount formats
    public static final String PRICE_FORMAT = "%.2f";

}
