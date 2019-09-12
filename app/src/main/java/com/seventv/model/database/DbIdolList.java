package com.seventv.model.database;

import android.support.v7.widget.RecyclerView;
import com.seventv.SevenTVApplication;
import com.seventv.model.Idol;
import java.util.List;

public class DbIdolList extends DbList<Idol> {

    private String mCategory;
    private String mKeyword;

    public DbIdolList(String category, String keyword, RecyclerView.Adapter adapter){
        mCategory = category;
        mKeyword = keyword;
        mAdapter = adapter;
        SevenTVApplication.DB_HELPER.addDbList(this);
    }

    @Override
    protected int getSizeTotal() {
        return (int) SevenTVApplication.DB_HELPER.queryNumFavoriteIdol(mCategory, mKeyword);
    }

    @Override
    protected List<Idol> getCache(int offset, int limit) {
        return SevenTVApplication.DB_HELPER.queryFavoriteIdol(mCategory, mKeyword, offset, limit);
    }
}
