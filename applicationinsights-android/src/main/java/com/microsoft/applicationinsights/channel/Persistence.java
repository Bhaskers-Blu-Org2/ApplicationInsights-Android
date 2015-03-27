package com.microsoft.applicationinsights.channel;
import android.content.Context;

import com.microsoft.applicationinsights.channel.contracts.shared.IJsonSerializable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

public class Persistence {

    /**
     * Volatile boolean for double checked synchronize block
     */
    private static volatile boolean isPersistenceLoaded = false;

    /**
     * Synchronization LOCK for setting static context
     */
    private static final Object LOCK = new Object();

    /**
     * The file path for persisted data
     */
    private static final String FILE_PATH = "appInsightsData.json";

    /**
     * The tag for logging
     */
    private static final String TAG = "Persistence";

    /**
     * The singleton INSTANCE of this class
     */
    private static Persistence instance;

    /**
     * A weak reference to the app context
     */
    private WeakReference<Context> weakContext;

    /**
     * Restrict access to the default constructor
     */
    protected Persistence(Context context) {
        this.weakContext = new WeakReference<Context>(context);
    }

    /**
     * Initialize the INSTANCE of persistence
     *
     * @param context the app context for the INSTANCE
     */
    public static void initialize(Context context) {
        // note: isPersistenceLoaded must be volatile for the double-checked LOCK to work
        if (!Persistence.isPersistenceLoaded) {
            synchronized (Persistence.LOCK) {
                if (!Persistence.isPersistenceLoaded) {
                    Persistence.isPersistenceLoaded = true;
                    Persistence.instance = new Persistence(context);
                }
            }
        }
    }

    /**
     * @return the INSTANCE of persistence or null if not yet initialized
     */
    public static Persistence getInstance() {
        if (Persistence.instance == null) {
            InternalLogging.error(TAG, "getInstance was called before initialization");
        }

        return Persistence.instance;
    }

    /**
     * Serializes the input and calls:
     *
     * @see Persistence#persist(String)
     */
    public boolean persist(IJsonSerializable[] data) {
        StringBuilder buffer = new StringBuilder();
        Boolean isSuccess = false;
        try {
            buffer.append('[');
            for (int i = 0; i < data.length; i++) {
                if (i > 0) {
                    buffer.append(',');
                }
                StringWriter stringWriter = new StringWriter();
                data[i].serialize(stringWriter);
                buffer.append(stringWriter.toString());
            }

            buffer.append(']');
            String serializedData = buffer.toString();
            isSuccess = this.persist(serializedData);
        } catch (IOException e) {
            InternalLogging.error(TAG, e.toString());
            return false;
        }

        return isSuccess;
    }

    /**
     * Saves a collection of IJsonSerializable objects to disk
     *
     * @param data the serializable collection to save
     * @return true if the operation was successful, false otherwise
     */
    public boolean persist(String data) {
        Boolean isSuccess = false;
        if (weakContext != null) {
            Context context = weakContext.get();
            if (context != null) {
                FileOutputStream outputStream;
                try {
                    outputStream = context.openFileOutput(FILE_PATH, Context.MODE_PRIVATE);
                    outputStream.write(data.getBytes());
                    outputStream.close();
                    isSuccess = true;
                } catch (Exception e) {
                    //Do nothing
                    InternalLogging.error(TAG, "Error writing telemetry data to file");
                }
            }
        }

        return isSuccess;
    }

    /**
     * Retrieves and deletes the next item from disk todo: support multiple items on disk
     *
     * @return the next item from disk or empty string if anything goes wrong
     */
    public String getNextItemFromDisk() {
        StringBuilder buffer = new StringBuilder();
        if (weakContext != null) {
            Context context = weakContext.get();
            if (context != null) {
                try {
                    FileInputStream inputStream = context.openFileInput(FILE_PATH);
                    InputStreamReader streamReader = new InputStreamReader(inputStream);

                    BufferedReader reader = new BufferedReader(streamReader);
                    String str;
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }

                    reader.close();
                } catch (Exception e) {
                    InternalLogging.error(TAG, "Error reading telemetry data from file");
                }

                // always delete the file
                context.deleteFile(FILE_PATH);
            }
        }

        return buffer.toString();
    }
}