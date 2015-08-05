package com.microsoft.applicationinsights.contracts;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;

/// <summary>
/// Data contract test class DataPointTests.
/// </summary>
public class DataPointTests extends TestCase {
    public void testNamePropertyWorksAsExpected() {
        String expected = "Test string";
        DataPoint item = new DataPoint();
        item.setName(expected);
        String actual = item.getName();
        Assert.assertEquals(expected, actual);

        expected = "Other string";
        item.setName(expected);
        actual = item.getName();
        Assert.assertEquals(expected, actual);
    }

    public void testKindPropertyWorksAsExpected() {
        DataPoint item = new DataPoint();
        item.setKind(DataPointType.MEASUREMENT);
        int actual = item.getKind().getValue();
        Assert.assertEquals(DataPointType.MEASUREMENT.getValue(), actual);

        item.setKind(DataPointType.AGGREGATION);
        actual = item.getKind().getValue();
        Assert.assertEquals(DataPointType.AGGREGATION.getValue(), actual);
    }

    public void testValuePropertyWorksAsExpected() {
        double expected = 1.5;
        DataPoint item = new DataPoint();
        item.setValue(expected);
        double actual = item.getValue();
        Assert.assertEquals(expected, actual);

        expected = 4.8;
        item.setValue(expected);
        actual = item.getValue();
        Assert.assertEquals(expected, actual);
    }

    public void testCountPropertyWorksAsExpected() {
        Integer expected = 42;
        DataPoint item = new DataPoint();
        item.setCount(expected);
        Integer actual = item.getCount();
        Assert.assertEquals(expected, actual);

        expected = 13;
        item.setCount(expected);
        actual = item.getCount();
        Assert.assertEquals(expected, actual);
    }

    public void testMinPropertyWorksAsExpected() {
        Double expected = 1.5;
        DataPoint item = new DataPoint();
        item.setMin(expected);
        Double actual = item.getMin();
        Assert.assertEquals(expected, actual);

        expected = 4.8;
        item.setMin(expected);
        actual = item.getMin();
        Assert.assertEquals(expected, actual);
    }

    public void testMaxPropertyWorksAsExpected() {
        Double expected = 1.5;
        DataPoint item = new DataPoint();
        item.setMax(expected);
        Double actual = item.getMax();
        Assert.assertEquals(expected, actual);

        expected = 4.8;
        item.setMax(expected);
        actual = item.getMax();
        Assert.assertEquals(expected, actual);
    }

    public void testStd_devPropertyWorksAsExpected() {
        Double expected = 1.5;
        DataPoint item = new DataPoint();
        item.setStdDev(expected);
        Double actual = item.getStdDev();
        Assert.assertEquals(expected, actual);

        expected = 4.8;
        item.setStdDev(expected);
        actual = item.getStdDev();
        Assert.assertEquals(expected, actual);
    }

    public void testSerializeMeasurement() throws IOException {
        DataPoint item = new DataPoint();
        item.setName("Test string");
        item.setKind(DataPointType.MEASUREMENT);
        item.setValue(1.5);
        item.setCount(42);
        item.setMin(1.5);
        item.setMax(1.5);
        item.setStdDev(1.5);
        StringWriter writer = new StringWriter();
        item.serialize(writer);
        String expected = "{\"name\":\"Test string\",\"value\":1.5,\"count\":42,\"min\":1.5,\"max\":1.5,\"stdDev\":1.5}";
        Assert.assertEquals(expected, writer.toString());
    }

    public void testSerializeAggregation() throws IOException {
        DataPoint item = new DataPoint();
        item.setName("Test string");
        item.setKind(DataPointType.AGGREGATION);
        item.setValue(1.5);
        item.setCount(42);
        item.setMin(1.5);
        item.setMax(1.5);
        item.setStdDev(1.5);
        StringWriter writer = new StringWriter();
        item.serialize(writer);
        String expected = "{\"name\":\"Test string\",\"kind\":1,\"value\":1.5,\"count\":42,\"min\":1.5,\"max\":1.5,\"stdDev\":1.5}";
        Assert.assertEquals(expected, writer.toString());
    }
}
