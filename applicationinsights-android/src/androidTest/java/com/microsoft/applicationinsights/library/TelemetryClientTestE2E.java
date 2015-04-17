package com.microsoft.applicationinsights.library;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.util.Log;

import com.microsoft.applicationinsights.library.ApplicationInsights;
import com.microsoft.applicationinsights.library.Channel;
import com.microsoft.applicationinsights.library.MockActivity;
import com.microsoft.applicationinsights.library.MockChannel;
import com.microsoft.applicationinsights.library.MockQueue;
import com.microsoft.applicationinsights.library.MockTelemetryClient;
import com.microsoft.applicationinsights.library.config.QueueConfig;
import com.microsoft.applicationinsights.library.config.SenderConfig;

import junit.framework.Assert;

import java.io.InvalidObjectException;
import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TelemetryClientTestE2E extends ActivityUnitTestCase<MockActivity> {

    public TelemetryClientTestE2E() {
        super(MockActivity.class);
    }

    private MockTelemetryClient client;
    private LinkedHashMap<String, String> properties;
    private LinkedHashMap<String, Double> measurements;

    public void setUp() throws Exception {
        super.setUp();

        Intent intent = new Intent(getInstrumentation().getTargetContext(), MockActivity.class);
        this.setActivity(this.startActivity(intent, null, null));

        MockTelemetryClient.getInstance().mockTrackMethod = false;
        Channel.initialize(new QueueConfig());
        Channel.getInstance().getQueue().getQueueConfig().setMaxBatchIntervalMs(20);

        Sender.initialize(new SenderConfig());

        this.properties = new LinkedHashMap<String, String>();
        this.properties.put("core property", "core value");
        this.measurements = new LinkedHashMap<String, Double>();
        this.measurements.put("core measurement", 5.5);
    }

    public void testTrackEvent() throws Exception {
        this.client.trackEvent(null);
        this.client.trackEvent("event1");
        this.client.trackEvent("event2", properties);
        this.client.trackEvent("event3", properties, measurements);
        this.validate();
    }

    public void testTrackTrace() throws Exception {
        this.client.trackTrace(null);
        this.client.trackTrace("trace1");
        this.client.trackTrace("trace2", properties);
        this.validate();
    }

    public void testTrackMetric() throws Exception {
        this.client.trackMetric(null, 0);
        this.client.trackMetric("metric1", 1.1);
        this.client.trackMetric("metric2", 3);
        this.client.trackMetric("metric3", 3.3);
        this.client.trackMetric("metric3", 4);
        this.validate();
    }

    public void testTrackException() throws Exception {
        this.client.trackHandledException(null);
        this.client.trackHandledException(new Exception());
        try {
            throw new InvalidObjectException("this is expected");
        } catch (InvalidObjectException exception) {
            this.client.trackHandledException(exception);
            this.client.trackHandledException(exception, properties);
        }

        this.validate();
    }

    public void testTrackPageView() throws Exception {
        this.client.trackPageView("android page");
        this.client.trackPageView("android page");
        this.client.trackPageView("android page", properties);
        this.client.trackPageView("android page", properties, measurements);
        this.validate();
    }

    public void testTrackAllRequests() throws Exception {
        Exception exception;
        try {
            throw new Exception();
        } catch (Exception e) {
            exception = e;
        }

        Channel.getInstance().getQueue().getQueueConfig().setMaxBatchCount(10);
        for (int i = 0; i < 10; i++) {
            this.client.trackEvent("android event");
            this.client.trackTrace("android trace");
            this.client.trackMetric("android metric", 0.0);
            this.client.trackHandledException(exception);
            this.client.trackPageView("android page");
            Thread.sleep(10);
        }

        ApplicationInsights.INSTANCE.sendPendingData();
        Thread.sleep(10);
        this.validate();
    }

    public void validate() throws Exception {
        try {
            MockQueue queue = MockChannel.getInstance().getQueue();
            CountDownLatch rspSignal = queue.sender.responseSignal;
            CountDownLatch sendSignal = queue.sender.sendSignal;
            rspSignal.await(30, TimeUnit.SECONDS);

            Log.i("RESPONSE", queue.sender.getLastResponse());

            if (rspSignal.getCount() < sendSignal.getCount()) {
                Log.w("BACKEND_ERROR", "response count is lower than enqueue count");
            } else if (queue.sender.responseCode == 206) {
                Log.w("BACKEND_ERROR", "response is 206, some telemetry was rejected");
            }

            if (queue.sender.responseCode != 200) {
                Assert.fail("response rejected with: " + queue.sender.getLastResponse());
            }

            Assert.assertEquals("response was received", 0, rspSignal.getCount());
            Assert.assertEquals("queue is empty", 0, queue.getQueueSize());
        } catch (InterruptedException e) {
            Assert.fail(e.toString());
        }
    }
}