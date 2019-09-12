package com.seventv.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.seventv.model.Idol;
import com.seventv.model.Video;
import com.seventv.model.database.DatabaseContract.FavoriteVideo;
import com.seventv.model.database.DatabaseContract.FavoriteIdol;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Favorite.db";

    private List<DbList> mDbLists = new ArrayList<>();

    public FavoriteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d("DATABASE", "onCreate called");
        db.execSQL(FavoriteVideo.SQL_CREATE_TABLE);
        db.execSQL(FavoriteIdol.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d("DATABASE", "onUpgrade called");
        //db.execSQL("");
        //onCreate(db);
    }

    // FavoriteVideo

    public void insertFavoriteVideo(Video video){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoriteVideo.COLUMN_NAME_CATEGORY, video.getCategory());
        values.put(FavoriteVideo.COLUMN_NAME_DATE, video.getDate());
        values.put(FavoriteVideo.COLUMN_NAME_THUMBNAIL, video.getThumbnailUrl());
        values.put(FavoriteVideo.COLUMN_NAME_ID, video.getId());
        values.put(FavoriteVideo.COLUMN_NAME_TITLE, video.getTitle());
        values.put(FavoriteVideo.COLUMN_NAME_URL, video.getDetailUrl());
        values.put(FavoriteVideo.COLUMN_NAME_PREVIEW, video.getPreviewUrl());

        long newRowId = db.insert(FavoriteVideo.TABLE_NAME, null, values);
        onDbUpdated();
    }

    public boolean hasFavoriteVideo(Video video){
        SQLiteDatabase db = getWritableDatabase();
        String selection = FavoriteVideo.COLUMN_NAME_URL + " = ?";
        String[] selectionArgs = {video.getDetailUrl()};
        Cursor cursor = db.query(
                FavoriteVideo.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean flag = cursor.getCount() > 0;
        cursor.close();
        return flag;
    }

    public void deleteFavoriteVideo(Video video){
        SQLiteDatabase db = getWritableDatabase();
        String selection = FavoriteVideo.COLUMN_NAME_URL + " = ?";
        String[] selectionArgs = {video.getDetailUrl()};
        db.delete(FavoriteVideo.TABLE_NAME, selection, selectionArgs);
        onDbUpdated();
    }

    public long queryNumFavoriteVideo(String category, String keyword){
        SQLiteDatabase db = getReadableDatabase();
        String selection;
        String[] selectionArgs = {category};
        if (keyword.length() > 0){
            selection = FavoriteVideo.COLUMN_NAME_CATEGORY + " = ? AND " + FavoriteVideo.COLUMN_NAME_TITLE + " LIKE ?";
            selectionArgs = new String[]{category, "%" + keyword + "%"};
        } else{
            selection = FavoriteVideo.COLUMN_NAME_CATEGORY + " = ?";
        }
        return DatabaseUtils.queryNumEntries(db, FavoriteVideo.TABLE_NAME, selection, selectionArgs);
    }

    public List<Video> queryFavoriteVideo(String category, String keyword, int offset, int limit){
        SQLiteDatabase db = getReadableDatabase();
        String selection;
        String[] selectionArgs = {category};
        if (keyword.length() > 0){
            selection = FavoriteVideo.COLUMN_NAME_CATEGORY + " = ? AND " + FavoriteVideo.COLUMN_NAME_TITLE + " LIKE ?";
            selectionArgs = new String[]{category, "%" + keyword + "%"};
        } else{
            selection = FavoriteVideo.COLUMN_NAME_CATEGORY + " = ?";
        }
        String limitString = (offset >= 0) ? (offset + "," + limit) : null;
        Cursor cursor = db.query(
                FavoriteVideo.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
                limitString
        );
        List<Video> videos = new ArrayList<>();
        while(cursor.moveToNext()){
            Video video = new Video();
            video.setId(cursor.getString(cursor.getColumnIndex(FavoriteVideo.COLUMN_NAME_ID)));
            video.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteVideo.COLUMN_NAME_TITLE)));
            video.setDate(cursor.getString(cursor.getColumnIndex(FavoriteVideo.COLUMN_NAME_DATE)));
            video.setThumbnailUrl(cursor.getString(cursor.getColumnIndex(FavoriteVideo.COLUMN_NAME_THUMBNAIL)));
            video.setDetailUrl(cursor.getString(cursor.getColumnIndex(FavoriteVideo.COLUMN_NAME_URL)));
            video.setPreviewUrl(cursor.getString(cursor.getColumnIndex(FavoriteVideo.COLUMN_NAME_PREVIEW)));
            videos.add(video);
        }
        cursor.close();
        return videos;
    }

    // Favorite Idol

    public void insertFavoriteIdol(Idol idol, String category){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoriteIdol.COLUMN_NAME_CATEGORY, category);
        values.put(FavoriteIdol.COLUMN_NAME_CODE, idol.getCode());
        values.put(FavoriteIdol.COLUMN_NAME_THUMBNAIL, idol.getAvatarUrl());
        values.put(FavoriteIdol.COLUMN_NAME_NAME, idol.getName());

        long newRowId = db.insert(FavoriteIdol.TABLE_NAME, null, values);
        onDbUpdated();
    }

    public boolean hasFavoriteIdol(Idol idol, String category){
        SQLiteDatabase db = getWritableDatabase();
        String selection = FavoriteIdol.COLUMN_NAME_CODE + " = ? AND " + FavoriteIdol.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {idol.getCode(), category};
        Cursor cursor = db.query(
                FavoriteIdol.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean flag = cursor.getCount() > 0;
        cursor.close();
        return flag;
    }

    public void deleteFavoriteIdol(Idol idol, String category){
        SQLiteDatabase db = getWritableDatabase();
        String selection = FavoriteIdol.COLUMN_NAME_CODE + " = ? AND " + FavoriteIdol.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {idol.getCode(), category};
        db.delete(FavoriteIdol.TABLE_NAME, selection, selectionArgs);
        onDbUpdated();
    }

    public List<Idol> queryFavoriteIdol(String category, String keyword, int offset, int limit){
        SQLiteDatabase db = getWritableDatabase();
        String selection;
        String[] selectionArgs = {category};
        if (keyword.length() > 0){
            selection = FavoriteIdol.COLUMN_NAME_CATEGORY + " = ? AND " + FavoriteIdol.COLUMN_NAME_NAME + " LIKE ?";
            selectionArgs = new String[]{category, "%" + keyword + "%"};
        } else{
            selection = FavoriteIdol.COLUMN_NAME_CATEGORY + " = ?";
        }
        String limitString = (offset >= 0) ? (offset + "," + limit) : null;
        Cursor cursor = db.query(
                FavoriteIdol.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
                limitString
        );
        List<Idol> idols = new ArrayList<>();
        while(cursor.moveToNext()){
            Idol idol = new Idol(cursor.getString(cursor.getColumnIndex(FavoriteIdol.COLUMN_NAME_CODE)) + "/ / ",
                    cursor.getString(cursor.getColumnIndex(FavoriteIdol.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndex(FavoriteIdol.COLUMN_NAME_THUMBNAIL)));
            idols.add(idol);
        }
        cursor.close();
        return idols;
    }

    public long queryNumFavoriteIdol(String category, String keyword){
        SQLiteDatabase db = getReadableDatabase();
        String selection;
        String[] selectionArgs = {category};
        if (keyword.length() > 0){
            selection = FavoriteIdol.COLUMN_NAME_CATEGORY + " = ? AND " + FavoriteIdol.COLUMN_NAME_NAME + " LIKE ?";
            selectionArgs = new String[]{category, "%" + keyword + "%"};
        } else{
            selection = FavoriteIdol.COLUMN_NAME_CATEGORY + " = ?";
        }
        return DatabaseUtils.queryNumEntries(db, FavoriteIdol.TABLE_NAME, selection, selectionArgs);
    }

    //

    public void addDbList(DbList dbList){
        if(!mDbLists.contains(dbList)){
            mDbLists.add(dbList);
        }
    }

    public void removeDbList(DbList dbList){
        mDbLists.remove(dbList);
    }

    public void onDbUpdated(){
        for(DbList dbList : mDbLists){
            dbList.deleteCache();
        }
    }

    public void cleanDb(){
        SQLiteDatabase db = getWritableDatabase();
        for(String tn: new String[]{FavoriteVideo.TABLE_NAME, FavoriteIdol.TABLE_NAME}){
            try {
                db.execSQL("delete from "+ tn);
            }catch (Exception e){
                Log.e("DATABASE", "table " + tn + " not found");
            }
        }
        onDbUpdated();
    }

}
