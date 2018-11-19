package com.imageupload.amazon;

import android.app.Activity;
import android.content.Context;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.imageupload.amazon.interfaces.AmazonCallback;
import com.imageupload.model.ImageBean;


/**
 * Created by  on 7/9/17.
 */

public class AmazonS3 {
    private Activity mActivity;
    private AmazonCallback amazonCallback;
    private TransferUtility mTransferUtility;


    /*
 *  Initialize activity instance
 */
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /*
    *  Initialize AmazonS3 callback
    */
    public void setCallback(AmazonCallback amazonCallback) {
        this.amazonCallback = amazonCallback;
    }




    public void uploadImage(final ImageBean imageBean) {
        File file = new File(imageBean.getImagePath());
        if(file.exists()) {
            mTransferUtility = AmazonUtils.getTransferUtility(mActivity);
            uploadFileOnAmazon(imageBean,file);

        }
    }

    /**
     * Method to delete the object
     *
     * @param context         context
     * @param fileS3ObjectKey file object to be deleted
     */
    public void deleteFileFromS3(Context context, String fileS3ObjectKey, String versionId) {
        try {
            AmazonS3Client s3client = AmazonUtils.getS3Client(context);
            s3client.deleteObject(new DeleteObjectRequest(AmazonS3Constants.BUCKET, versionId + "_" + fileS3ObjectKey));
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to delete multiple amazon s3 file objects
     *
     * @param context            context
     * @param objectKeyNamesList list of object key names to be deleted from the bucket
     */
    public void deleteMultipleFilesFromS3(Context context, List<String> objectKeyNamesList) {
        try {
            DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(AmazonS3Constants.BUCKET);
            AmazonS3Client s3client = AmazonUtils.getS3Client(context);
            List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();

            for (String objectKeyName :
                    objectKeyNamesList) {
                keys.add(new DeleteObjectsRequest.KeyVersion(objectKeyName));
            }

            multiObjectDeleteRequest.setKeys(keys);

            DeleteObjectsResult delObjRes = s3client.deleteObjects(multiObjectDeleteRequest);
            System.out.format("Successfully deleted all the %s items.\n",
                    delObjRes.getDeletedObjects().size());

        } catch (MultiObjectDeleteException e) {
            for (MultiObjectDeleteException.DeleteError deleteError : e.getErrors()) {
                // Process exception.
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to upload image on amazon.
     * @param imageBean
     * @param file
     */
    private void uploadFileOnAmazon(ImageBean imageBean, File file) {
        TransferObserver observer;
        observer = mTransferUtility.upload(AmazonS3Constants.BUCKET, "android" + String.valueOf(Calendar.getInstance().getTimeInMillis())+".jpg", file, CannedAccessControlList.PublicRead);
        observer.setTransferListener(new UploadListener(imageBean));
        imageBean.setmObserver(observer);
    }


    public void uploadDocFileOnAmazon(ImageBean bean) {
        File pdfFile=new File(bean.getImagePath());
        if (pdfFile.exists()) {
            mTransferUtility = AmazonUtils.getTransferUtility(mActivity);

            TransferObserver observer;
            String docType= bean.getImagePath().substring(bean.getImagePath().lastIndexOf("."));
            observer = mTransferUtility.upload(AmazonS3Constants.BUCKET, "android" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + docType
                    , pdfFile, CannedAccessControlList.PublicRead);
           // observer = mTransferUtility.upload(AmazonS3Constants.BUCKET, "android" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + (bean.getImagePath().contains(".pdf") ? ".pdf" : ".doc"), pdfFile, CannedAccessControlList.PublicRead);

            observer.setTransferListener(new UploadListener(bean));
            bean.setmObserver(observer);
        }
    }


    public void uploadAudioFile(ImageBean bean) {
        File file=new File(bean.getImagePath());
        if (file.exists()) {
            mTransferUtility = AmazonUtils.getTransferUtility(mActivity);
            TransferObserver observer;
            observer = mTransferUtility.upload(AmazonS3Constants.BUCKET, "android" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".mp3" , file, CannedAccessControlList.PublicRead);
            observer.setTransferListener(new UploadListener(bean));
            bean.setmObserver(observer);


        }
    }


    private class UploadListener implements TransferListener {
        private ImageBean imageBean;
        public UploadListener(ImageBean imageBean) {
            this.imageBean = imageBean;
        }

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
                imageBean.setIsSucess("0");
                amazonCallback.uploadError(e,imageBean);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
               int progress = (int) ((double) bytesCurrent * 100 / bytesTotal);
                imageBean.setProgress(progress);
                amazonCallback.uploadProgress(imageBean);
        }


        @Override
        public void onStateChanged(int id, TransferState newState) {
            if (newState == TransferState.COMPLETED) {
                imageBean.setIsSucess("1");
                String url = AmazonS3Constants.AMAZON_SERVER_URL + imageBean.getmObserver().getKey();
                imageBean.setServerUrl(url);
                amazonCallback.uploadSuccess(imageBean);
            }
            else  if (newState == TransferState.FAILED) {
                imageBean.setIsSucess("0");
                amazonCallback.uploadFailed(imageBean);
            }
        }
    }

}
