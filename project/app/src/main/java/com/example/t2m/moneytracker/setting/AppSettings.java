package com.example.t2m.moneytracker.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.t2m.moneytracker.common.Constants;

import java.util.prefs.Preferences;

public class AppSettings extends SettingsBase {

    public AppSettings(Context context) {
        super(context);
    }

    @Override
    protected SharedPreferences getPreferences() {
        return  PreferenceManager.getDefaultSharedPreferences(getContext());
    }
}
