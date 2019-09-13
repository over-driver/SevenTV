package com.seventv;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.seventv.model.database.FavoriteDbHelper;

public class SevenTVApplication extends Application {

    public static FavoriteDbHelper DB_HELPER;
    private static SevenTVApplication app;

    @Override
    public void onCreate(){
        super.onCreate();
        DB_HELPER = new FavoriteDbHelper(this);
        DB_HELPER.getWritableDatabase();
        app = this;
    }

    public static SevenTVApplication getApp(){
        return app;
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

}
