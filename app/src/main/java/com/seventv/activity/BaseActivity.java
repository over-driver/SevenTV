package com.seventv.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.seventv.R;
import com.seventv.SevenTVApplication;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    private String mColor;
    private boolean mNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mColor = SevenTVApplication.getThemeColor();
        mNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        int themeInt;
        switch (mColor){
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

    @Override
    protected void onResume(){
        super.onResume();

        if(!mColor.equals(SevenTVApplication.getThemeColor()) ||
                mNightMode != ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)){
            recreate();
        }
    }
}
