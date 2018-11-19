package com.customCamera.internal.manager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Arpit Gandhi
 */

public class CameraOutputModel implements Parcelable {
    private int type;
    private String path;

    public CameraOutputModel(int type, String path) {
        this.type = type;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public int getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.path);
    }

    protected CameraOutputModel(Parcel in) {
        this.type = in.readInt();
        this.path = in.readString();
    }

    public static final Creator<CameraOutputModel> CREATOR = new Creator<CameraOutputModel>() {
        @Override
        public CameraOutputModel createFromParcel(Parcel source) {
            return new CameraOutputModel(source);
        }

        @Override
        public CameraOutputModel[] newArray(int size) {
            return new CameraOutputModel[size];
        }
    };
}
