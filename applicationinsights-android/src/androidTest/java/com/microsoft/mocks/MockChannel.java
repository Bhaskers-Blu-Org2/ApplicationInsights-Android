package com.microsoft.mocks;

import android.content.Context;

import com.microsoft.applicationinsights.TelemetryClientConfig;
import com.microsoft.applicationinsights.channel.TelemetryChannel;
import com.microsoft.applicationinsights.channel.TelemetryQueue;

public class MockChannel extends TelemetryChannel {
    public MockChannel(TelemetryClientConfig config, Context context) {
        super(config, context);
    }

    @Override
    public void setQueue(TelemetryQueue queue) {
        super.setQueue(queue);
    }

    @Override
    public MockQueue getQueue() {
        return (MockQueue)super.getQueue();
    }
}