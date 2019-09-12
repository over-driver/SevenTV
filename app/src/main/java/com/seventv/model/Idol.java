package com.seventv.model;


public class Idol extends Filterable{

    private String mAvatarUrl;

    public Idol(String url, String name, String avatarUrl){
        super(url, name);
        mAvatarUrl = avatarUrl;
    }


    public String getName() {
        return mValue;
    }

    public void setName(String name) {
        mValue = name;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }
}
