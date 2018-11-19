package com.customCamera.internal.manager.listener;

import android.support.annotation.RestrictTo;

/**
 * 8/14/16.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface CameraCloseListener<CameraId> {
    void onCameraClosed(CameraId closedCameraId);
}
