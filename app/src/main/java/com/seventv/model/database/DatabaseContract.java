package com.seventv.model.database;

import android.provider.BaseColumns;

public final class DatabaseContract {

    private DatabaseContract(){}

    public static class FavoriteVideo implements BaseColumns{
        public static final String TABLE_NAME = "favorite_video";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
        public static final String COLUMN_NAME_DATE = "release_date";
        public static final String COLUMN_NAME_ID = "video_id";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_PREVIEW = "preview";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_CATEGORY + " TEXT," +
                COLUMN_NAME_DATE + " TEXT," +
                COLUMN_NAME_ID + " TEXT," +
                COLUMN_NAME_THUMBNAIL + " TEXT," +
                COLUMN_NAME_TITLE + " TEXT," +
                COLUMN_NAME_URL + " TEXT UNIQUE," +
                COLUMN_NAME_PREVIEW + " TEXT)";
    }

    public static class FavoriteIdol implements BaseColumns{
        public static final String TABLE_NAME = "favorite_idol";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
        public static final String COLUMN_NAME_CODE = "idol_code";
        public static final String COLUMN_NAME_CATEGORY = "category";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_CATEGORY + " TEXT," +
                COLUMN_NAME_CODE + " TEXT," +
                COLUMN_NAME_THUMBNAIL + " TEXT," +
                COLUMN_NAME_NAME + " TEXT," +
                "UNIQUE(" + COLUMN_NAME_CATEGORY + "," + COLUMN_NAME_CODE + "))";
    }
}
