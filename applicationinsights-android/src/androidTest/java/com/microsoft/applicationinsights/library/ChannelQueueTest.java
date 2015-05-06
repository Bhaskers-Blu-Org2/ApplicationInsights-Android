package com.microsoft.applicationinsights.library;

import android.test.InstrumentationTestCase;

import com.microsoft.applicationinsights.contracts.Envelope;
import com.microsoft.applicationinsights.contracts.shared.IJsonSerializable;
import com.microsoft.applicationinsights.library.config.IQueueConfig;

import junit.framework.Assert;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ChannelQueueTest extends InstrumentationTestCase {

    private PublicChannelQueue sut;
    private IQueueConfig mockConfig;
    private PublicPersistence mockPersistence;

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache",getInstrumentation().getTargetContext().getCacheDir().getPath());
        mockConfig = mock(IQueueConfig.class);
        sut = new PublicChannelQueue(mockConfig);
        mockPersistence = mock(PublicPersistence.class);
        sut.setPersistence(mockPersistence);
    }

    public void testInitialisationWorks() {
        Assert.assertNotNull(sut.config);
        Assert.assertNotNull(sut.timer);
        Assert.assertNotNull(sut.list);
        Assert.assertEquals(0, sut.list.size());
        Assert.assertFalse(sut.isCrashing);
    }

    public void testItemGetsEnqueued(){
        // Setup
        when(mockConfig.getMaxBatchIntervalMs()).thenReturn(10000);
        when(mockConfig.getMaxBatchCount()).thenReturn(3);

        // Test
        sut.enqueue(new Envelope());
        sut.enqueue(new Envelope());

        // Verify
        Assert.assertEquals(2, sut.list.size());
    }

    public void testQueueFlushedIfMaxBatchCountReached() {
        // Setup
        when(mockConfig.getMaxBatchIntervalMs()).thenReturn(10000);
        when(mockConfig.getMaxBatchCount()).thenReturn(3);

        // Test
        sut.enqueue(new Envelope());
        sut.enqueue(new Envelope());

        // Verify
        Assert.assertEquals(2, sut.list.size());
        verify(mockPersistence,never()).persist(any(IJsonSerializable[].class), anyBoolean(), anyBoolean());

        sut.enqueue(new Envelope());

        Assert.assertEquals(0, sut.list.size());
        verify(mockPersistence,times(1)).persist(any(IJsonSerializable[].class), anyBoolean(), anyBoolean());
    }

    public void testQueueFlushedAfterBatchIntervalReached() {
        // Setup
        when(mockConfig.getMaxBatchIntervalMs()).thenReturn(200);
        when(mockConfig.getMaxBatchCount()).thenReturn(3);

        // Test
        sut.enqueue(new Envelope());

        // Verify
        Assert.assertEquals(1, sut.list.size());
        verify(mockPersistence,never()).persist(any(IJsonSerializable[].class), anyBoolean(), anyBoolean());
        verify(mockPersistence,after(250).times(1)).persist(any(IJsonSerializable[].class), anyBoolean(), anyBoolean());
        Assert.assertEquals(0, sut.list.size());
    }

    public void testFlushingQueueWorks() {
        //Setup
        when(mockConfig.getMaxBatchIntervalMs()).thenReturn(200);
        when(mockConfig.getMaxBatchCount()).thenReturn(3);

        sut.enqueue(new Envelope());
        Assert.assertEquals(1, sut.list.size());
        verify(mockPersistence,never()).persist(any(IJsonSerializable[].class), anyBoolean(), anyBoolean());

        // Test
        sut.flush(true);

        // Verify
        Assert.assertEquals(0, sut.list.size());
        verify(mockPersistence,times(1)).persist(any(IJsonSerializable[].class), anyBoolean(), anyBoolean());
    }


}
