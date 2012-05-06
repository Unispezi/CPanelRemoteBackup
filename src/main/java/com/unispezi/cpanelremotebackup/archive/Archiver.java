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
