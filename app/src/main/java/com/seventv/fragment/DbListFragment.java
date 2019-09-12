package com.seventv.fragment;

import android.os.Bundle;
import com.seventv.model.database.DbList;

public abstract class DbListFragment<T> extends ListFragmentBase<T> {

    protected static String KEY_CATEGORY = "key_category";
    protected static String KEY_QUERY = "key_query";

    protected String mCategory;
    protected String mQuery;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getString(KEY_CATEGORY);
        mQuery = getArguments().getString(KEY_QUERY);
    }

    protected abstract DbList<T> loadMoreFromDb();

    @Override
    protected void loadMore(){
        mListAdapter.setNewData(loadMoreFromDb());
        mListAdapter.loadMoreComplete();
        mListAdapter.loadMoreEnd(true);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroy(){
        ((DbList) mListAdapter.getData()).destroy();
        super.onDestroy();
    }

}
