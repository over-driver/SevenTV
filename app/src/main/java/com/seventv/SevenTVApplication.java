package com.seventv;

import android.app.Application;

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

}
