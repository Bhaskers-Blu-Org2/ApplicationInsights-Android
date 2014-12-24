package com.microsoft.applicationinsights.channel.contracts;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/// <summary>
/// Data contract test class ExceptionDataTests.
/// </summary>
public class ExceptionDataTests extends TestCase
{
    public void testVerPropertyWorksAsExpected()
    {
        int expected = 42;
        ExceptionData item = new ExceptionData();
        item.setVer(expected);
        int actual = item.getVer();
        Assert.assertEquals(expected, actual);
        
        expected = 13;
        item.setVer(expected);
        actual = item.getVer();
        Assert.assertEquals(expected, actual);
    }
    
    public void testHandled_atPropertyWorksAsExpected()
    {
        String expected = "Test string";
        ExceptionData item = new ExceptionData();
        item.setHandledAt(expected);
        String actual = item.getHandledAt();
        Assert.assertEquals(expected, actual);
        
        expected = "Other string";
        item.setHandledAt(expected);
        actual = item.getHandledAt();
        Assert.assertEquals(expected, actual);
    }
    
    public void testExceptionsPropertyWorksAsExpected()
    {
        ExceptionData item = new ExceptionData();
        ArrayList<ExceptionDetails> actual = (ArrayList<ExceptionDetails>)item.getExceptions();
        Assert.assertNotNull(actual);
    }
    
    public void testSeverity_levelPropertyWorksAsExpected()
    {
        int expected = 5;
        ExceptionData item = new ExceptionData();
        item.setSeverityLevel(expected);
        int actual = item.getSeverityLevel();
        Assert.assertEquals(expected, actual);
        
        expected = 3;
        item.setSeverityLevel(expected);
        actual = item.getSeverityLevel();
        Assert.assertEquals(expected, actual);
    }
    
    public void testPropertiesPropertyWorksAsExpected()
    {
        ExceptionData item = new ExceptionData();
        LinkedHashMap<String, String> actual = (LinkedHashMap<String, String>)item.getProperties();
        Assert.assertNotNull(actual);
    }
    
    public void testMeasurementsPropertyWorksAsExpected()
    {
        ExceptionData item = new ExceptionData();
        LinkedHashMap<String, Double> actual = (LinkedHashMap<String, Double>)item.getMeasurements();
        Assert.assertNotNull(actual);
    }
    
    public void testSerialize() throws IOException
    {
        ExceptionData item = new ExceptionData();
        item.setVer(42);
        item.setHandledAt("Test string");
        for (ExceptionDetails entry : new ArrayList<ExceptionDetails>() {{add(new ExceptionDetails());}})
        {
            item.getExceptions().add(entry);
        }
        item.setSeverityLevel(5);
        for (Map.Entry<String, String> entry : new LinkedHashMap<String, String>() {{put("key1", "test value 1"); put("key2", "test value 2"); }}.entrySet())
        {
            item.getProperties().put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : new LinkedHashMap<String, Double>() {{put("key1", 3.1415); put("key2", 42.2); }}.entrySet())
        {
            item.getMeasurements().put(entry.getKey(), entry.getValue());
        }
        StringWriter writer = new StringWriter();
        item.serialize(writer);
        String expected = "{\"ver\":42,\"handledAt\":\"Test string\",\"exceptions\":[{}],\"severityLevel\":5,\"properties\":{\"key1\":\"test value 1\",\"key2\":\"test value 2\"},\"measurements\":{\"key1\":3.1415,\"key2\":42.2}}";
        Assert.assertEquals(expected, writer.toString());
    }

}
