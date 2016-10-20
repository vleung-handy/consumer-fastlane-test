package com.handybook.handybook.logger.handylogger.model;

import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.core.BaseApplication;

public class EventSuperPropertiesBase
{
    private static final String ANDROID = "android";

    @SerializedName("product_type")
    private String mProduct;
    @SerializedName("platform")
    private String mPlatform;
    @SerializedName("os_version")
    private String mOsVersion;
    @SerializedName("app_version")
    private String mAppVersion;
    @SerializedName("device_id")
    private String mDeviceId;
    @SerializedName("device_model")
    private String mDeviceModel;

    public EventSuperPropertiesBase()
    {
        mProduct = "consumer";
        mPlatform = ANDROID;
        mOsVersion = Build.VERSION.RELEASE;
        mAppVersion = BuildConfig.VERSION_NAME;
        mDeviceId = BaseApplication.getDeviceId();
        mDeviceModel = BaseApplication.getDeviceModel();
    }
}
