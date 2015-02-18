package com.microsoft.applicationinsights.channel.contracts;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import com.microsoft.commonlogging.channel.contracts.Base;

/// <summary>
/// Data contract test class BaseTests.
/// </summary>
public class BaseTests extends TestCase
{
    public void testBase_typePropertyWorksAsExpected()
    {
        String expected = "Test string";
        Base item = new Base();
        item.setBaseType(expected);
        String actual = item.getBaseType();
        Assert.assertEquals(expected, actual);
        
        expected = "Other string";
        item.setBaseType(expected);
        actual = item.getBaseType();
        Assert.assertEquals(expected, actual);
    }
    
    public void testSerialize() throws IOException
    {
        Base item = new Base();
        item.setBaseType("Test string");
        StringWriter writer = new StringWriter();
        item.serialize(writer);
        String expected = "{\"baseType\":\"Test string\"}";
        Assert.assertEquals(expected, writer.toString());
    }

}
