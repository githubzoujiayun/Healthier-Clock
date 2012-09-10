package com.jkydjk.healthier.clock.util;

import android.util.Config;

public class Log {
    public final static String LOGTAG = "AlarmClock";

    /**
     * This must be false for production. If true, turns on logging, test code,
     * etc.
     */
    public static final boolean DEBUG = true;

    public static final boolean LOGV = DEBUG ? Config.LOGD : Config.LOGV;

    public static void v(String log) {
        Throwable t = new Throwable();
        String className = t.getStackTrace()[1].getClassName();
        android.util.Log.v(className, log);
    }

    public static void e(String log) {
        android.util.Log.e(LOGTAG, log);
    }

    public static void e(String log, Exception ex) {
        android.util.Log.e(LOGTAG, log, ex);
    }
}
