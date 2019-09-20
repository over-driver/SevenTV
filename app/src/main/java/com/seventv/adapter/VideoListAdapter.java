package com.seventv.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seventv.R;
import com.seventv.model.Video;
import com.seventv.activity.VideoDetailActivity;
import java.util.List;

public class VideoListAdapter extends BaseQuickAdapter<Video, BaseViewHolder> {

    private VideoView mVideoViewPlaying;

    public VideoListAdapter(int layoutResId, List data, Context context, String category){
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, final Video item) {
        helper.setText(R.id.video_title, item.getTitle());
        helper.setText(R.id.upload_date, item.getDate());
        helper.setText(R.id.id_text_view, item.getId());

        Glide.with(mContext)
                .load(item.getThumbnailUrl())
                .placeholder(R.drawable.placeholder)
                .transition(new DrawableTransitionOptions().crossFade(300))
                .into((ImageView) helper.getView(R.id.video_thumbnail));

        final VideoView videoView = helper.getView(R.id.video_preview);
        videoView.stopPlayback();
        videoView.setVisibility(View.GONE);

        helper.setOnLongClickListener(R.id.video_card_view, v -> {
                if (item.getPreviewUrl() == null){
                    return false;
                }
                if (mVideoViewPlaying != null){
                    mVideoViewPlaying.stopPlayback();
                    mVideoViewPlaying.setVisibility(View.GONE);
                }
                mVideoViewPlaying = videoView;
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(item.getPreviewUrl()));
                videoView.setOnCompletionListener(mp -> {
                        mVideoViewPlaying.stopPlayback();
                        mVideoViewPlaying.setVisibility(View.GONE);
                        mVideoViewPlaying = null;
                });
                videoView.start();
                return true;
        });

        helper.setOnClickListener(R.id.video_card_view, v -> {
                Intent intent = VideoDetailActivity.newIntent(mContext, item);
                mContext.startActivity(intent);
        });

    }

}
