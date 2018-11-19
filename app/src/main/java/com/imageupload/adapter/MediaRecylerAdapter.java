package com.imageupload.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.imageupload.interfaces.OnRecyclerViewItemClickListener;
import com.imageupload.R;


/**
 */

public class MediaRecylerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int pictureViewType = 1;
    private final int documentViewType = 2;
    private final int linkViewType = 3;



    private List<String> mediaList;
    private Context context;

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public MediaRecylerAdapter(List<String> mediaList, Context context) {
        this.mediaList = mediaList;
        this.context = context;
    }


    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case pictureViewType:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_picture, parent, false);

                return new PictureViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PictureViewHolder) {
            ((PictureViewHolder) holder).bindData(mediaList.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @Override
    public int getItemViewType(int position) {
            return pictureViewType;
    }


    class PictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.iv_post_image)
        ImageView ivPostImage;


        public PictureViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (onRecyclerViewItemClickListener != null) {
                onRecyclerViewItemClickListener.onRecyclerViewItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (onRecyclerViewItemClickListener != null) {
                onRecyclerViewItemClickListener.onRecyclerViewItemLongClick(view, getAdapterPosition());
            }
            return true;
        }

        public void bindData(String url) {
                Glide.with(context).load(url).
                        apply(RequestOptions.placeholderOf(R.color.colorTextBlack)).into(ivPostImage);

        }
    }


}
