package com.microsoft.applicationinsights;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TelemetryClientConfigTest extends TestCase {

    TelemetryClientConfig config;

    public void setUp() throws Exception {
        super.setUp();
        this.config = new TelemetryClientConfig("ikey");
    }

    public void tearDown() throws Exception {

    }

    public void testGetInstrumentationKey() throws Exception {
        Assert.assertEquals("Ikey is set", "ikey", this.config.getInstrumentationKey());
    }

    public void testGetSessionRenewalMs() throws Exception {
        Assert.assertEquals("SessionRenewal is set", TelemetryClientConfig.defaultSessionRenewalMs,
                this.config.getSessionRenewalMs());
    }

    public void testGetSessionExpirationMs() throws Exception {
        Assert.assertEquals("SessionExpiry is set", TelemetryClientConfig.defaultSessionExpirationMs,
                this.config.getSessionExpirationMs());
    }

    public void testGetSenderConfig() throws Exception {
        Assert.assertNotNull("Sender config is not null", this.config.getGlobalSenderConfig());
    }
}