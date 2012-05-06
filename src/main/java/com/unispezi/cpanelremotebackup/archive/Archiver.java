package com.unispezi.cpanelremotebackup.archive;

import com.unispezi.cpanelremotebackup.tools.Log;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;

/**
 * Contains all logic that deals with archives (tar / gz)
 */
public class Archiver {
    byte[] buffer;

    public Archiver(int bufferSizeBytes) {
        buffer = new byte[bufferSizeBytes];
    }

    public boolean verifyDownloadedBackup(File backup) {

        File unzipped = unzipFile(backup);
        return verifyTar(unzipped);
    }

    public  boolean verifyTar(File file) {
        // unzip
        FileInputStream fin;
        String logString = "ERROR: Unzipping \"" + file + "\" failed";
        try {
            TarArchiveInputStream tarIn = null;
            try {
                fin = new FileInputStream(file);
                tarIn = new TarArchiveInputStream(fin);

                // Iterate over fileEntry and read each
                ArchiveEntry entry;
                while((entry = tarIn.getNextEntry()) != null) {

                    int read = 0;
                    while((read += tarIn.read(buffer)) < entry.getSize()) {
                        //Just read, will crash on problem
                    }
                }

            } finally {

                if (tarIn != null){
                    tarIn.close();
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            handleException(logString, e);
        } catch (IOException e) {
            handleException(logString, e);
        }
        return false;
    }

    public File unzipFile(File backup) {
        // unzip
        FileInputStream fin;
        File unzippedFile = null;
        String logString = "ERROR: Unzipping \"" + backup + "\" failed";
        try {
            FileOutputStream out = null;
            GzipCompressorInputStream gzIn = null;
            try {
                fin = new FileInputStream(backup);
                BufferedInputStream in = new BufferedInputStream(fin);
                String tempDirPath = System.getProperty("java.io.tmpdir");
                File tempDir = new File(tempDirPath);
                unzippedFile = new File(tempDir, "CPanelRemoteBackup.tmp");
                unzippedFile.deleteOnExit();
                out = new FileOutputStream(unzippedFile);
                gzIn = new GzipCompressorInputStream(in);
                int read;
                while (-1 != (read = gzIn.read(buffer))) {
                    out.write(buffer, 0, read);
                }
            } finally {
                if (out != null) {
                    out.close();
                }
                if (gzIn != null) {
                    gzIn.close();
                }
            }
        } catch (FileNotFoundException e) {
            handleException(logString, e);
        } catch (IOException e) {
            handleException(logString, e);
        }
        return unzippedFile;
    }

    private void handleException(String logMe, Throwable e) {
        logMe = logMe + ":" + e;
        com.unispezi.cpanelremotebackup.tools.Console.println(logMe);
        Log.error(logMe, e);
    }
}
