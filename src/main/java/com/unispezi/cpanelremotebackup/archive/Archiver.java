package com.unispezi.cpanelremotebackup.archive;

import com.unispezi.cpanelremotebackup.tools.Log;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Contains all logic that deals with archives (tar / gz)
 */
public class Archiver {
    byte[] buffer;

    public Archiver(int bufferSizeBytes) {
        buffer = new byte[bufferSizeBytes];
    }

    public boolean verifyDownloadedBackup(File backup) {

        String logString = "ERROR: Verifying \"" + backup + "\" failed";

        InputStream unzipped = unzipFile(backup);
        try {
            try {
                while (-1 != unzipped.read(buffer)) { }
            } finally {
                unzipped.close();
            }
            return true;
        } catch (FileNotFoundException e) {
            handleException(logString, e);
        } catch (IOException e) {
            handleException(logString, e);
        }
        return false;
    }

    public InputStream unzipFile(File backup) {
        // unzip
        GZIPInputStream gzIn = null;
        String logString = "ERROR: Unzipping \"" + backup + "\" failed";
        try {
            FileInputStream fin = new FileInputStream(backup);
            gzIn = new GZIPInputStream(fin);
        } catch (FileNotFoundException e) {
            handleException(logString, e);
        } catch (IOException e) {
            handleException(logString, e);
        }
        return gzIn;
    }

    private void handleException(String logMe, Throwable e) {
        logMe = logMe + ":" + e;
        com.unispezi.cpanelremotebackup.tools.Console.println(logMe);
        Log.error(logMe, e);
    }
}
