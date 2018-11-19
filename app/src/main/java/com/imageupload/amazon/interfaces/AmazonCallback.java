package com.imageupload.amazon.interfaces;


import com.imageupload.model.ImageBean;

/**
 *
 */

public interface AmazonCallback {

    void uploadSuccess(ImageBean bean);
    void uploadFailed(ImageBean bean);
    void uploadProgress(ImageBean bean);
    void uploadError(Exception e, ImageBean imageBean);
}
