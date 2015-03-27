/*
 * Generated from CrashDataThread.bond (https://github.com/Microsoft/bond)
*/
package com.microsoft.applicationinsights.channel.contracts;

import com.microsoft.applicationinsights.channel.contracts.shared.IJsonSerializable;
import com.microsoft.applicationinsights.channel.contracts.shared.JsonHelper;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Data contract class CrashDataThread.
 */
public class CrashDataThread implements
    IJsonSerializable
{
    /**
     * Backing field for property Id.
     */
    private int id;
    
    /**
     * Backing field for property Frames.
     */
    private List<CrashDataThreadFrame> frames;
    
    /**
     * Initializes a new instance of the <see cref="CrashDataThread"/> class.
     */
    public CrashDataThread()
    {
        this.InitializeFields();
    }
    
    /**
     * Gets the Id property.
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * Sets the Id property.
     */
    public void setId(int value) {
        this.id = value;
    }
    
    /**
     * Gets the Frames property.
     */
    public List<CrashDataThreadFrame> getFrames() {
        if (this.frames == null) {
            this.frames = new ArrayList<CrashDataThreadFrame>();
        }
        return this.frames;
    }
    
    /**
     * Sets the Frames property.
     */
    public void setFrames(List<CrashDataThreadFrame> value) {
        this.frames = value;
    }
    

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    @Override
    public void serialize(Writer writer) throws IOException
    {
        if (writer == null)
        {
            throw new IllegalArgumentException("writer");
        }
        
        writer.write('{');
        this.serializeContent(writer);
        writer.write('}');
    }

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    protected String serializeContent(Writer writer) throws IOException
    {
        String prefix = "";
        writer.write(prefix + "\"id\":");
        writer.write(JsonHelper.convert(this.id));
        prefix = ",";
        
        if (!(this.frames == null))
        {
            writer.write(prefix + "\"frames\":");
            JsonHelper.writeList(writer, this.frames);
            prefix = ",";
        }
        
        return prefix;
    }
    
    /**
     * Optionally initializes fields for the current context.
     */
    protected void InitializeFields() {
        // method stub to initialize fields for the current context
    }
}
