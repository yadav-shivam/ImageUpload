package com.customCamera.internal.manager;

import android.content.Context;

import com.customCamera.internal.configuration.CameraConfiguration;
import com.customCamera.internal.configuration.ConfigurationProvider;
import com.customCamera.internal.manager.listener.CameraCloseListener;
import com.customCamera.internal.manager.listener.CameraOpenListener;
import com.customCamera.internal.manager.listener.CameraPhotoListener;
import com.customCamera.internal.manager.listener.CameraVideoListener;
import com.customCamera.internal.utils.Size;

import java.io.File;

/**
 * 8/14/16.
 */
public interface CameraManager<CameraId, SurfaceListener> {

    void initializeCameraManager(ConfigurationProvider configurationProvider, Context context);

    void openCamera(CameraId cameraId, CameraOpenListener<CameraId, SurfaceListener> cameraOpenListener);

    void closeCamera(CameraCloseListener<CameraId> cameraCloseListener);

    void takePhoto(File photoFile, CameraPhotoListener cameraPhotoListener);

    void startVideoRecord(File videoFile, CameraVideoListener cameraVideoListener);

    Size getPhotoSizeForQuality(@CameraConfiguration.MediaQuality int mediaQuality);

    void setFlashMode(@CameraConfiguration.FlashMode int flashMode);

    void stopVideoRecord();

    void releaseCameraManager();

    CameraId getCurrentCameraId();

    CameraId getFaceFrontCameraId();

    CameraId getFaceBackCameraId();

    int getNumberOfCameras();

    int getFaceFrontCameraOrientation();

    int getFaceBackCameraOrientation();

    boolean isVideoRecording();
}
