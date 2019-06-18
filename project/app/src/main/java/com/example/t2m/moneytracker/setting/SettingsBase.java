package com.example.t2m.moneytracker.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Base class for preferences sections.
 */
abstract class SettingsBase {

    SettingsBase(Context context) {
        if (context.getApplicationContext() != null) {
            this.mContext = context.getApplicationContext();
        } else {
            this.mContext = context;
        }
    }

    // Context for preferences is the Application Context.
    private Context mContext;

    // common

    protected Context getContext() {
        return mContext;
    }

    /**
     * Override to set the preferences to use. For shared, use
     * PreferenceManager.getDefaultSharedPreferences(mContext);
     * @return preferences (either shared or private).
     */
    protected abstract SharedPreferences getPreferences();

    protected String getSettingsKey(Integer settingKeyConstant) {
        try {
            return getContext().getString(settingKeyConstant, "");
        } catch (Exception e) {
            Log.e(SettingsBase.class.getSimpleName(), "error getting string for resource " + settingKeyConstant);
        }
        return "";
    }

    // String

    public String get(Integer settingKey, String defaultValue) {
        String key = getSettingsKey(settingKey);
        return get(key, defaultValue);
    }

    public String get(String key, String defaultValue) {
        try {
            return getPreferences().getString(key, defaultValue);
        } catch (Exception e) {
            Log.e(SettingsBase.class.getSimpleName(), "reading string preference: " + key);

            return defaultValue;
        }
    }

    /**
     * Save string value to preferences.
     */
    public void set(String key, String value) {
        getPreferences().edit()
                .putString(key, value)
                .apply();
    }

    public void set(Integer settingsKey, String value) {
        getPreferences().edit()
                .putString(getSettingsKey(settingsKey), value)
                .apply();
    }

    // Boolean

    public boolean get(String key, boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    public boolean get(Integer settingKey, boolean defaultValue) {
        String key = getSettingsKey(settingKey);
        return getBooleanSetting(key, defaultValue);
    }

    protected boolean getBooleanSetting(String settingKey) {
        return getBooleanSetting(settingKey, false);
    }

    protected boolean getBooleanSetting(String settingKey, boolean defaultValue) {
        // This is the main method that actually fetches the value.
        return getPreferences().getBoolean(settingKey, defaultValue);
    }

    public void set(String key, boolean value) {
        getPreferences().edit()
                .putBoolean(key, value)
                .apply();
    }

    public void set(Integer key, boolean value) {
        String stringKey = getSettingsKey(key);
        this.set(stringKey, value);
    }

    // Integer

    public int get(String key, int defaultValue) {
        return getPreferences().getInt(key, defaultValue);
    }

    /**
     * Retrieve setting by passing the R.string.key
     * @param settingKey R.string.key_name
     * @param defaultValue The default value to use if setting not found.
     * @return The setting value or default.
     */
    public int get(Integer settingKey, int defaultValue) {
        String key = getSettingsKey(settingKey);
        return getIntSetting(key, defaultValue);
    }

    protected int getIntSetting(String settingKey, int defaultValue) {
        // This is the main method that actually fetches the value.
        return getPreferences().getInt(settingKey, defaultValue);
    }

    protected boolean set(String key, int value) {
        return getPreferences().edit()
                .putInt(key, value)
                .commit();
    }

    public boolean set(Integer key, int value) {
        String stringKey = getSettingsKey(key);
        return this.set(stringKey, value);
    }
}