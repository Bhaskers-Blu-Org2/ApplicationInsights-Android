package com.microsoft.applicationinsights.channel;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.microsoft.applicationinsights.channel.contracts.Application;
import com.microsoft.applicationinsights.channel.contracts.Device;
import com.microsoft.applicationinsights.channel.contracts.Internal;
import com.microsoft.applicationinsights.channel.contracts.Location;
import com.microsoft.applicationinsights.channel.contracts.Operation;
import com.microsoft.applicationinsights.channel.contracts.Session;
import com.microsoft.applicationinsights.channel.contracts.User;
import com.microsoft.applicationinsights.testapp.R;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class is holding all context information.
 */
public class Context {

    /**
     * Android app context.
     */
    private android.content.Context androidAppContext;

    /**
     * Application context.
     */
    private Application application;

    /**
     * Device context.
     */
    private Device device;

    /**
     * Location context.
     */
    private Location location;

    /**
     * Operation context.
     */
    private Operation operation;

    /**
     * Session context.
     */
    private Session session;

    /**
     * User context.
     */
    private User user;

    /**
     * Internal context.
     */
    private Internal internal;

    /**
     * Properties associated with this context.
     */
    private HashMap<String, String> properties;

    public android.content.Context getAndroidApp() {
        return androidAppContext;
    }

    public void setAndroidApp(android.content.Context androidApp) {
        this.androidAppContext = androidApp;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Internal getInternal() {
        return internal;
    }

    public void setInternal(Internal internal) {
        this.internal = internal;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Constructs a new instance of the Telemetry context tag keys
     * @param config the configuration for this context
     */
    public Context(IContextConfig config) {
        super();
        this.androidAppContext = config.getAppContext(); // todo: use session info
        this.properties = new HashMap<String, String>();

        this.application = new Application();
        this.device = new Device();
        this.location = new Location();
        this.operation = new Operation();
        this.session = new Session();
        this.user = new User();
        this.internal = new Internal();

        this.setAppContext();
        this.setDeviceContext();
        this.setSessionContext();
    }

    public HashMap<String, String> toHashMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        this.application.addToHashMap(map);
        this.device.addToHashMap(map);
        this.location.addToHashMap(map);
        this.operation.addToHashMap(map);
        this.session.addToHashMap(map);
        this.user.addToHashMap(map);
        this.internal.addToHashMap(map);
        return map;
    }

    /**
     * Sets the application context tags
     */
    private void setAppContext() {
        ContentResolver resolver = this.androidAppContext.getContentResolver();
        String id = Secure.getString(resolver, Secure.ANDROID_ID);
        if(id != null) {
            this.application.setVer(id);
        }
    }

    /**
     * Sets the session context tags
     */
    private void setSessionContext() {
        this.session.setIsNew("true");
        this.session.setId(UUID.randomUUID().toString());
    }

    /**
     * Sets the device context tags
     */
    private void setDeviceContext() {

        // get device ID
        ContentResolver resolver = this.androidAppContext.getContentResolver();
        String deviceIdentifier = Settings.Secure.getString(resolver, Settings.Secure.ANDROID_ID);
        if(deviceIdentifier != null) {
            this.device.setId(deviceIdentifier);
        }

        // check device type
        final TelephonyManager manager = (TelephonyManager)
                this.androidAppContext.getSystemService(android.content.Context.TELEPHONY_SERVICE);
        if (manager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
            this.device.setType("Phone");
        } else {
            this.device.setType("Tablet");
        }

        this.device.setOsVersion(Build.VERSION.RELEASE);
        this.device.setOs("Android");
        this.device.setOemName(Build.MANUFACTURER);
        this.device.setModel(Build.MODEL);
        this.device.setLocale(java.util.Locale.getDefault().toString());
    }
}
