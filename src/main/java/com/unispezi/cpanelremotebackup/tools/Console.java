package com.unispezi.cpanelremotebackup.tools;

/**
 * Use this to talk to the user on console. Hides the real
 * System.out away so this could  be changed later to something other
 *
 */
public class Console {
    public static void println(String text){
        System.out.println(text);
    }

    public static void print(String text){
        System.out.print(text);
    }
}
