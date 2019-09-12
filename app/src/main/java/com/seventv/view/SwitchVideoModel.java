package com.seventv.view;

import com.seventv.model.SevenVideoSource;

public class SwitchVideoModel {

    public final static int SOURCE = 0;
    public final static int PART = 1;
    public final static int RESOLUTION = 2;

    private String mData;
    private int mType;

    public SwitchVideoModel(String data, int type) {
        mData = data;
        mType = type;
    }

    public String getData() {
        return mData;
    }

    @Override
    public String toString() {
        switch (mType){
            case SOURCE:
                return SevenVideoSource.SOURCE_NAME.get(mData);
            case PART:
                return "Part: " + (Integer.parseInt(mData) + 1);
            case RESOLUTION:
                return mData;
        }
        return null;
    }
}
