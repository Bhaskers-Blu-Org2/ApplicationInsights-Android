package com.microsoft.applicationinsights.library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.microsoft.applicationinsights.library.config.ISessionConfig;
import com.microsoft.applicationinsights.logging.InternalLogging;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The public API for auto collecting application insights telemetry.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class LifeCycleTracking implements Application.ActivityLifecycleCallbacks {

    /**
     * The activity counter
     */
    protected final AtomicInteger activityCount;

    /**
     * The configuration for tracking sessions
     */
    protected ISessionConfig config;

    /**
     * The timestamp of the last activity
     */
    protected final AtomicLong lastBackground;

    /**
     * The telemetryContext which is needed to renew a session
     */
    protected TelemetryContext telemetryContext;

    /**
     * Volatile boolean for double checked synchronize block
     */
    private static volatile boolean isLoaded = false;

    /**
     * Synchronization LOCK for setting static context
     */
    private static final Object LOCK = new Object();

    /**
     * The singleton INSTANCE of this class
     */
    private static LifeCycleTracking instance;

    /**
     * The tag for logging
     */
    private static final String TAG = "LifeCycleTracking";

    /**
     * A flag which determines whether auto page view tracking has been enabled or not.
     */
    private static boolean autoPageViewsEnabled;

    /**
     * A flag which determines whether session management has been enabled or not.
     */
    private static boolean autoSessionManagementEnabled;

    /**
     * Create a new INSTANCE of the lifecycle event tracking
     *
     * @param config the session configuration for session tracking
     * @param telemetryContext the context, which is needed to renew sessions
     */
    protected LifeCycleTracking(ISessionConfig config, TelemetryContext telemetryContext) {
        this.activityCount = new AtomicInteger(0);
        this.lastBackground = new AtomicLong(this.getTime());
        this.config = config;
        this.telemetryContext = telemetryContext;
    }

    /**
     * Initialize the INSTANCE of lifecycle event tracking.
     *
     * @param telemetryContext the context, which is needed to renew sessions
     * @param config the session configuration for session tracking
     */
    protected static void initialize(TelemetryContext telemetryContext, ISessionConfig config) {
        // note: isPersistenceLoaded must be volatile for the double-checked LOCK to work
        if (!LifeCycleTracking.isLoaded) {
            synchronized (LifeCycleTracking.LOCK) {
                if (!LifeCycleTracking.isLoaded) {
                    LifeCycleTracking.isLoaded = true;
                    LifeCycleTracking.instance = new LifeCycleTracking(config, telemetryContext);
                }
            }
        }
    }

    /**
     * @return the INSTANCE of lifecycle event tracking or null if not yet initialized
     */
    protected static LifeCycleTracking getInstance() {
        if (LifeCycleTracking.instance == null) {
            InternalLogging.error(TAG, "getInstance was called before initialization");
        }

        return LifeCycleTracking.instance;
    }

    /**
     * Enables lifecycle event tracking for the provided application
     *
     * @param application the application object
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void registerActivityLifecycleCallbacks(Application application) {
        if(!autoPageViewsEnabled && !autoSessionManagementEnabled){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                application.registerActivityLifecycleCallbacks(LifeCycleTracking.getInstance());
            }
        }
    }

    /**
     * Enables lifecycle event tracking for the provided application
     *
     * @param application the application object
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void unregisterActivityLifecycleCallbacks(Application application) {
        if(autoPageViewsEnabled ^ autoSessionManagementEnabled){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                application.unregisterActivityLifecycleCallbacks(LifeCycleTracking.getInstance());
            }
        }
    }

    /**
     * Enables page view event tracking for the provided application
     *
     * @param application the application object
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void registerPageViewCallbacks(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            synchronized (LifeCycleTracking.LOCK) {
                registerActivityLifecycleCallbacks(application);
                autoPageViewsEnabled = true;
            }
        }
    }

    /**
     * Disables page view event tracking for the provided application
     *
     * @param application the application object
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void unregisterPageViewCallbacks(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            synchronized (LifeCycleTracking.LOCK) {
                unregisterActivityLifecycleCallbacks(application);
                autoPageViewsEnabled = false;
            }
        }
    }

    /**
     * Enables session event tracking for the provided application
     *
     * @param application the application object
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void registerSessionManagementCallbacks(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            synchronized (LifeCycleTracking.LOCK) {
                registerActivityLifecycleCallbacks(application);
                autoSessionManagementEnabled = true;
            }
        }
    }

    /**
     * Disables session event tracking for the provided application
     *
     * @param application the application object
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void unregisterSessionManagementCallbacks(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            synchronized (LifeCycleTracking.LOCK) {
                unregisterActivityLifecycleCallbacks(application);
                autoSessionManagementEnabled = false;
            }
        }
    }

    /**
     * This is called each time an activity is created.
     *
     * @param activity the Android Activity that's created
     * @param savedInstanceState the bundle
     */
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        int count = this.activityCount.getAndIncrement();
        synchronized (LifeCycleTracking.LOCK) {
            if (count == 0 && autoSessionManagementEnabled) {
                new TrackDataTask(TrackDataTask.DataType.NEW_SESSION).execute();
            }
        }
    }

    /**
     * This is called each time an activity becomes visible
     *
     * @param activity the activity which entered the foreground
     */
    public void onActivityStarted(Activity activity) {
        // unused but required to implement ActivityLifecycleCallbacks
    }

    /**
     * This is called each time an activity leaves the foreground
     *
     * @param activity the activity which left the foreground
     */
    public void onActivityResumed(Activity activity) {
        // check if the session should be renewed
        long now = this.getTime();
        long then = this.lastBackground.getAndSet(this.getTime());
        boolean shouldRenew = now - then >= this.config.getSessionIntervalMs();

        synchronized (LifeCycleTracking.LOCK) {
            if(autoSessionManagementEnabled && shouldRenew){
                this.telemetryContext.renewSessionId();
                new TrackDataTask(TrackDataTask.DataType.NEW_SESSION).execute();
            }
            if(autoPageViewsEnabled){
                new TrackDataTask(TrackDataTask.DataType.PAGE_VIEW, activity.getClass().getName(), null, null).execute();
            }
        }
    }

    /**
     * This is called each time an activity leaves the foreground
     *
     * @param activity the activity which was paused
     */
    public void onActivityPaused(Activity activity) {
        this.lastBackground.set(this.getTime());
    }

    public void onActivityStopped(Activity activity) {
        // unused but required to implement ActivityLifecycleCallbacks
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // unused but required to implement ActivityLifecycleCallbacks
    }

    public void onActivityDestroyed(Activity activity) {
        // unused but required to implement ActivityLifecycleCallbacks
    }

    /**
     * Test hook to get the current time
     *
     * @return the current time in milliseconds
     */
    protected long getTime() {
        return new Date().getTime();
    }
}
