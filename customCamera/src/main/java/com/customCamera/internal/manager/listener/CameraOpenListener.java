package com.customCamera.internal.manager.listener;

import android.support.annotation.RestrictTo;

import com.customCamera.internal.utils.Size;

/**
 * 8/14/16.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface CameraOpenListener<CameraId, SurfaceListener> {
    void onCameraOpened(CameraId openedCameraId, Size previewSize, SurfaceListener surfaceListener);

    void onCameraOpenError();
}
