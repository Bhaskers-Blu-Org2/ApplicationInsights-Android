package com.microsoft.applicationinsights.library;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

import junit.framework.Assert;

public class ExceptionTrackingTest extends ActivityUnitTestCase<MockActivity> {

    public Thread.UncaughtExceptionHandler originalHandler;
    public ExceptionTrackingTest() {
        super(MockActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        Intent intent = new Intent(getInstrumentation().getTargetContext(), MockActivity.class);
        this.setActivity(this.startActivity(intent, null, null));
    }

    public void tearDown() throws Exception {
        super.tearDown();
        Thread.setDefaultUncaughtExceptionHandler(originalHandler);
        Channel.getInstance().queue.setIsCrashing(false);
        ApplicationInsights.setDeveloperMode(false);
    }

    public void testRegisterExceptionHandler() throws Exception {
        ExceptionTracking.registerExceptionHandler();
        Thread.UncaughtExceptionHandler handler =
                Thread.getDefaultUncaughtExceptionHandler();
        Assert.assertNotNull("handler is set", handler);
        Assert.assertEquals("handler is of correct type", ExceptionTracking.class, handler.getClass());

        // double register without debug mode
        ApplicationInsights.setDeveloperMode(false);
        ExceptionTracking.registerExceptionHandler();
        Assert.assertTrue("no exception for multiple registration without debug mode", true);
    }
}