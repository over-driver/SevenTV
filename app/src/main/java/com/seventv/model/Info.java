package com.seventv.model;

public class Info extends Filterable {

    int mType;

    public Info(int type, String url, String value){
        super(url, value);
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
