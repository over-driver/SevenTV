package com.seventv.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.seventv.SevenTVApplication;
import com.seventv.model.SevenVideoSource;
import com.seventv.adapter.GenreAdapter;
import com.seventv.model.Idol;
import com.seventv.adapter.IdolAdapter;
import com.seventv.adapter.InfoAdapter;
import com.seventv.R;
import com.seventv.adapter.ScreenshotAdapter;
import com.seventv.model.Video;
import com.seventv.model.VideoDetail;
import com.seventv.model.Genre;
import com.seventv.model.Info;
import com.seventv.network.api.BestjavpornAPI;
import com.seventv.network.api.NetflavAPI;
import com.seventv.network.api.SevenAPI;
import com.seventv.network.parser.BestjavpornParser;
import com.seventv.network.parser.NetflavParser;
import com.seventv.network.parser.SevenParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class VideoDetailActivity extends AppCompatActivity {

    private static final String EXTRA_VIDEO = "video";

    @BindView(R.id.floating_action_button)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.toolbar_layout_background)
    ImageView mToolbarLayoutBackground;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.video_content)
    NestedScrollView mVideoContent;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.infos_recycler_view)
    RecyclerView mInfosRecyclerView;
    @BindView(R.id.infos_empty_text)
    TextView mInfosEmptyTextView;
    @BindView(R.id.genres_recycler_view)
    RecyclerView mGenresRecyclerView;
    @BindView(R.id.genre_empty_text)
    TextView mGenresEmptyTextView;
    @BindView(R.id.idols_recycler_view)
    RecyclerView mIdolsRecyclerView;
    @BindView(R.id.idols_empty_text)
    TextView mIdolsEmptyTextView;
    @BindView(R.id.screenshots_recycler_view)
    RecyclerView mScreenshotsRecyclerView;
    @BindView(R.id.screenshots_empty_text)
    TextView mScreenshotsEmptyTextView;

    MenuItem mStarButton;

    private Video mVideo;
    private VideoDetail mVideoDetail;
    private String mCategory;
    private boolean mSourceReady = false;

    public static Intent newIntent(Context context, Video video){
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVideo = (Video) getIntent().getSerializableExtra(EXTRA_VIDEO);
        mCategory = mVideo.getCategory();

        initRecyclerViews();
        getData();
    }

    private void getData(){
        String url = mVideo.getDetailUrl();
        url = url.substring(url.indexOf('/', 10) + 1);

        SevenAPI.INSTANCE.getVideoDetail(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@NonNull String response){
                        mVideoDetail = SevenParser.parseVideoDetail(response, mCategory);
                        mVideo.setTitle(mVideoDetail.getTitle());
                        mVideo.setId(mVideoDetail.getId());
                        setUpDetail();
                    }

                    @Override
                    public void onError(@NonNull Throwable e){ Log.e("VideoDetailActivity", "Load detail ERROR: " + e);}

                    @Override
                    public void onComplete(){ }
                });
    }

    private void initRecyclerViews(){
        mInfosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mInfosRecyclerView.setAdapter(new InfoAdapter(R.layout.item_info, new ArrayList<>()));

        mGenresRecyclerView.setLayoutManager(new FlexboxLayoutManager(this, FlexDirection.ROW));
        mGenresRecyclerView.setAdapter(new GenreAdapter(new ArrayList<>(), VideoDetailActivity.this));

        mScreenshotsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mScreenshotsRecyclerView.setAdapter(new ScreenshotAdapter(R.layout.item_screenshot, new ArrayList()));

        mIdolsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mIdolsRecyclerView.setAdapter(new IdolAdapter(R.layout.item_idol, new ArrayList(), mCategory));
    }

    private void setUpDetail(){
        if (mVideoDetail.getSevenVideoSource().needMoreSource()){
            Log.d("TEST_NETWORK", "try to load more source");
            if(mCategory.equals(SevenAPI.CATEGORY_CHINESE)){
                LoadNetflav();
            } else {
                LoadBestjavporn();
            }
        } else {
            mSourceReady = true;
        }

        setUpFloatingActionButton();
        setUpCover();
        setUpInfos();
        setUpGenres();
        setUpScreenshots();
        setUpIdols();
        showDetail();
    }

    private void setUpFloatingActionButton(){
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mSourceReady){
                    Toast.makeText(VideoDetailActivity.this, "正在加载资源", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mVideoDetail.getSevenVideoSource().hasAvailableSource()){
                    Toast.makeText(VideoDetailActivity.this, "暂无资源", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent;
                if(mVideoDetail.getSevenVideoSource().needMoreSource()){
                    String[] split = mVideoDetail.getSevenVideoSource().getVideoSources().get(SevenVideoSource.AVGLE).get(0).url.split("/");
                    intent = WebViewActivity.newIntent(VideoDetailActivity.this, split[split.length - 2]);
                } else {
                    intent = VideoPlayActivity.newIntent(VideoDetailActivity.this, mVideoDetail);
                }
                startActivity(intent);
            }
        });
        mFloatingActionButton.bringToFront();
    }

    private void setUpCover(){
        mCollapsingToolbarLayout.setTitle(mVideoDetail.getTitle());
        Glide.with(mToolbarLayoutBackground.getContext().getApplicationContext())
                .load(mVideoDetail.getCoverUrl())
                .into(mToolbarLayoutBackground);
    }

    private void setUpInfos(){
        List<Info> infos = mVideoDetail.getInfos();
        if(infos.isEmpty()){
            mInfosEmptyTextView.setVisibility(View.VISIBLE);
            mInfosRecyclerView.setVisibility(View.GONE);
        } else {
            mInfosRecyclerView.setNestedScrollingEnabled(false);
            ((InfoAdapter) mInfosRecyclerView.getAdapter()).setNewData(infos);
        }
    }

    private void setUpGenres(){
        List<Genre> genres = mVideoDetail.getGenres();
        if (genres.isEmpty()){
            mGenresRecyclerView.setVisibility(View.GONE);
            mGenresEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            ((FlexboxLayoutManager) mGenresRecyclerView.getLayoutManager()).setJustifyContent(JustifyContent.FLEX_START);
            ((FlexboxLayoutManager) mGenresRecyclerView.getLayoutManager()).setAlignItems(AlignItems.CENTER);;
            ((GenreAdapter) mGenresRecyclerView.getAdapter()).setNewData(genres);
        }
    }

    private void setUpScreenshots(){
        List<String> screenshotUrls = mVideoDetail.getScreenshotUrls();
        if(screenshotUrls.isEmpty()){
            mScreenshotsRecyclerView.setVisibility(View.GONE);
            mScreenshotsEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            ((ScreenshotAdapter) mScreenshotsRecyclerView.getAdapter()).setNewData(screenshotUrls);
        }
    }

    private void setUpIdols(){
        List<Idol> idols = mVideoDetail.getIdols();
        if (idols.isEmpty()){
            mIdolsRecyclerView.setVisibility(View.GONE);
            mIdolsEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            ((IdolAdapter) mIdolsRecyclerView.getAdapter()).setNewData(idols);
        }
    }

    private void showDetail(){
        mProgressBar.animate().setDuration(200).alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mProgressBar.setVisibility(View.GONE);
            }
        }).start();

        mVideoContent.setVisibility(View.VISIBLE);
        mVideoContent.setY(mVideoContent.getY() + 120);
        mVideoContent.setAlpha(0);
        mVideoContent.animate().translationY(0).alpha(1).setDuration(500).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateStarButton(boolean isFavorite){
        if(isFavorite){
            Drawable wrappedDrawable = DrawableCompat.wrap(AppCompatResources.getDrawable(VideoDetailActivity.this, R.drawable.ic_star));
            DrawableCompat.setTint(wrappedDrawable, Color.WHITE);
            mStarButton.setIcon(wrappedDrawable);
            mStarButton.setTitle("取消收藏");
        } else {
            Drawable wrappedDrawable = DrawableCompat.wrap(AppCompatResources.getDrawable(VideoDetailActivity.this, R.drawable.ic_star_border));
            DrawableCompat.setTint(wrappedDrawable, Color.WHITE);
            mStarButton.setIcon(wrappedDrawable);
            mStarButton.setTitle("收藏");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        mStarButton = menu.findItem(R.id.action_favorite);
        updateStarButton(SevenTVApplication.DB_HELPER.hasFavoriteVideo(mVideo));

        mStarButton.setOnMenuItemClickListener(item ->  {
            if(SevenTVApplication.DB_HELPER.hasFavoriteVideo(mVideo)){
                SevenTVApplication.DB_HELPER.deleteFavoriteVideo(mVideo);
                updateStarButton(false);
            } else{
                SevenTVApplication.DB_HELPER.insertFavoriteVideo(mVideo);
                updateStarButton(true);
            }
            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }

    public String getCategory() {
        return mCategory;
    }

    private void LoadBestjavporn(){
        Log.d("TEST_NETWORK", "try to load more source / Bestjavporn");

        BestjavpornAPI.INSTANCE.searchVideo(mVideoDetail.getId()).subscribeOn(Schedulers.io())
                .flatMap((response) -> {
                    String url = BestjavpornParser.parsePageUrl(response);
                    Log.d("TEST_NETWORK", "url:"  + url);
                    if(url.length() > 0 && url.contains(mVideoDetail.getId().toLowerCase())){
                        return BestjavpornAPI.INSTANCE.getVideo(url);
                    } else {
                        return Observable.just("");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        if (s.length() > 0){
                            String url = BestjavpornParser.parseSource(s);
                            Log.d("VideoDetailActivity", "new source: "  + url);
                            mVideoDetail.getSevenVideoSource().addSource(SevenVideoSource.BESTJAVPORN, url);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("TEST_NETWORK", "Load more source error: " + e);
                        mSourceReady = true;
                    }

                    @Override
                    public void onComplete() {
                        Log.d("VideoDetailActivity", "load more source complete");
                        mSourceReady = true;
                    }
                });
    }

    private void LoadNetflav(){
        Log.d("TEST_NETWORK", "try to load more source / Netflav");

        NetflavAPI.INSTANCE.searchVideo(mVideoDetail.getId()).subscribeOn(Schedulers.io())
                .flatMap((response) -> {
                    String url = NetflavParser.parsePageUrl(response, mVideoDetail.getId());
                    Log.d("TEST_NETWORK", "url:"  + url);
                    if(url.length() > 0){
                        return NetflavAPI.INSTANCE.getVideo(url);
                    } else {
                        return Observable.just("");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        if (s.length() > 0){
                            String url = NetflavParser.parseSource(s);
                            Log.d("VideoDetailActivity", "new source: "  + url);
                            mVideoDetail.getSevenVideoSource().addSource(SevenVideoSource.FEMBED, url + " ");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("TEST_NETWORK", "Load more source error: " + e);
                        mSourceReady = true;
                    }

                    @Override
                    public void onComplete() {
                        Log.d("VideoDetailActivity", "load more source complete");
                        mSourceReady = true;
                    }
                });
    }
}
