package com.microsoft.applicationinsights.library;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.app.Application;
import android.os.Build;

import com.microsoft.applicationinsights.logging.InternalLogging;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SyncUtil implements ComponentCallbacks, ComponentCallbacks2 {

    /**
     * The singleton INSTANCE of this class
     */
    private static SyncUtil instance;

    /**
     * The tag for logging
     */
    private static final String TAG = "SyncUtil";

    /**
     * @return the INSTANCE of autocollection event tracking or null if not yet initialized
     */
    protected static SyncUtil getInstance() {
        if (SyncUtil.instance == null) {
            SyncUtil.instance = new SyncUtil();
        }

        return SyncUtil.instance;
    }


    private SyncUtil() {
    }

    protected void start(Application application) {
        if(application != null) {
            application.registerComponentCallbacks(SyncUtil.instance);
            InternalLogging.info(TAG, "Started listenng to componentcallbacks to trigger sync");
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            InternalLogging.info(TAG, "UI of the app is hidden");
            InternalLogging.info(TAG, "Syncing data");
            Channel.getInstance().synchronize();
        } else if (level == TRIM_MEMORY_RUNNING_LOW || level == TRIM_MEMORY_RUNNING_LOW) {
            InternalLogging.info(TAG, "Memory running low, syncing data");
            Channel.getInstance().synchronize();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // unused but required to implement ComponentCallbacks
    }

    @Override
    public void onLowMemory() {
        // unused but required to implement ComponentCallbacks
        InternalLogging.warn(TAG, "Received onLowMemory()-Callback, persisting data");
        Channel.getInstance().synchronize();
    }
}
