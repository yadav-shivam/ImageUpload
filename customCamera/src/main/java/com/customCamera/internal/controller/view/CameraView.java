package com.customCamera.internal.controller.view;

import android.app.Activity;
import android.view.View;

import com.customCamera.internal.configuration.CameraConfiguration;
import com.customCamera.internal.utils.Size;

/**
 *
 */
public interface CameraView {

    Activity getActivity();

    void updateCameraPreview(Size size, View cameraPreview);

    void updateUiForMediaAction(@CameraConfiguration.MediaAction int mediaAction);

    void updateCameraSwitcher(int numberOfCameras);

    void onPhotoTaken();

    void onVideoRecordStart(int width, int height);

    void onVideoRecordStop();

    void releaseCameraPreview();

}
