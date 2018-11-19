package com.imageupload.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.imageupload.R;
import com.imageupload.utils.TouchImageView;

public class MediaPreviewActivity extends AppCompatActivity {


    @BindView(R.id.iv_preview)
    TouchImageView ivPreview;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_preview);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            content = getIntent().getStringExtra("image");
            Glide.with(this).load(content).into(ivPreview);
        }

    }


    @Override
    public void onBackPressed() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        onBackPressed();
    }
}

