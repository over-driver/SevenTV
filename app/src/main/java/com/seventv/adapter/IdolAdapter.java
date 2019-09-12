package com.seventv.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seventv.SevenTVApplication;
import com.seventv.model.Idol;
import com.seventv.R;
import com.seventv.activity.FilterActivity;
import com.seventv.network.api.SevenAPI;

import java.util.List;

public class IdolAdapter extends BaseQuickAdapter<Idol, BaseViewHolder> {

    private String mCategory;

    public IdolAdapter(int layoutResId, List data, String category) {
        super(layoutResId, data);
        mCategory = category;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Idol item) {
        helper.setText(R.id.name_text_view, item.getName());
        RequestManager rm = Glide.with(mContext);
        RequestBuilder<Drawable> rb;
        if(item.getAvatarUrl() == null){
            rb = rm.load(R.drawable.avatar);
        } else {
            rb = rm.load(item.getAvatarUrl());
        }
        rb.transition(new DrawableTransitionOptions().crossFade(300))
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into((ImageView) helper.getView(R.id.avatar_image_view));

        helper.setOnClickListener(R.id.idol_view, (View v) -> {
                Intent intent = FilterActivity.newIntent(mContext,
                        mCategory,
                        SevenAPI.FILTERS.get(R.string.info_idol),
                        mContext.getResources().getString(R.string.info_idol),
                        item.getCode(), item.getName());
                mContext.startActivity(intent);
            });

        helper.setOnLongClickListener(R.id.idol_view,  (View v) -> {

            boolean hasFavorite = SevenTVApplication.DB_HELPER.hasFavoriteIdol(item, mCategory);
            String[] choices = {"复制名字到剪贴板", "添加到收藏夹"};
            if(hasFavorite){
                choices[1] = "从收藏夹删除";
            }
            (new AlertDialog.Builder(mContext)).setItems(choices, ((dialog1, which) -> {
                if(which == 0){
                    ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setPrimaryClip(ClipData.newPlainText(mContext.getResources().getString(R.string.info_idol), item.getValue()));
                    Toast.makeText(mContext, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                }else if(which == 1){
                    if(hasFavorite){
                        SevenTVApplication.DB_HELPER.deleteFavoriteIdol(item, mCategory);
                    } else {
                        SevenTVApplication.DB_HELPER.insertFavoriteIdol(item, mCategory);
                    }
                }
            })).create().show();
            return true;
        });
    }
}