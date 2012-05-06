/*  Copyright (C) 2012 Carsten Lergenmüller

    Permission is hereby granted, free of charge, to any person obtaining a copy of this
    software and associated documentation files (the "Software"), to deal in the Software
    without restriction, including without limitation the rights to use, copy, modify,
    merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
    permit persons to whom the Software is furnished to do so, subject to the following
    conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
        INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
        PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
        HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
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

