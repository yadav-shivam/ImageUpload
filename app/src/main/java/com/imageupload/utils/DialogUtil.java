package com.imageupload.utils;

import android.app.ProgressDialog;
import android.content.Context;



/**
 * Created by shivam on 15/2/18.
 */

public class DialogUtil {

    private static final String TAG = "DialogUtil";
    private static DialogUtil INSTANCE;
    private ProgressDialog progDialog;

    private DialogUtil() {

    }

    public static DialogUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (DialogUtil.class) {
                if (INSTANCE == null)
                    INSTANCE = new DialogUtil();
            }
        }
        return INSTANCE;
    }


    public void showProgressDialogWithMessage(Context context, int titleResId, int messageResId) {
        String title = null;
        if (titleResId > 0) {
            title = context.getString(titleResId);
        }
        String message = null;
        if (messageResId > 0) {
            message = context.getString(messageResId);
        }
        showProgressDialog(context, title, message);
    }

    private void showProgressDialog(Context context, String title, String message) {
        progDialog = new ProgressDialog(context);
        progDialog.setMessage(message);
        progDialog.setTitle(title);
        progDialog.setCancelable(false);
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.show();
    }


    public void dismissProgressDialog() {

        if (progDialog != null && progDialog.isShowing())
            progDialog.dismiss();
    }


}
