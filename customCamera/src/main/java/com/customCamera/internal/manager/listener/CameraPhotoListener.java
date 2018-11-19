package com.customCamera.internal.manager.listener;

import android.support.annotation.RestrictTo;

import java.io.File;

/**
 * 8/14/16.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface CameraPhotoListener {
    void onPhotoTaken(File photoFile);

    void onPhotoTakeError();
}
