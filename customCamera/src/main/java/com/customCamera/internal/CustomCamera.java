package com.customCamera.internal;

import android.app.Activity;
import android.content.Intent;

import com.customCamera.internal.configuration.CameraConfiguration;
import com.customCamera.internal.ui.camera.Camera1Activity;
import com.customCamera.internal.ui.camera2.Camera2Activity;
import com.customCamera.internal.utils.CameraHelper;
import com.customCamera.internal.utils.MediaConstant;

/**
 * Sandrios Camera Builder Class
 */
public class CustomCamera {

    private static CustomCamera mInstance = null;
    private static Activity mActivity;
    private int mediaAction = CameraConfiguration.MEDIA_ACTION_BOTH;
    private boolean showPicker = true;
    private boolean autoRecord = false;
    private int type = 501;
    private boolean enableImageCrop = false;
    private long videoSize = -1;

    public static CustomCamera with(Activity activity) {
        if (mInstance == null) {
            mInstance = new CustomCamera();
        }
        mActivity = activity;
        return mInstance;
    }

    public CustomCamera setShowPickerType(int type) {
        this.type = type;
        return mInstance;
    }

    public CustomCamera setShowPicker(boolean showPicker) {
        this.showPicker = showPicker;
        return mInstance;
    }

    public CustomCamera setMediaAction(int mediaAction) {
        this.mediaAction = mediaAction;
        return mInstance;
    }

    public CustomCamera enableImageCropping(boolean enableImageCrop) {
        this.enableImageCrop = enableImageCrop;
        return mInstance;
    }

    @SuppressWarnings("SameParameterValue")
    public CustomCamera setVideoFileSize(int fileSize) {
        this.videoSize = fileSize;
        return mInstance;
    }

    /**
     * Only works if Media Action is set to Video
     */
    public CustomCamera setAutoRecord() {
        autoRecord = true;
        return mInstance;
    }

//    private void launchCamera(final CameraCallback cameraCallback) {
//        Dexter.withActivity(mActivity)
//                .withPermissions(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.RECORD_AUDIO)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
////                        launchIntent();
//                        if (CameraHelper.hasCamera(mActivity)) {
//                            Intent cameraIntent;
//                            if (CameraHelper.hasCamera2(mActivity)) {
//                                cameraIntent = new Intent(mActivity, Camera2Activity.class);
//                            } else {
//                                cameraIntent = new Intent(mActivity, Camera1Activity.class);
//                            }
//                            cameraIntent.putExtra(CameraConfiguration.Arguments.SHOW_PICKER, showPicker);
//                            cameraIntent.putExtra(CameraConfiguration.Arguments.PICKER_TYPE, type);
//                            cameraIntent.putExtra(CameraConfiguration.Arguments.MEDIA_ACTION, mediaAction);
//                            cameraIntent.putExtra(CameraConfiguration.Arguments.ENABLE_CROP, enableImageCrop);
//                            cameraIntent.putExtra(CameraConfiguration.Arguments.AUTO_RECORD, autoRecord);
//
//                            if (videoSize > 0) {
//                                cameraIntent.putExtra(CameraConfiguration.Arguments.VIDEO_FILE_SIZE, videoSize * 1024 * 1024);
//                            }
//                            mActivity.startActivity(cameraIntent);
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//
//                    }
//                }).check();
//
//        SandriosBus.getBus()
//                .toObserverable()
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) throws Exception {
//                        if (o instanceof CameraOutputModel) {
//                            CameraOutputModel outputModel = (CameraOutputModel) o;
//                            if (cameraCallback != null) {
//                                cameraCallback.onComplete(outputModel);
//                                mInstance = null;
//                            }
//                            SandriosBus.complete();
//                        }
//                    }
//                });
//    }

    public void launchIntent() {
//        launchCamera(new CameraCallback() {
//            @Override
//            public void onComplete(CameraOutputModel cameraOutputModel) {
//                Log.e(cameraOutputModel.toString(),cameraOutputModel.toString());
//            }
//        });
        if (CameraHelper.hasCamera(mActivity)) {
            Intent cameraIntent;
            if (CameraHelper.hasCamera2(mActivity))
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                cameraIntent = new Intent(mActivity, Camera2Activity.class);
            else
                cameraIntent = new Intent(mActivity, Camera1Activity.class);
            cameraIntent.putExtra(CameraConfiguration.Arguments.SHOW_PICKER, showPicker);
            cameraIntent.putExtra(CameraConfiguration.Arguments.PICKER_TYPE, type);
            cameraIntent.putExtra(CameraConfiguration.Arguments.MEDIA_ACTION, mediaAction);
            cameraIntent.putExtra(CameraConfiguration.Arguments.ENABLE_CROP, enableImageCrop);
            cameraIntent.putExtra(CameraConfiguration.Arguments.AUTO_RECORD, autoRecord);

            if (videoSize > 0) {
                cameraIntent.putExtra(CameraConfiguration.Arguments.VIDEO_FILE_SIZE, videoSize * 1024 * 1024);
            }
            mActivity.startActivityForResult(cameraIntent, MediaConstant.MEDIA_REQUEST_CODE);
        }
    }

//    public interface CameraCallback {
//        void onComplete(CameraOutputModel cameraOutputModel);
//    }

    public class MediaType {
        public static final int PHOTO = 0;
        public static final int VIDEO = 1;
    }
}
