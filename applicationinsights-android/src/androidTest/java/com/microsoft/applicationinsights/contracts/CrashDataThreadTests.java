package com.microsoft.applicationinsights.contracts;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/// <summary>
/// Data contract test class CrashDataThreadTests.
/// </summary>
public class CrashDataThreadTests extends TestCase
{
    public void testIdPropertyWorksAsExpected()
    {
        int expected = 42;
        CrashDataThread item = new CrashDataThread();
        item.setId(expected);
        int actual = item.getId();
        Assert.assertEquals(expected, actual);
        
        expected = 13;
        item.setId(expected);
        actual = item.getId();
        Assert.assertEquals(expected, actual);
    }
    
    public void testFramesPropertyWorksAsExpected()
    {
        CrashDataThread item = new CrashDataThread();
        ArrayList<CrashDataThreadFrame> actual = (ArrayList<CrashDataThreadFrame>)item.getFrames();
        Assert.assertNotNull(actual);
    }
    
    public void testSerialize() throws IOException
    {
        CrashDataThread item = new CrashDataThread();
        item.setId(42);
        for (CrashDataThreadFrame entry : new ArrayList<CrashDataThreadFrame>() {{add(new CrashDataThreadFrame());}})
        {
            item.getFrames().add(entry);
        }
        StringWriter writer = new StringWriter();
        item.serialize(writer);
        String expected = "{\"id\":42,\"frames\":[{\"address\":null}]}";
        Assert.assertEquals(expected, writer.toString());
    }

}
