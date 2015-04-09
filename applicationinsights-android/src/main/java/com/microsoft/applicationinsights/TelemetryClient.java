package com.microsoft.applicationinsights;

import android.app.Application;

import com.microsoft.applicationinsights.internal.CreateDataTask;
import com.microsoft.applicationinsights.internal.logging.InternalLogging;

import java.util.Map;

/**
 * The public API for recording application insights telemetry.
 */
public class TelemetryClient {
    public static final String TAG = "TelemetryClient";

    private static TelemetryClient instance;

    /**
     * Volatile boolean for double checked synchronize block
     */
    private static volatile boolean isTelemetryClientLoaded = false;

    /**
     * Synchronization LOCK for setting static context
     */
    private static final Object LOCK = new Object();

    /**
     * Restrict access to the default constructor
     */
    protected TelemetryClient() {
    }

    /**
     * Initialize the INSTANCE of the telemetryclient
     */
    protected static void initialize() {
        // note: isPersistenceLoaded must be volatile for the double-checked LOCK to work
        if (!TelemetryClient.isTelemetryClientLoaded) {
            synchronized (TelemetryClient.LOCK) {
                if (!TelemetryClient.isTelemetryClientLoaded) {
                    TelemetryClient.isTelemetryClientLoaded = true;
                    TelemetryClient.instance = new TelemetryClient();
                }
            }
        }
    }

    /**
     * @return the INSTANCE of persistence or null if not yet initialized
     */
    public static TelemetryClient getInstance() {
        initialize();
        if (TelemetryClient.instance == null) {
            InternalLogging.error(TAG, "getInstance was called before initialization");
        }

        return TelemetryClient.instance;
    }


    /**
     * {@code properties} defaults to {@code null}.
     * {@code measurements} defaults to {@code null}.
     *
     * @see TelemetryClient#trackEvent(String, Map, Map)
     */
    public void trackEvent(String eventName) {
        trackEvent(eventName, null, null);
    }

    /**
     * {@code measurements} defaults to {@code null}.
     *
     * @see TelemetryClient#trackEvent(String, Map, Map)
     */
    public void trackEvent(String eventName, Map<String, String> properties) {
        trackEvent(eventName, properties, null);
    }

    /**
     * Sends information about an event to Application Insights.
     *
     * @param eventName    The name of the event
     * @param properties   Custom properties associated with the event. Note: values set here will
     *                     supersede values set in {@link com.microsoft.applicationinsights.AppInsights#setCommonProperties}.
     * @param measurements Custom measurements associated with the event.
     */
    public void trackEvent(
          String eventName,
          Map<String, String> properties,
          Map<String, Double> measurements) {
        new CreateDataTask(CreateDataTask.DataType.EVENT, eventName, properties, measurements).execute();
    }

    /**
     * {@code measurements} defaults to {@code null}.
     *
     * @see TelemetryClient#trackTrace(String, Map)
     */
    public void trackTrace(String message) {
        trackTrace(message, null);
    }

    /**
     * Sends tracing information to Application Insights.
     *
     * @param message    The message associated with this trace.
     * @param properties Custom properties associated with the event. Note: values set here will
     *                   supersede values set in {@link com.microsoft.applicationinsights.AppInsights#setCommonProperties}.
     */
    public void trackTrace(String message, Map<String, String> properties) {
        new CreateDataTask(CreateDataTask.DataType.TRACE, message, properties, null).execute();
    }

    /**
     * Sends information about an aggregated metric to Application Insights. Note: all data sent via
     * this method will be aggregated. To enqueue non-aggregated data use
     * {@link TelemetryClient#trackEvent(String, Map, Map)} with measurements.
     *
     * @param name  The name of the metric
     * @param value The value of the metric
     */
    public void trackMetric(String name, double value) {
        new CreateDataTask(CreateDataTask.DataType.METRIC, name, value).execute();
    }

    /**
     * {@code properties} defaults to {@code null}.
     * {@code measurements} defaults to {@code null}.
     *
     * @see TelemetryClient#trackHandledException(Throwable, Map)
     */
    public void trackHandledException(Throwable handledException) {
        this.trackHandledException(handledException, null);
    }

    /**
     * Sends information about an handledException to Application Insights.
     *
     * @param handledException The handledException to track.
     * @param properties       Custom properties associated with the event. Note: values set here will
     *                         supersede values set in {@link com.microsoft.applicationinsights.AppInsights#setCommonProperties}.
     */
    public void trackHandledException(Throwable handledException, Map<String, String> properties) {
        new CreateDataTask(CreateDataTask.DataType.HANDLED_EXCEPTION, handledException, properties).execute();
    }

    /**
     * {@code properties} defaults to {@code null}.
     * {@code measurements} defaults to {@code null}.
     *
     * @see TelemetryClient#trackPageView(String, Map, Map)
     */
    public void trackPageView(String pageName) {
        this.trackPageView(pageName, null, null);
    }

    /**
     * {@code measurements} defaults to {@code null}.
     *
     * @see TelemetryClient#trackPageView(String, Map, Map)
     */
    public void trackPageView(String pageName, Map<String, String> properties) {
        this.trackPageView(pageName, properties, null);
    }

    /**
     * Sends information about a page view to Application Insights.
     *
     * @param pageName     The name of the page.
     * @param properties   Custom properties associated with the event. Note: values set here will
     *                     supersede values set in {@link com.microsoft.applicationinsights.AppInsights#setCommonProperties}.
     * @param measurements Custom measurements associated with the event.
     */
    public void trackPageView(
          String pageName,
          Map<String, String> properties,
          Map<String, Double> measurements) {
        new CreateDataTask(CreateDataTask.DataType.PAGE_VIEW, pageName, properties, null).execute();
    }

    /**
     * Sends information about a new Session to Application Insights.
     */
    public void trackNewSession() {
        new CreateDataTask(CreateDataTask.DataType.NEW_SESSION).execute();
    }

    /**
     * Registers an activity life cycle callback handler to track page views and sessions.
     *
     * @param application the application used to register the life cycle callbacks
     */
    protected void enableActivityTracking(Application application) {
        LifeCycleTracking.registerActivityLifecycleCallbacks(application);
    }
}
