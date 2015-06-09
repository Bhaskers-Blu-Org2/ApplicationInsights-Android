package com.microsoft.applicationinsights.library;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.microsoft.applicationinsights.library.config.ApplicationInsightsConfig;
import com.microsoft.applicationinsights.logging.InternalLogging;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ApplicationInsights {
    INSTANCE;

    /**
     * The tag for logging.
     */
    private static final String TAG = "ApplicationInsights";

    /**
     * A flag which determines, if developer mode (logging) should be enabled.
     */
    private static AtomicBoolean DEVELOPER_MODE = new AtomicBoolean(Util.isEmulator() || Util.isDebuggerAttached());

    /**
     * The configuration of the SDK.
     */
    private ApplicationInsightsConfig config;

    /**
     * A flag, which determines if auto collection of sessions and page views should be disabled.
     * Default is false.
     */
    private boolean autoCollectionDisabled;

    /**
     * A flag, which determines if sending telemetry data should be disabled. Default is false.
     */
    private boolean telemetryDisabled;

    /**
     * A flag, which determines if crash reporting should be disabled. Default is false.
     */
    private boolean exceptionTrackingDisabled;

    /**
     * The instrumentation key associated with the app.
     */
    private String instrumentationKey;

    /**
     * The weakContext which contains additional information for the telemetry data sent out.
     */
    private TelemetryContext telemetryContext;

    /**
     * A custom user ID used for sending telemetry data.
     */
    private String userId;

    /**
     * The weakContext associated with Application Insights.
     */
    private WeakReference<Context> weakContext;

    /**
     * The application needed for auto collecting telemetry data
     */
    private WeakReference<Application> weakApplication;

    /**
     * Properties associated with this telemetryContext.
     */
    private Map<String, String> commonProperties;

    private static boolean isRunning;
    private static boolean isSetup;

    /**
     * Create ApplicationInsights instance
     */
    ApplicationInsights() {
        this.telemetryDisabled = false;
        this.exceptionTrackingDisabled = false;
        this.autoCollectionDisabled = false;
        this.config = new ApplicationInsightsConfig();
    }

    /**
     * Configure Application Insights
     * Note: This should be called before start
     *
     * @param context the context associated with Application Insights
     * @param context the application context associated with Application Insights
     * @warning auto-collection of lifecycle-events is disabled when using this method
     * @deprecated This method is deprecated: Use setup(Context context, Application application) instead.
     */
    public static void setup(Context context) {
        ApplicationInsights.INSTANCE.setupInstance(context, null, null);
    }

    /**
     * Configure Application Insights
     * Note: This should be called before start
     *
     * @param application the application context the application needed for auto collecting telemetry data
     */
    public static void setup(Context context, Application application) {
        ApplicationInsights.INSTANCE.setupInstance(context, application, null);
    }

    /**
     * Configure Application Insights
     * Note: This should be called before start
     *
     * @param context            the application context associated with Application Insights
     * @param instrumentationKey the instrumentation key associated with the app
     * @warning auto-collection of lifecycle-events is disabled when using this method
     * @deprecated This method is deprecated: Use setup(Context context, Application application) instead.
     */
    public static void setup(Context context, String instrumentationKey) {
        ApplicationInsights.INSTANCE.setupInstance(context, null, instrumentationKey);
    }

    /**
     * Configure Application Insights
     * Note: This should be called before start
     *
     * @param context            the application context associated with Application Insights
     * @param application        the application needed for auto collecting telemetry data
     * @param instrumentationKey the instrumentation key associated with the app
     */
    public static void setup(Context context, Application application, String instrumentationKey) {
        ApplicationInsights.INSTANCE.setupInstance(context, application, instrumentationKey);
    }

    /**
     * Configure Application Insights
     * Note: This should be called before start
     *
     * @param context            the application context associated with Application Insights
     * @param instrumentationKey the instrumentation key associated with the app
     */
    public void setupInstance(Context context, Application application, String instrumentationKey) {
        if (!isSetup) {
            if (context != null) {
                this.weakContext = new WeakReference<Context>(context);
                this.instrumentationKey = instrumentationKey;
                this.weakApplication = new WeakReference<Application>(application);
                isSetup = true;
                InternalLogging.info(TAG, "ApplicationInsights has been setup correctly.", null);
            } else {
                InternalLogging.warn(TAG, "ApplicationInsights could not be setup correctly " +
                      "because the given weakContext was null");
            }
        }

    }

    /**
     * Start ApplicationInsights
     * Note: This should be called after {@link #isSetup}
     */
    public static void start() {
        INSTANCE.startInstance();
    }

    /**
     * Start ApplicationInsights
     * Note: This should be called after {@link #isSetup}
     */
    public void startInstance() {
        if (!isSetup) {
            InternalLogging.warn(TAG, "Could not start Application Insights since it has not been " +
                  "setup correctly.");
            return;
        }
        if (!isRunning) {
            Context context = INSTANCE.getContext();

            if (context == null) {
                InternalLogging.warn(TAG, "Could not start Application Insights as context is null");
                return;
            }

            if (this.instrumentationKey == null) {
                this.instrumentationKey = readInstrumentationKey(context);
            }

            this.telemetryContext = new TelemetryContext(context, this.instrumentationKey, userId);
            EnvelopeFactory.initialize(telemetryContext, this.commonProperties);

            Persistence.initialize(context);
            Sender.initialize(this.config);
            Channel.initialize(this.config);

            // Initialize Telemetry
            TelemetryClient.initialize(!telemetryDisabled);
            Application application = INSTANCE.getApplication();

            if (INSTANCE.getApplication() != null &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                    !this.autoCollectionDisabled) {
                LifeCycleTracking.initialize(telemetryContext, this.config);
                LifeCycleTracking.registerForPersistingWhenInBackground(application);
                LifeCycleTracking.registerPageViewCallbacks(application);
                LifeCycleTracking.registerSessionManagementCallbacks(application);
            } else {
                InternalLogging.warn(TAG, "Auto collection of page views could not be " +
                      "started. Either the given application was null, the device API level " +
                        "is lower than 14, or the user actively disabled the feature.");
            }

            // Start crash reporting
            if (!this.exceptionTrackingDisabled) {
                ExceptionTracking.registerExceptionHandler();
            }

            isRunning = true;
            Sender.getInstance().sendDataOnAppStart();
            InternalLogging.info(TAG, "ApplicationInsights has been started.", "");
        }
    }

    /**
     * Triggers persisting and if applicable sending of queued data
     * note: this will be called
     * {@link com.microsoft.applicationinsights.library.config.ApplicationInsightsConfig#maxBatchIntervalMs} after
     * tracking any telemetry so it is not necessary to call this in most cases.
     */
    public static void sendPendingData() {
        if (!isRunning) {
            InternalLogging.warn(TAG, "Could not set send pending data, because " +
                  "ApplicationInsights has not been started, yet.");
            return;
        }
        Channel.getInstance().synchronize();
    }

    /**
     * Enable auto page view tracking as well as auto session tracking. This will only work, if
     * {@link ApplicationInsights#telemetryDisabled} is set to false.
     *
     * @param application the application used to register the life cycle callbacks
     * @deprecated This method is deprecated: Use setAutoCollectionDisabled instead.
     */
    public static void enableActivityTracking(Application application) {
        if (!isRunning) { //TODO fix log warning
            InternalLogging.warn(TAG, "Could not set activity tracking, because " +
                  "ApplicationInsights has not been started, yet.");
            return;
        }
        if (!INSTANCE.telemetryDisabled) {
            if(application != null){
                LifeCycleTracking.registerActivityLifecycleCallbacks(application);
            }
        }
    }

    /**
     * Enable auto page view tracking. This will only work, if ApplicationInsights has been setup
     * with an application. This method should only be called after
     * {@link com.microsoft.applicationinsights.library.ApplicationInsights#start()}.
     */
    public static void enableAutoPageViewTracking() {
        if (!isRunning) {
            InternalLogging.warn(TAG, "Could not set page view tracking, because " +
                  "ApplicationInsights has not been started yet.");
            return;
        } else if (INSTANCE.getApplication() == null) {
            InternalLogging.warn(TAG, "Could not set page view tracking, because " +
                  "ApplicationInsights has not been setup with an application.");
            return;
        } else {
            LifeCycleTracking.registerPageViewCallbacks(INSTANCE.getApplication());
        }
    }

    /**
     * Disable auto page view tracking. This will only work, if ApplicationInsights has been setup
     * with an application. This method should only be called after
     * {@link com.microsoft.applicationinsights.library.ApplicationInsights#start()}.
     */
    public static void disableAutoPageViewTracking() {
        if (!isRunning) {
            InternalLogging.warn(TAG, "Could not unset page view tracking, because " +
                  "ApplicationInsights has not been started yet.");
            return;
        } else if (INSTANCE.getApplication() == null) {
            InternalLogging.warn(TAG, "Could not unset page view tracking, because " +
                  "ApplicationInsights has not been setup with an application.");
            return;
        } else {
            LifeCycleTracking.unregisterPageViewCallbacks(INSTANCE.getApplication());
        }
    }

    /**
     * Enable auto session tracking. This will only work, if ApplicationInsights has been setup
     * with an application. This method should only be called after
     * {@link com.microsoft.applicationinsights.library.ApplicationInsights#start()}.
     */
    public static void enableAutoSessionManagement() {
        if (!isRunning) {
            InternalLogging.warn(TAG, "Could not set session management, because " +
                  "ApplicationInsights has not been started yet.");
            return;
        } else if (INSTANCE.getApplication() == null) {
            InternalLogging.warn(TAG, "Could not set session management, because " +
                  "ApplicationInsights has not been setup with an application.");
            return;
        } else {
            LifeCycleTracking.registerSessionManagementCallbacks(INSTANCE.getApplication());
        }
    }

    /**
     * Disable auto session tracking. This will only work, if ApplicationInsights has been setup
     * with an application. This method should only be called after
     * {@link com.microsoft.applicationinsights.library.ApplicationInsights#start()}.
     */
    public static void disableAutoSessionManagement() {
        if (!isRunning) {
            InternalLogging.warn(TAG, "Could not unset session management, because " +
                  "ApplicationInsights has not been started yet.");
            return;
        } else if (INSTANCE.getApplication() == null) {
            InternalLogging.warn(TAG, "Could not unset session management, because " +
                  "ApplicationInsights has not been setup with an application.");
            return;
        } else {
            LifeCycleTracking.unregisterSessionManagementCallbacks(INSTANCE.getApplication());
        }
    }

    /**
     * Enable / disable tracking of unhandled exceptions.
     *
     * @param disabled if set to true, crash reporting will be disabled
     */
    public static void setExceptionTrackingDisabled(boolean disabled) {
        if (!isSetup) {
            InternalLogging.warn(TAG, "Could not enable/disable exception tracking, because " +
                  "ApplicationInsights has not been setup correctly.");
            return;
        }
        if (isRunning) {
            InternalLogging.warn(TAG, "Could not enable/disable exception tracking, because " +
                  "ApplicationInsights has already been started.");
            return;
        }
        INSTANCE.exceptionTrackingDisabled = disabled;
    }

    /**
     * Enable / disable tracking of telemetry data.
     *
     * @param disabled if set to true, the telemetry feature will be disabled
     */
    public static void setTelemetryDisabled(boolean disabled) {
        if (!isSetup) {
            InternalLogging.warn(TAG, "Could not enable/disable telemetry, because " +
                  "ApplicationInsights has not been setup correctly.");
            return;
        }
        if (isRunning) {
            InternalLogging.warn(TAG, "Could not enable/disable telemetry, because " +
                  "ApplicationInsights has already been started.");
            return;
        }
        INSTANCE.telemetryDisabled = disabled;
    }

    /**
     * Enable / disable auto collection of telemetry data.
     *
     * @param disabled if set to true, the auto collection feature will be disabled
     */
    public static void setAutoCollectionDisabled(boolean disabled) {
        if (!isSetup) {
            InternalLogging.warn(TAG, "Could not enable/disable auto collection, because " +
                  "ApplicationInsights has not been setup correctly.");
            return;
        }
        if (isRunning) {
            InternalLogging.warn(TAG, "Could not enable/disable auto collection, because " +
                  "ApplicationInsights has already been started.");
            return;
        }
        INSTANCE.autoCollectionDisabled = disabled;
    }

    /**
     * Gets the properties which are common to all telemetry sent from this client.
     *
     * @return common properties for this telemetry client
     */
    public static Map<String, String> getCommonProperties() {
        return INSTANCE.commonProperties;
    }

    /**
     * Sets properties which are common to all telemetry sent form this client.
     *
     * @param commonProperties a dictionary of properties to enqueue with all telemetry.
     */
    public static void setCommonProperties(Map<String, String> commonProperties) {
        if (!isSetup) {
            InternalLogging.warn(TAG, "Could not set common properties, because " +
                  "ApplicationInsights has not been setup correctly.");
            return;
        }
        if (isRunning) {
            InternalLogging.warn(TAG, "Could not set common properties, because " +
                  "ApplicationInsights has already been started.");
            return;
        }
        INSTANCE.commonProperties = commonProperties;
    }

    public static void setDeveloperMode(boolean developerMode) {
        DEVELOPER_MODE.set(developerMode);
    }

    public static boolean isDeveloperMode() {
        return DEVELOPER_MODE.get();
    }

    /**
     * Reads the instrumentation key from AndroidManifest.xml if it is available
     *
     * @param context the application weakContext to check the manifest from
     * @return the instrumentation key configured for the application
     */
    private String readInstrumentationKey(Context context) {
        String iKey = "";
        if (context != null) {
            try {
                Bundle bundle = context
                      .getPackageManager()
                      .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                      .metaData;
                if (bundle != null) {
                    iKey = bundle.getString("com.microsoft.applicationinsights.instrumentationKey");
                } else {
                    logInstrumentationInstructions();
                }
            } catch (PackageManager.NameNotFoundException exception) {
                logInstrumentationInstructions();
                Log.v(TAG, exception.toString());
            }
        }

        return iKey;
    }

    /**
     * Returns the application reference that Application Insights needs.
     *
     * @return the Context that's used by the Application Insights SDK
     */
    protected Context getContext() {
        Context context = null;
        if (weakContext != null) {
            context = weakContext.get();
        }

        return context;
    }

    /**
     * Get the reference to the Application (used for life-cycle tracking)
     *
     * @return the reference to the application that was used during initialization of the SDK
     */
    protected Application getApplication() {
        Application application = null;
        if (weakApplication != null) {
            application = weakApplication.get();
        }

        return application;
    }


    /* Writes instructions on how to configure the instrumentation key.
        */
    private static void logInstrumentationInstructions() {
        String instructions = "No instrumentation key found.\n" +
              "Set the instrumentation key in AndroidManifest.xml";
        String manifestSnippet = "<meta-data\n" +
              "android:name=\"com.microsoft.applicationinsights.instrumentationKey\"\n" +
              "android:value=\"${AI_INSTRUMENTATION_KEY}\" />";
        InternalLogging.error("MissingInstrumentationkey", instructions + "\n" + manifestSnippet);
    }

    /**
     * Gets the configuration for the ApplicationInsights instance
     *
     * @return the instance ApplicationInsights configuration
     */
    public static ApplicationInsightsConfig getConfig() {
        return INSTANCE.config;
    }

    /**
     * Sets the session configuration for the instance
     */
    public void setConfig(ApplicationInsightsConfig config) {
        if (!isSetup) {
            InternalLogging.warn(TAG, "Could not set telemetry configuration, because " +
                  "ApplicationInsights has not been setup correctly.");
            return;
        }
        if (isRunning) {
            InternalLogging.warn(TAG, "Could not set telemetry configuration, because " +
                  "ApplicationInsights has already been started.");
            return;
        }
        INSTANCE.config = config;
    }

    /**
     * Force Application Insights to create a new session with a custom sessionID.
     *
     * @param sessionId a custom session ID used of the session to create
     */
    public static void renewSession(String sessionId) {
        if (!INSTANCE.telemetryDisabled && INSTANCE.telemetryContext != null) {
            INSTANCE.telemetryContext.renewSessionId(sessionId);
        }
    }

    /**
     * Set the user Id associated with the telemetry data. If userId == null, ApplicationInsights
     * will generate a random ID.
     *
     * @param userId a user ID associated with the telemetry data
     */
    public static void setUserId(String userId) {
        if (isRunning) {
            INSTANCE.telemetryContext.configUserContext(userId);
        } else {
            INSTANCE.userId = userId;
        }
    }

    /**
     * Get the instrumentation key associated with this app.
     *
     * @return the Application Insights instrumentation key set for this app
     */
    protected static String getInstrumentationKey() {
        return INSTANCE.instrumentationKey;
    }


}
