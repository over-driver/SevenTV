package com.seventv.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.activity.FilterActivity;
import com.seventv.activity.VideoDetailActivity;
import com.seventv.model.Info;
import com.seventv.network.api.SevenAPI;

import java.util.List;

public class InfoAdapter extends BaseQuickAdapter<Info, BaseViewHolder> {

    public InfoAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Info item) {

        String typeString = mContext.getString(item.getType());

        helper.setText(R.id.info_key, typeString);
        helper.setText(R.id.info_value, item.getValue());

        helper.setOnLongClickListener(R.id.info_value, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText(typeString, item.getValue()));
                Toast.makeText(mContext, mContext.getString(R.string.copied_to_clipborad), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        if (item.getCode() != null){
            TextView info_key = (TextView) helper.getView(R.id.info_value);
            info_key.setPaintFlags(info_key.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            info_key.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            helper.setOnClickListener(R.id.info_value, v -> {
                    Intent intent = FilterActivity.newIntent(mContext,
                            ((VideoDetailActivity) mContext).getCategory(),
                            SevenAPI.FILTERS.get(item.getType()),
                            typeString, item.getCode(), item.getValue());
                    mContext.startActivity(intent);
            });
        }

    }
}