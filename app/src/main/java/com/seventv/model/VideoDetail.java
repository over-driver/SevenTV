package com.seventv.model;

import java.util.ArrayList;
import java.util.List;

public class VideoDetail{

    private List<Info> mInfos;
    private List<Genre> mGenres;
    private List<Idol> mIdols;
    private String mCoverUrl;
    private List<String> mScreenshotUrls;
    private SevenVideoSource mSevenVideoSource;

    public VideoDetail(){
        mInfos = new ArrayList<>();
        mGenres = new ArrayList<>();
        mIdols= new ArrayList<>();
    }

    public String getTitle() {
        if (mInfos.size() > 0){
            return mInfos.get(0).getValue();
        } else {
            return null;
        }
    }

    public String getId(){
        if (mInfos.size() > 1){
            return mInfos.get(1).getValue();
        } else {
            return null;
        }
    }

    public List<Info> getInfos(){return mInfos;}

    public void addInfo(int key, String value, String url){
        mInfos.add(new Info(key, url, value));
    }

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void addGenres(String url, String value) {
        mGenres.add(new Genre(url, value));
    }

    public List<Idol> getIdols() {
        return mIdols;
    }

    public void addIdols(String url, String name, String avatarUrl){
        mIdols.add(new Idol(url, name, avatarUrl));
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
    }

    public List<String> getScreenshotUrls() {
        return mScreenshotUrls;
    }

    public void setScreenshotUrls(List<String> screenshotUrls) {
        mScreenshotUrls = screenshotUrls;
    }

    public SevenVideoSource getSevenVideoSource() {
        return mSevenVideoSource;
    }

    public void setSevenVideoSource(SevenVideoSource sevenVideoSource) {
        mSevenVideoSource = sevenVideoSource;
    }
}
