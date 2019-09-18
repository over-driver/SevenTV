package com.seventv.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;

import com.seventv.R;
import com.seventv.SevenTVApplication;

import java.util.Locale;

import static android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String nightMode = getDefaultSharedPreferences(this).getString("night_mode", "off");
        int nightModeCode;
        switch (nightMode){
            case "on":
                nightModeCode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case "off":
                nightModeCode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case "system":
                nightModeCode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
            case "time":
                nightModeCode = AppCompatDelegate.MODE_NIGHT_AUTO;
                break;
            default:
                nightModeCode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        AppCompatDelegate.setDefaultNightMode(nightModeCode);

        super.onCreate(savedInstanceState);

        String themeColor = PreferenceManager.getDefaultSharedPreferences(this).getString("theme_color", "pink");
        int themeInt;
        switch (themeColor){
            case "pink":
                themeInt = R.style.ThemePink;
                break;
            case "red":
                themeInt = R.style.ThemeRed;
                break;
            case "blue":
                themeInt = R.style.ThemeBlue;
                break;
            case "yellow":
                themeInt = R.style.ThemeYellow;
                break;
            case "purple":
                themeInt = R.style.ThemePurple;
                break;
            case "green":
                themeInt = R.style.ThemeGreen;
                break;
            default:
                themeInt = R.style.ThemePink;
                break;
        }
        getTheme().applyStyle(themeInt, true);
    }

    @Override
    protected void attachBaseContext(Context newBase){
        Locale locale = SevenTVApplication.getLocale();
        if(locale == null){
            super.attachBaseContext(newBase);
        }
        else{
            Configuration config = newBase.getResources().getConfiguration();
            config.setLocale(locale);
            super.attachBaseContext(newBase.createConfigurationContext(config));
        }
    }
}
