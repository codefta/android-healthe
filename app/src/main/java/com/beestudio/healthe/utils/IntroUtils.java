package com.beestudio.healthe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class IntroUtils {

    private Context mContext;

    public IntroUtils(Context context) {
        this.mContext = context;
    }

    private static final String PREFERENCES_FILE = "myPref";

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(settingName, defaultValue);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }
}
