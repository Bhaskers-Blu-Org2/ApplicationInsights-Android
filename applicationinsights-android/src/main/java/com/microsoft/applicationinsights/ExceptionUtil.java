package com.microsoft.applicationinsights;

import com.microsoft.applicationinsights.contracts.CrashData;
import com.microsoft.applicationinsights.contracts.CrashDataHeaders;
import com.microsoft.applicationinsights.contracts.CrashDataThread;
import com.microsoft.applicationinsights.contracts.CrashDataThreadFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExceptionUtil {

    //TODO get packageName from somewhere else
    public static CrashData getCrashData(Throwable exception, Map<String, String> properties, String packageName) {
        Throwable localException = exception;
        if (localException == null) {
            localException = new Exception();
        }

        // TODO: set handletAt
        // read stack frames
        List<CrashDataThreadFrame> stackFrames = new ArrayList<>();
        StackTraceElement[] stack = localException.getStackTrace();
        for (int i = stack.length - 1; i >= 0; i--) {
            StackTraceElement rawFrame = stack[i];
            CrashDataThreadFrame frame = new CrashDataThreadFrame();
            frame.setSymbol(rawFrame.toString());
            stackFrames.add(frame);
            frame.setAddress("");
        }

        CrashDataThread crashDataThread = new CrashDataThread();
        crashDataThread.setFrames(stackFrames);
        List<CrashDataThread> threads = new ArrayList<>(1);
        threads.add(crashDataThread);

        CrashDataHeaders crashDataHeaders = new CrashDataHeaders();
        crashDataHeaders.setId(UUID.randomUUID().toString());

        String message = localException.getMessage();
        crashDataHeaders.setExceptionReason(ensureNotNull(message));
        crashDataHeaders.setExceptionType(localException.getClass().getName());
        crashDataHeaders.setApplicationPath(packageName);

        CrashData crashData = new CrashData();
        crashData.setThreads(threads);
        crashData.setHeaders(crashDataHeaders);
        crashData.setProperties(properties);

        return crashData;
    }


    /**
     * Ensures required string values are non-null
     */
    private static String ensureNotNull(String input) {
        if (input == null) {
            return "";
        } else {
            return input;
        }
    }
}
