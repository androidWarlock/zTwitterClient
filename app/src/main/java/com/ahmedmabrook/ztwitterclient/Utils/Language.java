package com.ahmedmabrook.ztwitterclient.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.ahmedmabrook.ztwitterclient.Views.Activities.LoginActivity;

import java.util.Locale;

/**
 * Authored by Ahmed Mabrook - ahmed.mabrook@chestnut.com
 * On Jan 2017 .
 * Language: Describtion goes here.
 */

public class Language {


    /**
     * @param context Context of the application
     * @return the current language of the device
     */
    public static String getCurrentLang(Context context) {

        if (context != null) {
            String current = context.getSharedPreferences("yellow", Context.MODE_PRIVATE).getString("lang", null);
            if (current == null) {
                current = context.getResources().getConfiguration().locale.getLanguage();
            }
            if (current.equalsIgnoreCase("ar")) {
                return "ar";
            }
            return "en";
        }
        return "en";
    }

    public static void loadLanguage(Context context) {
        SharedPreferences settings = context.getSharedPreferences("yellow", Context.MODE_PRIVATE);

        String storedLang = settings.getString("lang", null);
        String langToLoad = "";
        if (storedLang == null) {
            langToLoad = Language.getCurrentLang(context);
        } else {
            langToLoad = storedLang;
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lang", langToLoad);
        editor.commit();

        Locale locale = new Locale(langToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

    }

    public static void changeLanguage(Activity activity) {

        SharedPreferences settings = activity.getSharedPreferences("yellow", activity.getBaseContext().MODE_PRIVATE);
        String currentlang = settings.getString("lang", null);
        String languageToLoad = ""; // your language
        // if arabic show switch to english
        if (currentlang != null) {
            if (currentlang.equalsIgnoreCase("ar")) {
                languageToLoad = "en";
            } else {
                languageToLoad = "ar";
            }
        } else {
            languageToLoad = Language.getCurrentLang(activity.getApplicationContext());
        }
        SharedPreferences.Editor editor = settings.edit().putString("lang", languageToLoad);
        editor.commit();

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());

        Intent startAgain = new Intent(activity, LoginActivity.class);
        startAgain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(startAgain);

    }

    /**
     * detect the language of the string
     *
     * @param s the tested string
     * @return "En" for english string , "Ar" for arabic string
     */
    public static String detectLanguage(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return "ar";
            i += Character.charCount(c);
        }
        return "en";
    }

    public static String convertToArabicDigits(String value) {
        String newValue = value.replace("1", "١").replace("2", "٢").replace("3", "٣").replace("4", "٤").replace("5", "٥")
                .replace("6", "٦").replace("7", "٧").replace("8", "٨").replace("9", "").replace("0", "٠");

        return newValue;
    }
}

