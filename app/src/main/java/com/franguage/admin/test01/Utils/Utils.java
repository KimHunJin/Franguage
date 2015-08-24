package com.franguage.admin.test01.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HunJin on 2015-08-18.
 */
public class Utils {

    public static void setAppPreferences
            (Context context, String key, String value)
    {
        SharedPreferences pref = null;
        pref = context.getSharedPreferences("logo", 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);

        prefEditor.commit();
    }


    public static String getAppPreferences
            (Context context, String key)
    {
        String returnValue = null;

        SharedPreferences pref = null;
        pref = context.getSharedPreferences("logo", 0);
        returnValue = pref.getString(key, "");
        return returnValue;
    }
}
