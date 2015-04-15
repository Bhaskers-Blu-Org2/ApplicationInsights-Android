package com.microsoft.applicationinsights.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.microsoft.applicationinsights.internal.logging.InternalLogging;

public class ChannelConfig {

    private static final String TAG = "TelemetryChannelConfig";

    /**
     * Synchronization LOCK for setting the iKey
     */
    private static final Object LOCK = new Object();

    /**
     * The instrumentationKey from AndroidManifest.xml
     */
    private static String iKeyFromManifest = null;

    /**
     * The instrumentation key for this telemetry channel
     */
    private String instrumentationKey;

    /**
     * Constructs a new INSTANCE of TelemetryChannelConfig
     *
     * @param context The android activity context
     */
    public ChannelConfig(Context context) {
        this.instrumentationKey = ChannelConfig.readInstrumentationKey(context);
    }

    /**
     * Gets the instrumentation key for this telemetry channel
     * @return the instrumentation key
     */
    public String getInstrumentationKey() {
        return instrumentationKey;
    }

    /**
     * Sets the instrumentation key for this telemetry channel
     *
     * @param instrumentationKey
     */
    public void setInstrumentationKey(String instrumentationKey) {
        this.instrumentationKey = instrumentationKey;
    }

    /**
     * Gets the sender config INSTANCE for this channel.
     *
     * @return The TelemetryConfig for Application Insights
     */
    public static TelemetryConfig getStaticConfig() {
        return ChannelQueue.INSTANCE.getConfig();
    }

    /**
     * Gets the static instrumentation key from AndroidManifest.xml if it is available
     *
     * @param context the application context to check the manifest from
     * @return the instrumentation key for the application or empty string if not available
     */
    private static String getInstrumentationKey(Context context) {
        synchronized (ChannelConfig.LOCK) {
            if (ChannelConfig.iKeyFromManifest == null) {
                String iKey = ChannelConfig.readInstrumentationKey(context);
                ChannelConfig.iKeyFromManifest = iKey;
            }
        }

        return ChannelConfig.iKeyFromManifest;
    }

    /**
     * Reads the instrumentation key from AndroidManifest.xml if it is available
     *
     * @param context the application context to check the manifest from
     * @return the instrumentation key configured for the application
     */
    private static String readInstrumentationKey(Context context) {
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
     * Writes instructions on how to configure the instrumentation key.
     */
    private static void logInstrumentationInstructions() {
        String instructions = "No instrumentation key found.\n" +
                "Set the instrumentation key in AndroidManifest.xml";
        String manifestSnippet = "<meta-data\n" +
                "android:name=\"com.microsoft.applicationinsights.instrumentationKey\"\n" +
                "android:value=\"${AI_INSTRUMENTATION_KEY}\" />";
        InternalLogging.error("MissingInstrumentationkey", instructions + "\n" + manifestSnippet);
    }
}
