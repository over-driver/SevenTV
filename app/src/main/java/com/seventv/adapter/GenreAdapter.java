package com.seventv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.activity.FilterActivity;
import com.seventv.activity.VideoDetailActivity;
import com.seventv.model.Genre;
import com.seventv.network.api.SevenAPI;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.FlexViewHolder> {

    private List<Genre> mGenres;
    private Context mContext;

    public GenreAdapter(List<Genre> genres, Context context) {
        mGenres = genres;
        mContext = context;
    }

    @Override
    public FlexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_genre, parent, false);

        return new FlexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FlexViewHolder holder, final int position) {
        Genre item = mGenres.get(position);

        holder.mTextView.setText(item.getValue());

        if (item.getCode() != null){
            holder.mTextView.setOnClickListener(view -> {
                    Intent intent = FilterActivity.newIntent(mContext,
                            ((VideoDetailActivity) mContext).getCategory(),
                            SevenAPI.FILTERS.get(R.string.info_genre),
                            //SevenTVApplication.myGetString(R.string.info_genre),
                            mContext.getResources().getString(R.string.info_genre),
                            item.getCode(), item.getValue());
                    mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    public void setNewData(List<Genre> genres){
        mGenres = genres;
        notifyDataSetChanged();
    }

    static class FlexViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public FlexViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.genre_text_view);
        }
    }
}
