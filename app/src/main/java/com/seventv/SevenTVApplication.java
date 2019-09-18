package com.seventv;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.seventv.model.database.FavoriteDbHelper;

import java.util.Locale;

public class SevenTVApplication extends Application {

    public static FavoriteDbHelper DB_HELPER;
    private static SevenTVApplication app;
    private static Context mContext;
    private static Locale mLocale;
    private static String mLanguage;
    private static String LANGUAGE_CODE_KEY = "language_code";
    private static String mColor;

    @Override
    public void onCreate(){
        super.onCreate();
        DB_HELPER = new FavoriteDbHelper(this);
        DB_HELPER.getWritableDatabase();
        app = this;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mLanguage = sp.getString(LANGUAGE_CODE_KEY, "zh");
        if(!sp.contains(LANGUAGE_CODE_KEY)){
            sp.edit().putString(LANGUAGE_CODE_KEY, "zh").apply();
        }
        setLocale(sp.getString("language", "auto"));

        String nightMode = PreferenceManager.getDefaultSharedPreferences(this).getString("night_mode", "off");
        setNightMode(nightMode);

        mColor = PreferenceManager.getDefaultSharedPreferences(this).getString("theme_color", "pink");
    }

    public static long getVersionCode(Context context){
        try{
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e){
            Log.e("PACKAGE_NAME", e.toString());
            return 0;
        }
    }

    public static String myGetString(int resId){
        return mContext.getString(resId);
    }

    public static void setLocale(String language){
        if(language.equals(app.getResources().getStringArray(R.array.language_preference_value)[0])){
            mContext = app.getApplicationContext();
            mLocale = null;
        } else {
            String[] languageSplit = language.split("-r");
            if(languageSplit.length == 1){
                mLocale = new Locale(languageSplit[0]);
            }else{
                mLocale = new Locale(languageSplit[0], languageSplit[1]);
            }
            Configuration config = new Configuration(app.getResources().getConfiguration());
            config.setLocale(mLocale);
            mContext = app.createConfigurationContext(config);
        }
    }

    public static Locale getLocale(){
        String language = (mLocale == null) ? app.getString(R.string.language_code) : mLocale.getLanguage();
        if(!language.equals(mLanguage)){
            SevenTVApplication.DB_HELPER.cleanDb();
            mLanguage = language;
            PreferenceManager.getDefaultSharedPreferences(app).edit().putString(LANGUAGE_CODE_KEY, mLanguage).apply();
        }
        return mLocale;
    }

    public static void setNightMode(String nightMode){
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
    }

    public static String getThemeColor(){
        return mColor;
    }

    public static void setThemeColor(String color){
        mColor = color;
    }

}
