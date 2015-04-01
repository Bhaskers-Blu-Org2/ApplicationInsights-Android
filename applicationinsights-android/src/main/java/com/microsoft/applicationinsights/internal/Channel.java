package com.microsoft.applicationinsights.internal;

import android.content.Context;

import com.microsoft.applicationinsights.contracts.CrashData;
import com.microsoft.applicationinsights.contracts.Data;
import com.microsoft.applicationinsights.contracts.Envelope;
import com.microsoft.applicationinsights.contracts.shared.IJsonSerializable;
import com.microsoft.applicationinsights.contracts.shared.ITelemetry;
import com.microsoft.applicationinsights.contracts.shared.ITelemetryData;
import com.microsoft.applicationinsights.internal.logging.InternalLogging;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class records telemetry for application insights.
 */
public class Channel {

    private static final String TAG = "Channel";

    /**
     * The id for this channel
     */
    private final long channelId;

    /**
     * The sequence counter for this channel
     */
    private final AtomicInteger seqCounter;

    /**
     * Test hook to the sender
     */
    private TelemetryQueue queue;

    /**
     * Instantiates a new INSTANCE of Sender
     */
    public Channel() {
        this.queue = TelemetryQueue.INSTANCE;

        Random random = new Random();
        this.channelId = Math.abs(random.nextLong());
        this.seqCounter = new AtomicInteger(0);
    }

    /**
     * @return the sender for this channel.
     */
    public TelemetryQueue getQueue() {
        return this.queue;
    }

    /**
     * Test hook to set the queue for this channel
     *
     * @param queue the queue to use for this channel
     */
    protected void setQueue(TelemetryQueue queue) {
        this.queue = queue;
    }

    /**
     * Records the passed in data.
     *
     * @param envelope the envelope object to record
     */
    public void enqueue(Envelope envelope) {
        this.queue.isCrashing = false;

        // enqueue to queue
        this.queue.enqueue(envelope);

        InternalLogging.info(TAG, "enqueued telemetry", envelope.getName());
    }

    public void processUnhandledException(Envelope envelope) {
        this.queue.isCrashing = true;

        IJsonSerializable[] data = new IJsonSerializable[1];
        data[0] = envelope;

        Persistence persistence = Persistence.getInstance();
        if (persistence != null) {
            persistence.persist(data, true);
        }
        else {
            InternalLogging.info(TAG, "error persisting crash", envelope.toString());
        }

        this.queue.isCrashing = true;
        this.queue.flush();
    }

}
