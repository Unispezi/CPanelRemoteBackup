package com.unispezi.cpanelremotebackup.tools;

/**
 * Crude and simple logging system. Use this for debugging, not
 * for talking to the user on console. Use Console for that.
 * TODO implement reasonable log system here
 */
public class Log {
   static boolean logToConsole = false;

    public static void debug(String logMe){
        if (logToConsole){
            System.out.println("DEBUG: " + logMe);
        }
    }

    public static void info(String logMe){
        if (logToConsole){
            System.out.println("INFO : " + logMe);
        }
    }
    public static void error(String logMe){
        if (logToConsole){
            System.out.println("ERROR: " + logMe);
        }
    }
    public static void error(String logMe, Throwable exception){
        error(logMe);
        if (logToConsole){
            exception.printStackTrace(System.out);
        }
    }
}

