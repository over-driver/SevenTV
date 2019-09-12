package com.seventv.model.database;

import android.support.v7.widget.RecyclerView;
import com.seventv.SevenTVApplication;
import com.seventv.model.Video;
import java.util.List;

public class DbVideoList extends DbList<Video> {

    private String mCategory;
    private String mKeyword;

    public DbVideoList(String category, String keyword, RecyclerView.Adapter adapter){
        mCategory = category;
        mKeyword = keyword;
        mAdapter = adapter;
        SevenTVApplication.DB_HELPER.addDbList(this);
    }

    @Override
    protected int getSizeTotal() {
        return (int) SevenTVApplication.DB_HELPER.queryNumFavoriteVideo(mCategory, mKeyword);
    }

    @Override
    protected List<Video> getCache(int offset, int limit) {
        return SevenTVApplication.DB_HELPER.queryFavoriteVideo(mCategory, mKeyword, offset, limit);
    }
}
