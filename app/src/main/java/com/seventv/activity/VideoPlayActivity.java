package com.seventv.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.seventv.R;
import com.seventv.model.SevenVideoSource;
import com.seventv.network.NetworkBasic;
import com.seventv.view.SevenVideoPlayer;
import com.seventv.model.SevenVideoSourceManager;
import com.seventv.model.VideoDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class VideoPlayActivity extends AppCompatActivity {


    private static final String EXTRA_VIDEO_SOURCE = "video_source";
    private static final String EXTRA_TITLE = "video_title";

    SevenVideoPlayer mVideoPlayer;
    //String mTitle;
    OrientationUtils mOrientationUtils;

    public static Intent newIntent(Context context, VideoDetail videoDetail){
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(EXTRA_VIDEO_SOURCE, (Serializable) videoDetail.getSevenVideoSource().getVideoSources());
        intent.putExtra(EXTRA_TITLE, videoDetail.getTitle());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("VideoPlayActivity","OnCreate");
        setContentView(R.layout.activity_video_play);
        //mTitle = getIntent().getStringExtra(EXTRA_TITLE)
        //mVideoDetail = (Map<String, List<SevenVideoSource.VideoUrl>>) getIntent().getSerializableExtra(EXTRA_VIDEO_SOURCE);
        init();
    }

    private void init(){

        SevenVideoPlayer videoPlayer = (SevenVideoPlayer) findViewById(R.id.video_player);
        mVideoPlayer = (SevenVideoPlayer) videoPlayer.startWindowFullscreen(VideoPlayActivity.this, true, true);
        mVideoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        mVideoPlayer.getBackButton().setVisibility(View.VISIBLE);
        mOrientationUtils = new OrientationUtils(this, mVideoPlayer);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("landscape", false)){
            //Log.d("VIDEO_PLAY","lock");
            //mOrientationUtils.setEnable(false);
            //mOrientationUtils.setRotateWithSystem(false);
            //mVideoPlayer.setRotateViewAuto(false);
            //mVideoPlayer.setLockLand(true);
            videoPlayer.setLockLand(true);
            videoPlayer.setRotateViewAuto(true);
        }
        mVideoPlayer.getFullscreenButton().setVisibility(View.GONE);
        /*
        mVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrientationUtils.resolveByClick();
                mVideoPlayer.startWindowFullscreen(VideoPlayActivity.this, true, true);
            }
        });*/
        //mVideoPlayer.getFullscreenButton().performClick();

        mVideoPlayer.setIsTouchWiget(true);
        mVideoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mVideoPlayer.setUp(new SevenVideoSourceManager((Map<String, List<SevenVideoSource.VideoUrl>>) getIntent().getSerializableExtra(EXTRA_VIDEO_SOURCE)),
                getIntent().getStringExtra(EXTRA_TITLE));

        //mVideoPlayer.startPlayLogic();
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
        if (mOrientationUtils != null)
            mOrientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed(){
        Log.d("VideoPlayActivity", "Back button");
        if (mOrientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            //mVideoPlayer.getFullscreenButton().performClick();
            //return;
        }
        mVideoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }



}
