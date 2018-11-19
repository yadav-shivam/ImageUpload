package com.imageupload.interfaces;

import android.view.View;

/**
 * Created by appinventiv on 12/1/18.
 */

public interface OnRecyclerViewItemClickListener {

    void onRecyclerViewItemClick(View view, int position);

    void onRecyclerViewItemLongClick(View view, int position);

}
