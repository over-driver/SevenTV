package com.seventv.model;

public abstract class Filterable  {

    protected String mCode;
    protected String mValue;

    public Filterable(String url, String value){
        if(url != null){
            String[] split = url.split("/");
            mCode = split[split.length - 3];
        }
        mValue = value;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}
