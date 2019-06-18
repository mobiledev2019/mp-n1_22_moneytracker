package com.example.t2m.moneytracker.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GeneralSetting extends SettingsBase {

    public GeneralSetting(Context context) {
        super(context);
    }
    @Override
    protected SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }
}
