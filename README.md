[ ![Download](https://api.bintray.com/packages/appinsights-android/maven/ApplicationInsights-Android/images/download.svg) ](https://bintray.com/appinsights-android/maven/ApplicationInsights-Android/_latestVersion)

# Application Insights for Android (1.0-Beta.2)

This project provides an Android SDK for Application Insights. [Application Insights](http://azure.microsoft.com/en-us/services/application-insights/) is a service that allows developers to keep their applications available, performing, and succeeding. This module allows you to send telemetry of various kinds (events, traces, exceptions, etc.) to the Application Insights service where your data can be visualized in the Azure Portal.

The minimum SDK to use the Application Insights SDK in your app is 9.

Automatic collection of lifecycle-events requires API level 15 and up (Ice Cream Sandwich+).


**Release Notes**

* SDK has been compiled using Android SDK 22
* Resolution tracking (full resolution of the device in physical pixels)  
* Exposed property for UserID so it can set it manually
* Exposed property for SessionID so it can be overwriten e.g. on app start by the developer
* Internal APIs and properties are now hidden from the developer so the SDK can be used in a safe way
* Simplified configuration of the SDK using the SessionConfig, QueueConfig or SenderConfig classes (see [Usage](#1))
* Developer Mode now has to be activated by the developer (see [Developer Mode](#2))
* Minor bugfixes


**Breaking Changes**

* **[1.0-Beta.2]** To enable automatic lifecycle-tracking, Application Insights has to be set up with an instance of Application (see [Life-cycle tracking] (#2)), otherwise, lifecycle-tracking is disabled.

* **[1.0-Beta.1]** Setup and start of the Application Insights SDK are now done using the new umbrella class `ApplicationInsights` instead of `AppInsights `

* **[1.0-Alpha.5]** Setup and start of the Application Insights SDK are now done using the new umbrella class `AppInsights` instead of `TelemetryClient`

## Setup ##
	
**Add a compile dependency for the SDK**

Per-module

```java
dependencies {
    compile 'com.microsoft.azure:applicationinsights-android:+'
}
```

**Configure the instrumentation key and add permissions**

Please see the "[Getting an Application Insights Instrumentation Key](https://github.com/Microsoft/ApplicationInsights-Home/wiki#getting-an-application-insights-instrumentation-key)" section of the wiki for more information on acquiring a key.

Plase add the two permissions for `INTERNET` and `ACCESS_NETWORK_STATE` into your app's `AndroidManifest.xml` as well as the property for your instrumentation key as follows. Replace `${AI_INSTRUMENTATION_KEY}` with your instrumentation key or the variable leave it and use gradle.properties to set it. 

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application>
        <meta-data
            android:name="com.microsoft.applicationinsights.instrumentationKey"
            android:value="${AI_INSTRUMENTATION_KEY}" />
    </application>
</manifest>
```

**Optional: load instrumentation key from gradle**

~/.gradle/gradle.properties

```java
ai_instrumentation_key=<KEY_PLACEHOLDER>
```

Top-level gradle build file

```java
android {
    buildTypes {
        all {
            manifestPlaceholders = [AI_INSTRUMENTATION_KEY: ai_instrumentation_key]
        }
    }
}
```

**Optional: set instrumentation key in code**

It is also possible to set the instrumentation key of your app in code. This will override the one you might have set in your gradle or manifest file. Setting the instrumentation key programmatically can be done while setting up Application Insights:

```java
ApplicationInsights.setup(this, getApplication(), "<YOUR-IKEY-GOES-HERE>");
ApplicationInsights.start();
```

## <a name="1"></a>Usage ##

Add the following import to your app's root activity

```java
import com.microsoft.applicationinsights.library.ApplicationInsights;
```

And add 

```java
ApplicationInsights.setup(this, getApplication());
ApplicationInsights.start();
```

in the activity's `onCreate`-callback.

A typicall onCreate-method looks like this.

```java
public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ApplicationInsights.setup(this, getApplication());
        //... other initialization code ...//
        ApplicationInsights.start();
        
        // track telemetry data
        TelemetryClient client = TelemetryClient.getInstance();
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("property1", "my custom property");
        client.trackEvent("sample event", properties);
        client.trackTrace("sample trace");
        client.trackMetric("sample metric", 3);
    }
}
```

## <a name="2"></a> Automatic collection of life-cycle events

This only works in Android SDK version 15 and up (Ice Cream Sandwich+) and is enabled by default. Don't forget to set the Application instance when setting up ApplicationInsights (otherwise auto collection will be disabled):

```java
	ApplicationInsights.setup(this, getApplication()); //setup
```

If you want to explicitly **Disable** automatic collection of life-cycle events, call ```setAutoCollectionDisabled``` inbetween setup and start of Application Insights. 

```java
	ApplicationInsights.setup(this); //setup

	ApplicationInsights.setAutoCollectionDisabled(true); //disable the auto-collection
	
	ApplicationInsights.start(); //start
```

## <a name="3"></a> Additional configuration

To configure Application Insights according to your need, first, call

```java
ApplicationInsights.setup(this, getApplication()); //setup
```

And then use the different configuration objects to set your individual values.

```java
SessionConfig sessionConfig = ApplicationInsights.getSessionConfig();
// How long should the app stay in background until a new session gets created (20s by default)?
sessionConfig.setSessionIntervalMs(40000);
```

```java
SenderConfig senderConfig = ApplicationInsights.getSenderConfig();
// By default the endpoint URL points to the production environment
senderConfig.setEndpointUrl("http://dc-int.services.visualstudio.com/v2/track");
```

```java
QueueConfig queueConfig = ApplicationInsights.getQueueConfig();
// Interval for collecting and sending out telemetry data (unhandled exceptions are sent out immediately)
// 15s by default
queueConfig.setMaxBatchIntervalMs(3000);
```
After all configurations have been made, just start `ApplicationInsights`:

```java
ApplicationInsights.start(); //start
```

## <a name="4"></a> Developer Mode

The **developer mode** is enabled if the debugger is attached or if the app is running in the emulator. This will enable the console logging and decrease the number of telemetry items sent in a batch (5 items) as well as the interval items will be sent (3 seconds).

Feel free to explicitly enable/disable the developer mode:

```java
	 ApplicationInsights.setDeveloperMode(false);

```

## Documentation

[http://microsoft.github.io/ApplicationInsights-Android/]()

## Contributing

**Development environment**

* Install <a href="http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html" target="_blank">JDK 1.8</a>
* Install <a href="http://developer.android.com/sdk/index.html" target="_blank">Android studio</a>
* [Get an instrumentation key](/Microsoft/ApplicationInsights-Home/wiki#getting-an-application-insights-instrumentation-key) and set it in the manifest
* Run tests from android studio
