package com.eb.sc.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



public class BaseConfig {
    private static BaseConfig cm = null;
    private Editor editor;
    private SharedPreferences sharedPreferences;

    public static BaseConfig getInstance(Context context) {
        if (cm == null) {
            cm = new BaseConfig(context);
        }
        return cm;
    }

    public BaseConfig(Context context) {
        String packageName = context.getApplicationInfo().packageName;
        sharedPreferences = context.getSharedPreferences(
                String.format("sd_%s", packageName), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setStringValue(String key, String value) {
        if (value == null) {
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }
        editor.commit();
    }





    public String getStringValue(String key, String def) {
        return sharedPreferences.getString(key, def);
    }

    public void setIntValue(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getIntValue(String key, int def) {
        return sharedPreferences.getInt(key, def);
    }
    public void setLongValue(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLongValue(String key, long def) {
        return sharedPreferences.getLong(key, def);
    }

    public void removeValue(String key) {
        editor.remove(key);
        editor.commit();
    }

    public void clear() {
        editor.clear();
    }

    public void register(
            SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences
                .registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public void unregister(
            SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }
}
