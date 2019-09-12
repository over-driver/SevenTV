package com.seventv.fragment;

import android.os.Bundle;
import com.seventv.adapter.VideoListAdapter;
import com.seventv.model.Video;
import com.seventv.R;
import com.seventv.model.database.DbVideoList;

public class DbVideoListFragment extends DbListFragment<Video> {

    public static DbVideoListFragment newInstance(String category, String query){
        DbVideoListFragment fragment = new DbVideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CATEGORY, category);
        bundle.putString(KEY_QUERY, query);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected DbVideoList loadMoreFromDb(){
        return new DbVideoList(mCategory, mQuery, mListAdapter);
    }

    @Override
    protected VideoListAdapter newAdapter(){
        return new VideoListAdapter(R.layout.item_video, null, getActivity(), mCategory);
    }
}
