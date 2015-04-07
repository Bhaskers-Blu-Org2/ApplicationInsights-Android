package com.microsoft.mocks;

import com.microsoft.applicationinsights.internal.Channel;
import com.microsoft.applicationinsights.internal.TelemetryQueue;

public class MockChannel extends Channel {
    public MockChannel() {
        super();
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