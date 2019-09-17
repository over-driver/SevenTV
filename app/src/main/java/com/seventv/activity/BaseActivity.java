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

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String nightMode = PreferenceManager.getDefaultSharedPreferences(this).getString("night_mode", "off");
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
        //getTheme().applyStyle(R.style.ThemeRed, true);
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
