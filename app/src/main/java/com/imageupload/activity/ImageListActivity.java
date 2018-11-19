package com.imageupload.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.customCamera.internal.CustomCamera;
import com.customCamera.internal.configuration.CameraConfiguration;
import com.customCamera.internal.manager.CameraOutputModel;
import com.customCamera.internal.utils.MediaConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.imageupload.interfaces.OnRecyclerViewItemClickListener;
import com.imageupload.R;
import com.imageupload.adapter.MediaRecylerAdapter;
import com.imageupload.amazon.AmazonS3;
import com.imageupload.amazon.interfaces.AmazonCallback;
import com.imageupload.model.ImageBean;
import com.imageupload.utils.AppSharedPrefs;
import com.imageupload.utils.AppUtils;
import pub.devrel.easypermissions.EasyPermissions;

public class ImageListActivity extends AppCompatActivity implements AmazonCallback {
    private final String[] CAMERA_WITH_STORAGE_AND_RECORDER = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    private final int CAMERA_PERMISSION_BOTH = 101;
    private final int CAMERA_PERMISSION = 102;

    @BindView(R.id.rv_media)
    RecyclerView rvMedia;
    @BindView(R.id.tv_no_media)
    TextView tvNoMedia;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.ll_progress)
    LinearLayout llProgress;

    private Activity mActivity;
    private AmazonS3 mAmazonS3;
    private ArrayList<String> mediaList;
    private MediaRecylerAdapter mediaRecylerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        ButterKnife.bind(this);
        mActivity = this;
        initializeAmazonS3();
        setUpRecyclerView();
    }


    /**
     * Initialize AmazonS3 instance
     */
    private void initializeAmazonS3() {
        mAmazonS3 = new AmazonS3();
        mAmazonS3.setActivity(mActivity);
        mAmazonS3.setCallback(this);
    }

    @OnClick(R.id.fab_add)
    public void onViewClicked() {
        if (EasyPermissions.hasPermissions(mActivity, CAMERA_WITH_STORAGE_AND_RECORDER))
            openBothImagePicker();
        else
            EasyPermissions.requestPermissions(mActivity, getString(R.string.camera_storage_record_permission), CAMERA_PERMISSION_BOTH, CAMERA_WITH_STORAGE_AND_RECORDER);

    }

    /**
     * method to launch camera with picker
     */
    private void openBothImagePicker() {
        CustomCamera
                .with(mActivity)
                .setShowPicker(true)
                .setShowPickerType(CameraConfiguration.PHOTO)
                .setMediaAction(CameraConfiguration.MEDIA_ACTION_PHOTO)
                .enableImageCropping(true)
                .launchIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case MediaConstant.MEDIA_REQUEST_CODE:
                    final CameraOutputModel model = data.getParcelableExtra(MediaConstant.MEDIA_RESULT);
                    if (model != null) {
                        File file = new File(model.getPath());
                        if (file.exists()) {
                            if (model.getType() == CustomCamera.MediaType.PHOTO) {
                                Uri uri = Uri.fromFile(file);
                                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                                    startUpload(model.getPath(), String.valueOf(System.currentTimeMillis()));
                                }else {
                                    AppUtils.getInstance().showToast(mActivity,"No internet connection.");
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }


    /**
     * method to start upload file on s3
     * @param path
     * @param id
     */
    private void startUpload(String path, String id) {
        ImageBean bean = addDataInBean(path, id);
        mAmazonS3.uploadImage(bean);

        llProgress.setVisibility(View.VISIBLE);
    }

    /**
     * method to add data in bean
     * @param path
     * @param id
     * @return
     */
    private ImageBean addDataInBean(String path, String id) {
        ImageBean bean = new ImageBean();
        bean.setId(id);
        bean.setName("abc");
        bean.setImagePath(path);
        return bean;
    }

    @Override
    public void uploadSuccess(ImageBean bean) {
        if (!isFinishing()) {
            String images = AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.IMAGE, "");
            StringBuilder imagesSB = new StringBuilder(images);
            if (imagesSB.length() != 0)
                imagesSB.append(",");

            imagesSB.append(bean.getImagePath());

            AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.IMAGE, imagesSB.toString());

            tvNoMedia.setVisibility(View.GONE);
            mediaList.add(bean.getImagePath());
            mediaRecylerAdapter.notifyItemInserted(mediaList.size()-1);


            llProgress.setVisibility(View.GONE);

        }
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        AppUtils.getInstance().showSnackBar(findViewById(android.R.id.content), "Upload failed.");
        llProgress.setVisibility(View.GONE);
    }

    @Override
    public void uploadProgress(ImageBean bean) {

    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {
        llProgress.setVisibility(View.GONE);
        AppUtils.getInstance().showSnackBar(findViewById(android.R.id.content), "Upload failed.");
    }


    /**
     * method to setup recycler view
     */
    private void setUpRecyclerView() {
        mediaList=new ArrayList<>();
        String images = AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.IMAGE, "");
        if (!TextUtils.isEmpty(images)) {
            String[] imagesArray = images.split(",");
            mediaList.addAll(Arrays.asList(imagesArray));
        }
        mediaRecylerAdapter = new MediaRecylerAdapter(mediaList, mActivity);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);

        rvMedia.setLayoutManager(gridLayoutManager);
        rvMedia.setAdapter(mediaRecylerAdapter);

        tvNoMedia.setVisibility(mediaList.size()==0?View.VISIBLE:View.GONE);

        mediaRecylerAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(View view, int position) {
                Intent mediaIntent=new Intent(mActivity,MediaPreviewActivity.class);
                mediaIntent.putExtra("image",mediaList.get(position));
                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions
                            .makeSceneTransitionAnimation(mActivity, view, "image_preview");
                    startActivity(mediaIntent, options.toBundle());
                } else {
                    startActivity(mediaIntent);
                }
            }

            @Override
            public void onRecyclerViewItemLongClick(View view, int position) {

            }
        });
    }


}
