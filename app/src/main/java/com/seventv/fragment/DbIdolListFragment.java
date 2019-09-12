package com.seventv.fragment;

import android.os.Bundle;
import com.seventv.R;
import com.seventv.adapter.IdolAdapter;
import com.seventv.model.Idol;
import com.seventv.model.database.DbIdolList;

public class DbIdolListFragment extends DbListFragment<Idol> {

    public static DbIdolListFragment newInstance(String category, String query){
        DbIdolListFragment fragment = new DbIdolListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CATEGORY, category);
        bundle.putString(KEY_QUERY, query);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected DbIdolList loadMoreFromDb(){
        return new DbIdolList(mCategory, mQuery, mListAdapter);
    }

    @Override
    protected IdolAdapter newAdapter(){
        return new IdolAdapter(R.layout.item_idol_list, null, mCategory);
    }
}
