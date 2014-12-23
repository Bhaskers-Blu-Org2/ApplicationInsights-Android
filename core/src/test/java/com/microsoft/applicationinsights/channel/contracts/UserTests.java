package com.microsoft.applicationinsights.channel.contracts;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// <summary>
/// Data contract test class UserTests.
/// </summary>
public class UserTests extends TestCase
{
    public void testAccount_acquisition_datePropertyWorksAsExpected()
    {
        String expected = "Test string";
        User item = new User();
        item.setAccountAcquisitionDate(expected);
        String actual = item.getAccountAcquisitionDate();
        Assert.assertEquals(expected, actual);
        
        expected = "Other string";
        item.setAccountAcquisitionDate(expected);
        actual = item.getAccountAcquisitionDate();
        Assert.assertEquals(expected, actual);
    }
    
    public void testAccount_idPropertyWorksAsExpected()
    {
        String expected = "Test string";
        User item = new User();
        item.setAccountId(expected);
        String actual = item.getAccountId();
        Assert.assertEquals(expected, actual);
        
        expected = "Other string";
        item.setAccountId(expected);
        actual = item.getAccountId();
        Assert.assertEquals(expected, actual);
    }
    
    public void testUser_agentPropertyWorksAsExpected()
    {
        String expected = "Test string";
        User item = new User();
        item.setUserAgent(expected);
        String actual = item.getUserAgent();
        Assert.assertEquals(expected, actual);
        
        expected = "Other string";
        item.setUserAgent(expected);
        actual = item.getUserAgent();
        Assert.assertEquals(expected, actual);
    }
    
    public void testIdPropertyWorksAsExpected()
    {
        String expected = "Test string";
        User item = new User();
        item.setId(expected);
        String actual = item.getId();
        Assert.assertEquals(expected, actual);
        
        expected = "Other string";
        item.setId(expected);
        actual = item.getId();
        Assert.assertEquals(expected, actual);
    }
    
    public void testSerialize() throws IOException
    {
        User item = new User();
        item.setAccountAcquisitionDate("Test string");
        item.setAccountId("Test string");
        item.setUserAgent("Test string");
        item.setId("Test string");
        StringWriter writer = new StringWriter();
        item.serialize(writer);
        String expected = "{\"ai.user.accountAcquisitionDate\":\"Test string\",\"ai.user.accountId\":\"Test string\",\"ai.user.userAgent\":\"Test string\",\"ai.user.id\":\"Test string\"}";
        Assert.assertEquals(expected, writer.toString());
    }

}
