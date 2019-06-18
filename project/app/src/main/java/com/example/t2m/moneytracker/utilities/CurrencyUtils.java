package com.example.t2m.moneytracker.utilities;

import android.content.Context;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyUtils {

    public static class CurrencySymbols {
        public static String NONE = "";
        public static String MALAYSIA = "RM";
        public static String INDONESIA = "Rp";
        public static String SRILANKA = "Rs";
        public static String USA = "$";
        public static String UK = "£";
        public static String INDIA = "₹";
        public static String PHILIPPINES = "₱";
        public static String PAKISTAN = "₨";
        public static String VIETNAMDONG = "đ";
    }

    //properties
    private String Separator = ",";
    private Boolean Spacing = false;
    private Boolean Delimiter = false;
    private Boolean Decimals = true;

    /**
     * Get price with VietNam currency
     *
     * @param price
     */
    public static String formatVnCurrence(String price) {

        NumberFormat format = new DecimalFormat("#,##0.00");
        format.setCurrency(Currency.getInstance(Locale.US));

        price = (!TextUtils.isEmpty(price)) ? price : "0";
        price = price.trim();
        price = format.format(Double.parseDouble(price));
        price = price.replaceAll(",", "\\.");

        if (price.endsWith(".00")) {
            int centsIndex = price.lastIndexOf(".00");
            if (centsIndex != -1) {
                price = price.substring(0, centsIndex);
            }
        }
        price = String.format("%s đ", price);
        return price;
    }

    public String getSeparator() {
        return Separator;
    }

    public void setSeparator(String separator) {
        Separator = separator;
    }

    public Boolean getSpacing() {
        return Spacing;
    }

    public void setSpacing(Boolean spacing) {
        Spacing = spacing;
    }

    public Boolean getDelimiter() {
        return Delimiter;
    }

    public void setDelimiter(Boolean delimiter) {
        Delimiter = delimiter;
    }

    public Boolean getDecimals() {
        return Decimals;
    }

    public void setDecimals(Boolean decimals) {
        Decimals = decimals;
    }

}
