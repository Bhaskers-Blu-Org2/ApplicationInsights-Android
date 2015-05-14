package com.microsoft.applicationinsights.contracts;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import com.microsoft.telemetry.ITelemetry;
import com.microsoft.telemetry.ITelemetryData;
import com.microsoft.telemetry.IContext;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.Domain;
import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.JsonHelper;

/**
 * Data contract class MessageData.
 */
public class MessageData extends Domain implements
    ITelemetry
{
    /**
     * Backing field for property Ver.
     */
    private int ver = 2;
    
    /**
     * Backing field for property Message.
     */
    private String message;
    
    /**
     * Backing field for property SeverityLevel.
     */
    private com.microsoft.applicationinsights.contracts.Client.Contracts.SeverityLevel severityLevel;
    
    /**
     * Backing field for property Properties.
     */
    private LinkedHashMap<String, String> properties;
    
    /**
     * Initializes a new instance of the <see cref="MessageData"/> class.
     */
    public MessageData()
    {
        this.InitializeFields();
        this.SetupAttributes();
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
     * Gets the Message property.
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Sets the Message property.
     */
    public void setMessage(String value) {
        this.message = value;
    }
    
    /**
     * Gets the SeverityLevel property.
     */
    public com.microsoft.applicationinsights.contracts.Client.Contracts.SeverityLevel getSeverityLevel() {
        return this.severityLevel;
    }
    
    /**
     * Sets the SeverityLevel property.
     */
    public void setSeverityLevel(com.microsoft.applicationinsights.contracts.Client.Contracts.SeverityLevel value) {
        this.severityLevel = value;
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
        
        writer.write(prefix + "\"message\":");
        writer.write(JsonHelper.convert(this.message));
        prefix = ",";
        
        if (!(this.severityLevel == 0))
        {
            writer.write(prefix + "\"severityLevel\":");
            writer.write(JsonHelper.convert(this.severityLevel));
            prefix = ",";
        }
        
        if (!(this.properties == null))
        {
            writer.write(prefix + "\"properties\":");
            JsonHelper.writeDictionary(writer, this.properties);
            prefix = ",";
        }
        
        return prefix;
    }
    
    /**
     * Sets up the events attributes
     */
    public void SetupAttributes()
    {
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        QualifiedName = "com.microsoft.applicationinsights.contracts.MessageData";
    }
}
