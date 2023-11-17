package com.twd.setting.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.UnknownHostException;

public final class HLog {
    public static final HLog INSTANCE = new HLog();
    public static final int LOG_LEVEL_ALL = -1;
    private static final int LOG_LEVEL_DEBUG = 0;
    public static final int LOG_LEVEL_ERROR = 1;
    private static final String appTag = "Settings-";
    private static int logLevel = 1;

    public static final void d(String paramString1, String paramString2) {
        HLog localHLog = INSTANCE;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Settings-");
        localStringBuilder.append(paramString1);
        localHLog.printLog(localStringBuilder.toString(), paramString2, 0);
    }

    public static final void e(String paramString1, String paramString2) {
        HLog localHLog = INSTANCE;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Settings-");
        localStringBuilder.append(paramString1);
        localHLog.printLog(localStringBuilder.toString(), paramString2, 1);
    }

    public static final void e(String paramString1, String paramString2, Throwable paramThrowable) {
        HLog localHLog = INSTANCE;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Settings-");
        localStringBuilder.append(paramString1);
        paramString1 = localStringBuilder.toString();
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramString2);
        localStringBuilder.append(" : ");
        localStringBuilder.append(localHLog.getStackTraceString(paramThrowable));
        localHLog.printLog(paramString1, localStringBuilder.toString(), 1);
    }

    public static final void e(String paramString, Throwable paramThrowable) {
        HLog localHLog = INSTANCE;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Settings-");
        localStringBuilder.append(paramString);
        localHLog.printLog(localStringBuilder.toString(), localHLog.getStackTraceString(paramThrowable), 1);
    }

    private String getStackTraceString(Throwable paramThrowable) {
        if (paramThrowable == null) {
            return "";
        }
        for (Object localObject = paramThrowable; localObject != null; localObject = ((Throwable) localObject).getCause()) {
            if ((localObject instanceof UnknownHostException)) {
                return "";
            }
        }
        Writer writer = new StringWriter();
        PrintWriter localPrintWriter = new PrintWriter((Writer) writer);
        paramThrowable.printStackTrace(localPrintWriter);
        localPrintWriter.flush();
        return ((StringWriter) writer).toString();
    }

    private final void printLog(String paramString1, String paramString2, int paramInt) {
        int i = logLevel;
        if (((i != 0) || (paramInt == 0)) && ((i != 1) || (paramInt == 1))) {
            int j;
            for (i = 0; i < paramString2.length(); i = j) {
                int k = paramString2.length();
                j = i + 3000;
                String str;
                if (k <= j) {
                    str = paramString2.substring(i);
                } else {
                    str = paramString2.substring(i, j);
                }
                if (paramInt == 0) {
                    Log.d(paramString1, str);
                } else {
                    Log.e(paramString1, str);
                }
            }
        }
    }

    public final int getLogLevel() {
        return logLevel;
    }

    public final void setLogLevel(int paramInt) {
        logLevel = paramInt;
    }
}
