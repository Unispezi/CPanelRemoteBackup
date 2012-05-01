package com.unispezi.cpanelremotebackup;

/**
 * Crude and simple logging system. Use this for debugging, not
 * for talking to the user on console. Use Console for that.
 */
public class Log {
    public static void debug(String logMe){
        System.out.println("DEBUG: " + logMe);
    }

    public static void info(String logMe){
        System.out.println("INFO : " + logMe);
    }
    public static void error(String logMe){
        System.out.println("ERROR: " + logMe);
    }
}

