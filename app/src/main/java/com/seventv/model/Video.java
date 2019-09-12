package com.seventv.model;

import android.util.Log;

import java.io.Serializable;

public class Video implements Serializable {

    private String mTitle;
    private String mDetailUrl;
    private String mThumbnailUrl;
    private String mPreviewUrl;
    private String mDate;
    private String mId;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDetailUrl() {
        return mDetailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        mDetailUrl = detailUrl;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        mPreviewUrl = previewUrl;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getId(){return mId;}

    public void setId(String id){mId = id;}

    public String getCategory(){
        for(String s : mDetailUrl.split("/")){
            if(s.contains("_")){
                return s.split("_")[0];
            }
        }
        Log.e("video", "error category");
        return null;
    }
}
