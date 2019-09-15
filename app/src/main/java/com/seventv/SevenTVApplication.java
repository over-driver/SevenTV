package com.seventv;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.seventv.model.database.FavoriteDbHelper;

import java.util.Locale;

public class SevenTVApplication extends Application {

    public static FavoriteDbHelper DB_HELPER;
    private static SevenTVApplication app;
    private static Context mContext;
    private static Locale mLocale;
    private static String mLanguage;
    private static String LANGUAGE_CODE_KEY = "language_code";

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
    }

    //public static SevenTVApplication getApp(){
    //    return app;
    //}

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

    public static String getLanguage(){
        return mLanguage;
    }

    public static Locale getLocale(){
        String language;
        if(mLocale == null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                language = app.getResources().getConfiguration().getLocales().get(0).getLanguage();
                //Log.d("LOCALE", app.getResources().getConfiguration().getLocales().toString());
            } else {
                language = Locale.getDefault().getLanguage();
            }
            //Log.d("LOCALE", "null " + language);
        } else {
            language = mLocale.getLanguage();
        }
        //Log.d("LOCALE", "Compare " + language + " " + mLanguage);
        if(!language.equals(mLanguage)){
            SevenTVApplication.DB_HELPER.cleanDb();
            //Toast.makeText(app, "delete DB", Toast.LENGTH_LONG).show();
            mLanguage = language;
            PreferenceManager.getDefaultSharedPreferences(app).edit().putString(LANGUAGE_CODE_KEY, mLanguage).apply();
        }
        return mLocale;
    }

}
