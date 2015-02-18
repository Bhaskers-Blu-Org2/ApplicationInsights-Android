package com.microsoft.applicationinsights.channel.contracts;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import com.microsoft.commonlogging.channel.contracts.shared.ITelemetry;
import com.microsoft.commonlogging.channel.contracts.shared.ITelemetryData;
import com.microsoft.commonlogging.channel.contracts.shared.IContext;
import com.microsoft.commonlogging.channel.contracts.shared.IJsonSerializable;
import com.microsoft.commonlogging.channel.contracts.shared.JsonHelper;

/**
 * Data contract class MetricData.
 */
public class MetricData extends Domain implements
    ITelemetry
{
    /**
     * Envelope Name for this telemetry.
     */
    public String getEnvelopeName() {
        return "Microsoft.ApplicationInsights.Metric";
    }
    
    /**
     * Base Type for this telemetry.
     */
    public String getBaseType() {
        return "Microsoft.ApplicationInsights.MetricData";
    }
    
    /**
     * Backing field for property Ver.
     */
    private int ver = 2;
    
    /**
     * Backing field for property Metrics.
     */
    private ArrayList<DataPoint> metrics;
    
    /**
     * Backing field for property Properties.
     */
    private LinkedHashMap<String, String> properties;
    
    /**
     * Initializes a new instance of the <see cref="MetricData"/> class.
     */
    public MetricData()
    {
        this.InitializeFields();
    }
    
    /**
     * Gets the Ver property.
     */
    public int getVer() {
        return this.ver;
    }
    
    /**
     * Sets the Ver property.
     */
    public void setVer(int value) {
        this.ver = value;
    }
    
    /**
     * Gets the Metrics property.
     */
    public ArrayList<DataPoint> getMetrics() {
        if (this.metrics == null) {
            this.metrics = new ArrayList<DataPoint>();
        }
        return this.metrics;
    }
    
    /**
     * Sets the Metrics property.
     */
    public void setMetrics(ArrayList<DataPoint> value) {
        this.metrics = value;
    }
    
    /**
     * Gets the Properties property.
     */
    public LinkedHashMap<String, String> getProperties() {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<String, String>();
        }
        return this.properties;
    }
    
    /**
     * Sets the Properties property.
     */
    public void setProperties(LinkedHashMap<String, String> value) {
        this.properties = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = super.serializeContent(writer);
        writer.write(prefix + "\"ver\":");
        writer.write(JsonHelper.convert(this.ver));
        prefix = ",";
        
        writer.write(prefix + "\"metrics\":");
        JsonHelper.writeList(writer, this.metrics);
        prefix = ",";
        
        if (!(this.properties == null))
        {
            writer.write(prefix + "\"properties\":");
            JsonHelper.writeDictionary(writer, this.properties);
            prefix = ",";
        }
        
        return prefix;
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        
    }
}
