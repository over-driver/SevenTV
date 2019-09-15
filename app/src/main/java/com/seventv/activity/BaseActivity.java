package com.seventv.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;

import com.seventv.SevenTVApplication;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

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
