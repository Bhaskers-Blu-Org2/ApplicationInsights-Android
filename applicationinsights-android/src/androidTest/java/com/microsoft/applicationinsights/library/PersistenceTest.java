package com.microsoft.applicationinsights.library;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.io.File;

public class PersistenceTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();
        Persistence.initialize(this.getContext());
        Persistence persistence = Persistence.getInstance();

        while(persistence.nextAvailableFile() != null) {
            File file = persistence.nextAvailableFile();
            persistence.deleteFile(file);
        }

    }

    public void testGetInstance() throws Exception {
        Persistence persistence = Persistence.getInstance();
        Assert.assertNotNull(persistence);
    }

    public void testSaveAndGetData() throws Exception {
        Persistence persistence = Persistence.getInstance();

        String data = "SAVE THIS DATA";
        persistence.writeToDisk(data, false);
        File file = persistence.nextAvailableFile();
        Assert.assertEquals("Data retrieved from file is equal to data saved", data, persistence.load(file));
    }
}