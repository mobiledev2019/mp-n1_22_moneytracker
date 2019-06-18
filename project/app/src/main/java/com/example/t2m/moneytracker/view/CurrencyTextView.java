package com.example.t2m.moneytracker.view;

import android.content.Context;
import android.util.AttributeSet;

import com.example.t2m.moneytracker.utilities.CurrencyUtils;

import androidx.annotation.Nullable;

public class CurrencyTextView extends android.support.v7.widget.AppCompatTextView {

    String rawText;

    public CurrencyTextView(Context context) {
        super(context);
    }

    public CurrencyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CurrencyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        rawText = text.toString();
        String currency = text.toString();
        try {
            currency = CurrencyUtils.formatVnCurrence(currency);
        }catch (Exception e){}

        super.setText(currency, type);
    }

    @Override
    public CharSequence getText() {
        return rawText;
    }
}
