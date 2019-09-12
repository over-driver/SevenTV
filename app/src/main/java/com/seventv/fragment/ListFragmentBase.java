package com.seventv.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seventv.BuildConfig;
import com.seventv.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ListFragmentBase<T> extends Fragment {

    @BindView(R.id.recycler_view_video)
    RecyclerView mRecyclerView;
    @BindView(R.id.content_view)
    SwipeRefreshLayout mRefreshLayout;

    protected BaseQuickAdapter<T, BaseViewHolder> mListAdapter;
    protected int mPage = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        ButterKnife.bind(this, view);

        initAdapter();
        initSwipeRefreshLayout();
        loadMore();
        return view;
    }

    protected void initSwipeRefreshLayout(){
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                loadMore();
            }
        });
        mRefreshLayout.setRefreshing(true);
    }

    protected abstract BaseQuickAdapter<T, BaseViewHolder> newAdapter();

    private void initAdapter(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListAdapter = newAdapter();
        mListAdapter.setOnLoadMoreListener(() -> {
            if(BuildConfig.DEBUG){
                Log.d("VIDEO_LIST_FRAGMENT", "Load More");
            }
            mPage = mPage + 1;
            loadMore();
        }, mRecyclerView);
        mListAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.setEnableLoadMore(true);
    }

    protected abstract void loadMore();

}
