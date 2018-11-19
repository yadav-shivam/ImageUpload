package com.customCamera.internal.manager.listener;

import com.customCamera.internal.utils.Size;

import java.io.File;

/**
 * 8/14/16.
 */
public interface CameraVideoListener {
    void onVideoRecordStarted(Size videoSize);

    void onVideoRecordStopped(File videoFile);

    void onVideoRecordError();
}
