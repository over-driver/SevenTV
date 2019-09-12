package com.seventv.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventv.R;
import com.seventv.model.SevenVideoSource;
import com.seventv.model.SevenVideoSourceManager;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.List;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

public class SevenVideoPlayer extends StandardGSYVideoPlayer {

    private final static int SWITCH_RESOLUTION = 0;
    private final static int SWITCH_SOURCE = 1;
    private final static int SWITCH_PART = 2;

    private final static long SKIP_L = 10 * 60 * 1000;
    private final static long SKIP_M = 5 * 60 * 1000;
    private final static long SKIP_S = 1 * 60 * 1000;

    private TextView mSwitchResolution;
    private TextView mSwitchSource;
    private TextView mSwitchPart;
    private View mForward;
    private View mBackward;
    private ImageView mForwardL;
    private ImageView mForwardM;
    private ImageView mForwardS;
    private ImageView mBackwardL;
    private ImageView mBackwardM;
    private ImageView mBackwardS;

    private SevenVideoSourceManager mSevenVideoSourceManager;

    private String mSource = null;
    private String mPart = null;
    private String mResolution = null;
    private String mMyUrl = "";

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public SevenVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SevenVideoPlayer(Context context) {
        super(context);
    }

    public SevenVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }

    private void initView() {

        mSwitchPart = (TextView) findViewById(R.id.switch_part);
        mSwitchResolution = (TextView) findViewById(R.id.switch_resolution);
        mSwitchSource = (TextView) findViewById(R.id.switch_source);

        mForward = (View) findViewById(R.id.forward_layout);
        mBackward = (View) findViewById(R.id.backward_layout);

        mForwardL = (ImageView) findViewById(R.id.forward_l);
        mForwardM = (ImageView) findViewById(R.id.forward_m);
        mForwardS = (ImageView) findViewById(R.id.forward_s);
        mBackwardL = (ImageView) findViewById(R.id.backward_l);
        mBackwardM = (ImageView) findViewById(R.id.backward_m);
        mBackwardS = (ImageView) findViewById(R.id.backward_s);

        mForwardL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                safeSeekTo(getCurrentPositionWhenPlaying() + SKIP_L);
            }
        });

        mForwardM.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                safeSeekTo(getCurrentPositionWhenPlaying() + SKIP_M);
            }
        });

        mForwardS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                safeSeekTo(getCurrentPositionWhenPlaying() + SKIP_S);
            }
        });

        mBackwardL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                safeSeekTo(getCurrentPositionWhenPlaying() - SKIP_L);
            }
        });

        mBackwardM.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                safeSeekTo(getCurrentPositionWhenPlaying() - SKIP_M);
            }
        });

        mBackwardS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                safeSeekTo(getCurrentPositionWhenPlaying() - SKIP_S);
            }
        });


        mSwitchPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSwitchDialog(SWITCH_PART);
            }
        });

        mSwitchSource.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSwitchDialog(SWITCH_SOURCE);
            }
        });

        mSwitchResolution.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSwitchDialog(SWITCH_RESOLUTION);
            }
        });

        //updateText();
        //切换视频清晰度
        /*
        mSwitchSize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSwitchDialog();
            }
        });
        */
    }

    private void safeSeekTo(long position){
        if (position < 0){
            position = 1;
        }
        if (position > getDuration()){
            position = getDuration() - 1;
        }
        Log.d("Position", ""+position);
        seekTo(position);
    }

    private void updateText(){
        if(mSource == null){
            mSwitchSource.setText("");
        }else{
            mSwitchSource.setText(SevenVideoSource.SOURCE_NAME.get(mSource));
        }
        if(mResolution == null){
            mSwitchResolution.setText("");
        } else {
            mSwitchResolution.setText(mResolution);
        }
        if(mPart == null){
            mSwitchPart.setText("");
        } else {
            mSwitchPart.setText("Part: " + Integer.toString(Integer.parseInt(mPart) + 1) + "/" + Integer.toString(mSevenVideoSourceManager.numPart(mSource)));
        }
    }

    /*
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param title         title
     * @return
     */

    /*
    public boolean setUp(List<SwitchVideoModel> url, boolean cacheWithPlay, String title) {
        mUrlList = url;
        return setUp(url.get(mSourcePosition).getUrl(), cacheWithPlay, title);
    }
    */

    @Override
    protected void onClickUiToggle() {
        super.onClickUiToggle();
        mForward.setVisibility(mBottomContainer.getVisibility());
        mBackward.setVisibility(mBottomContainer.getVisibility());
    }

    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        mForward.setVisibility(INVISIBLE);
        mBackward.setVisibility(INVISIBLE);
    }

    private boolean chooseKernel(String source){
        if (source.equals(mSource)){
            return mCache;
        }
        if (source.equals(SevenVideoSource.AVGLE)) {
            PlayerFactory.setPlayManager(IjkPlayerManager.class);
            CacheFactory.setCacheManager(ProxyCacheManager.class);
            return false;
            //mVideoPlayer.setUp(realUrl, false, "test video");
        } else if (source.equals(SevenVideoSource.VERYSTREAM)){
            PlayerFactory.setPlayManager(Exo2PlayerManager.class);
            CacheFactory.setCacheManager(ProxyCacheManager.class);
            return false;
            //mVideoPlayer.setUp(realUrl, false, "test video");
        } else {
            PlayerFactory.setPlayManager(Exo2PlayerManager.class);
            CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
            return true;
            //mVideoPlayer.setUp(realUrl, true, "test video");
        }

    }

    public boolean setUp(SevenVideoSourceManager sevenVideoSourceManager, String title) {
        boolean flag = setUp("", true, title);
        mSevenVideoSourceManager = sevenVideoSourceManager;

        mTitle = title;
        mSevenVideoSourceManager.setOnUrlReadyListener(new SevenVideoSourceManager.OnUrlReadyListener() {
            @Override
            public void onUrlReady(String source, String part, String resolution) {
                String url = mSevenVideoSourceManager.getUrl(source, part, resolution);
                Log.d("VIDEO URL", "url = " + url);
                Log.d("VIDEO URL", "new: " +source+part+resolution);
                Log.d("VIDEO URL", "old: " +mSource+mPart+mResolution);
                boolean isUpdate = false;
                if(mSource == null){
                    isUpdate = true;
                    boolean cache = chooseKernel(source);
                    setUp(url, cache, mCachePath, mTitle);
                    startPlayLogic();
                } else { //if (mCurrentState == GSYVideoPlayer.CURRENT_STATE_PLAYING || mCurrentState == GSYVideoPlayer.CURRENT_STATE_PAUSE){
                    isUpdate = true;
                    onVideoPause();
                    final long currentPosition = mCurrentPosition;
                    getGSYVideoManager().releaseMediaPlayer();
                    cancelProgressTimer();
                    hideAllWidget();
                    boolean cache = chooseKernel(source);
                    boolean restorePosition = mPart.equals(part) && mSource.equals(source);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setUp(url, cache, mCachePath, mTitle);
                            if(restorePosition){
                                setSeekOnStart(currentPosition);
                            }
                            startPlayLogic();
                            cancelProgressTimer();
                            hideAllWidget();
                        }
                    }, 500);

                }
                if(isUpdate){
                    mMyUrl = url;
                    mSource = source;
                    mPart = part;
                    mResolution = resolution;
                    updateText();
                }
                mSwitchResolution.setClickable(true);
                mSwitchSource.setClickable(true);
                mSwitchPart.setClickable(true);
            }
        });
        requestVideoUrl(mSevenVideoSourceManager.getPreferredSource(), "0", null);
        return flag;
        //mUrlList = url;
        //return setUp(url.get(mSourcePosition).getUrl(), cacheWithPlay, title);
    }

    private void requestVideoUrl(String source, String part, String resolution){
        // disable ui
        mSwitchResolution.setClickable(false);
        mSwitchSource.setClickable(false);
        mSwitchPart.setClickable(false);
        mSevenVideoSourceManager.requestVideoUrl(source, part, resolution);
    }

    /*
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param title         title
     * @return
     */
    /*
    public boolean setUp(List<SwitchVideoModel> url, boolean cacheWithPlay, File cachePath, String title) {
        mUrlList = url;
        return setUp(url.get(mSourcePosition).getUrl(), cacheWithPlay, cachePath, title);
    }*/

    @Override
    public int getLayoutId() {
        return R.layout.video_player;
    }

    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        SevenVideoPlayer sampleVideo = (SevenVideoPlayer) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mSource = mSource;
        sampleVideo.mResolution = mResolution;
        sampleVideo.mPart = mPart;
        sampleVideo.mSevenVideoSourceManager = mSevenVideoSourceManager;
        sampleVideo.updateText();
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        return sampleVideo;
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            SevenVideoPlayer sampleVideo = (SevenVideoPlayer) gsyVideoPlayer;
            mSevenVideoSourceManager = sampleVideo.mSevenVideoSourceManager;
            mSource = sampleVideo.mSource;
            mPart = sampleVideo.mPart;
            mResolution = sampleVideo.mResolution;

            setUp(mMyUrl, mCache, mCachePath, mTitle);

            updateText();
        }
    }

    /**
     * 弹出切换清晰度
     */
    private void showSwitchDialog(final int type) {

        /*
        if (!mHadPlay) {
            return;
        }*/

        List<SwitchVideoModel> data = null;
        String oldData = null;
        switch (type){
            case SWITCH_RESOLUTION:
                data = mSevenVideoSourceManager.getAvailableResolution(mSource, mPart);
                oldData = mResolution;
                break;
            case SWITCH_SOURCE:
                data = mSevenVideoSourceManager.getAvailableSource();
                oldData = mSource;
                break;
            case SWITCH_PART:
                data = mSevenVideoSourceManager.getAvailablePart(mSource);
                oldData = mPart;
                break;
        }
        final List<SwitchVideoModel> dataFinal = data;
        final String oldDataFinal = oldData;

        SwitchVideoTypeDialog switchVideoTypeDialog = new SwitchVideoTypeDialog(getContext());
        switchVideoTypeDialog.initList(data, new SwitchVideoTypeDialog.OnListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String newData = dataFinal.get(position).getData();
                if(!newData.equals(oldDataFinal)){
                    switch (type){
                        case SWITCH_RESOLUTION:
                            requestVideoUrl(mSource, mPart, newData);
                            break;
                        case SWITCH_SOURCE:
                            requestVideoUrl(newData, "0", null);
                            break;
                        case SWITCH_PART:
                            requestVideoUrl(mSource, newData, mResolution);
                            break;
                    }
                }
            }
        });
        switchVideoTypeDialog.show();
    }
    /*
    @Override
    public void onCompletion(){
        releaseNetWorkState();
        if(Integer.parseInt(mPart) < mSevenVideoSourceManager.numPart(mSource) - 1){
            return;
        }
        super.onCompletion();
    }

    @Override
    public void onAutoCompletion(){
        if(playNext()){
            return;
        }
        super.onAutoCompletion();
    }

    private boolean playNext(){
        int intPart = Integer.parseInt(mPart);
        if(intPart < mSevenVideoSourceManager.numPart(mSource) - 1){
            String part = Integer.toString(intPart + 1);
            requestVideoUrl(mSource, part, mResolution);
            return true;
        }
        return false;
    }
    */
}
