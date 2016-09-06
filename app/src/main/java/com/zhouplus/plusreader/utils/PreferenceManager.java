package com.zhouplus.plusreader.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhouplus
 * Time at 2016/8/15
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class PreferenceManager {
    public static Boolean getPreferenceBoolean(Context context, String key, Boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(PlusConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static String getPreferenceString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(PlusConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static void setPreferenceString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(PlusConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public static int getPreferenceInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(PlusConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static void setPreferenceInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(PlusConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).apply();
    }

    public static void setPreferenceBoolean(Context context, String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(PlusConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }
}
