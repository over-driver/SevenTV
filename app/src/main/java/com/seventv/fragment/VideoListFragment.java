package com.seventv.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.seventv.BuildConfig;
import com.seventv.R;
import com.seventv.model.Video;
import com.seventv.adapter.VideoListAdapter;
import com.seventv.network.api.SevenAPI;
import com.seventv.network.parser.SevenParser;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class VideoListFragment extends ListFragmentBase<Video> {

    private static String KEY_CATEGORY = "key_category";
    private static String KEY_ACTION= "key_action";
    private static String KEY_FILTER = "key_filter";
    private static String KEY_QUERY = "key_query";

    int PAGE_SIZE = 20;

    private String mCategory;
    private String mAction;
    private String mFilter;
    private String mQuery;

    public static VideoListFragment newInstance(String category){
        return newInstance(category, SevenAPI.ACTION_LIST, "", "");
    }

    public static VideoListFragment newInstance(String category, String query){
        return newInstance(category, SevenAPI.ACTION_SEARCH, "", query);
    }

    public static VideoListFragment newInstance(String category, String filter, String query){
        return newInstance(category, SevenAPI.ACTION_FILTER, filter, query);
    }

    public static VideoListFragment newInstance(String category, String action, String filter, String query){
        VideoListFragment fragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CATEGORY, category);
        bundle.putString(KEY_ACTION, action);
        bundle.putString(KEY_FILTER, filter);
        bundle.putString(KEY_QUERY, query);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getString(KEY_CATEGORY);
        mAction = getArguments().getString(KEY_ACTION);
        mFilter = getArguments().getString(KEY_FILTER);
        mQuery = getArguments().getString(KEY_QUERY);
    }

    @Override
    protected VideoListAdapter newAdapter(){
        return new VideoListAdapter(R.layout.item_video, null, getActivity(), mCategory);
    }

    @Override
    protected void loadMore(){
        getObservable().subscribe(getObserver());
    }

    private Observable<String> getObservable(){
        Observable<String> observable = null;
        String page = Integer.toString(mPage);
        switch (mAction){
            case SevenAPI.ACTION_LIST:
                observable = SevenAPI.INSTANCE.getVideoList(mCategory, page);
                break;
            case SevenAPI.ACTION_SEARCH:
                observable = SevenAPI.INSTANCE.searchVideo(mCategory, mQuery, page);
                break;
            case SevenAPI.ACTION_FILTER:
                observable = SevenAPI.INSTANCE.filterVideo(mCategory, mFilter, mQuery, page);
                break;
        }
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) ;
    }

    private Observer<String> getObserver(){

        final boolean isRefresh = mPage == 1;

        return new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String response){
                //Log.d("visible",mCategory + isVisible());
                List<Video> videos = SevenParser.parseVideoList(response, mCategory);
                if (BuildConfig.DEBUG){
                    Log.d("VIDEO_LIST_FRAGMENT", "load " + videos.size() + " new videos");
                }
                if(isRefresh){
                    mListAdapter.setNewData(videos);
                    mListAdapter.setEnableLoadMore(true);
                    mRefreshLayout.setRefreshing(false);
                } else {
                    mListAdapter.addData(videos);
                }
                if (videos.size() < PAGE_SIZE){
                    mListAdapter.loadMoreEnd(isRefresh);
                    if (isVisible() && isAdded() && getUserVisibleHint() && !isRefresh){
                        Toast.makeText(VideoListFragment.this.getActivity(),
                                "no more data", Toast.LENGTH_SHORT).show();
                    }
                } else if (!isRefresh){
                    mListAdapter.loadMoreComplete();
                }
                //mVideoListAdapter.setEnableLoadMore(true);
            }

            @Override
            public void onError(@NonNull Throwable e){
                Log.e("VIDEO_LIST_FRAGMENT", "Load more error: " + e);
                mRefreshLayout.setRefreshing(false);
                mListAdapter.loadMoreFail();
            }

            @Override
            public void onComplete(){}

        };
    }

}
