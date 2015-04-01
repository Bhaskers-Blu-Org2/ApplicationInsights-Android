package com.microsoft.applicationinsights.contracts;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;

/// <summary>
/// Data contract test class ApplicationTests.
/// </summary>
public class ApplicationTests extends TestCase
{
    public void testVerPropertyWorksAsExpected()
    {
        String expected = "Test string";
        Application item = new Application();
        item.setVer(expected);
        String actual = item.getVer();
        Assert.assertEquals(expected, actual);
        
        expected = "Other string";
        item.setVer(expected);
        actual = item.getVer();
        Assert.assertEquals(expected, actual);
    }
    
    public void testSerialize() throws IOException
    {
        Application item = new Application();
        item.setVer("Test string");
        StringWriter writer = new StringWriter();
        item.serialize(writer);
        String expected = "{\"ai.application.ver\":\"Test string\"}";
        Assert.assertEquals(expected, writer.toString());
    }

}
