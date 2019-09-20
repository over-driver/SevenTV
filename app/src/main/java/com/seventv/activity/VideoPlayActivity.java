package com.seventv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import com.seventv.R;
import com.seventv.model.SevenVideoSource;
import com.seventv.view.SevenVideoPlayer;
import com.seventv.model.SevenVideoSourceManager;
import com.seventv.model.VideoDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class VideoPlayActivity extends BaseActivity {

    private static final String EXTRA_VIDEO_SOURCE = "video_source";
    private static final String EXTRA_TITLE = "video_title";

    SevenVideoPlayer mVideoPlayer;

    public static Intent newIntent(Context context, VideoDetail videoDetail){
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(EXTRA_VIDEO_SOURCE, (Serializable) videoDetail.getSevenVideoSource().getVideoSources());
        intent.putExtra(EXTRA_TITLE, videoDetail.getTitle());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        init();
    }

    private void init(){

        SevenVideoPlayer videoPlayer = (SevenVideoPlayer) findViewById(R.id.video_player);
        mVideoPlayer = (SevenVideoPlayer) videoPlayer.startWindowFullscreen(VideoPlayActivity.this, true, true);
        mVideoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        mVideoPlayer.getBackButton().setVisibility(View.VISIBLE);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("landscape", false)){
            videoPlayer.setLockLand(true);
            videoPlayer.setRotateViewAuto(true);
        }
        mVideoPlayer.getFullscreenButton().setVisibility(View.GONE);
        mVideoPlayer.autoPlayNext(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("play_next", false));
        mVideoPlayer.setIsTouchWiget(true);
        mVideoPlayer.getBackButton().setOnClickListener(v -> onBackPressed());
        mVideoPlayer.setUp(new SevenVideoSourceManager((Map<String, List<SevenVideoSource.VideoUrl>>) getIntent().getSerializableExtra(EXTRA_VIDEO_SOURCE)),
                getIntent().getStringExtra(EXTRA_TITLE));
    }

    @Override
    protected void onPause(){
        super.onPause();
        mVideoPlayer.onVideoPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mVideoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public void onBackPressed(){
        mVideoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
}
