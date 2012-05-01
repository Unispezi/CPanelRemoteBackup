package com.unispezi.cpanelremotebackup;

import com.unispezi.cpanelremotebackup.connector.HTTPClient;
import com.unispezi.cpanelremotebackup.ftp.FTPClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Carsten
 * Date: 01.05.12
 * Time: 10:54
 * To change this template use File | Settings | File Templates.
 */
public class CPanelRemoteBackup {
    private static final Pattern BACKUP_FILENAME_PATTERN = Pattern.compile("backup-.*\\.tar\\.gz");
    private static volatile long bytesDownloaded;
    private static Object asyncThreadMonitor = new Object();

    public static void main(String[] args){
        String user = System.getProperty("username");
        String password = System.getProperty("password");
        String hostName = System.getProperty("hostname");
        String outputDirectory = System.getProperty("outdir");
        long timeoutSeconds = 300;
        long pollIntervalSeconds = 15;
        long minFileBytes = 5000;

        FTPClient ftp = new FTPClient(hostName, user, password);
        ftp.connect();
        ftp.login();

        FTPFile youngestBackupBeforeTheOneWeStarted = findYoungestBackup(ftp, BACKUP_FILENAME_PATTERN);

        String backupName = youngestBackupBeforeTheOneWeStarted == null ? "none" : youngestBackupBeforeTheOneWeStarted.getName();
        Console.println("Youngest backup in home directory before we started:" + backupName);

        int port;
        boolean secure = true;
        String skin = "x3";

        if (secure) {
            port = 2083;
        } else {
            port = 2082;
        }

        HTTPClient http = new HTTPClient(hostName, port, secure, user, password);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dest", "homedir"); //Put into home directory
        params.put("email_radio", 0); //No email
        params.put("server", ""); //Needed for dest = remote ftp directory only
        params.put("port", "");   //Needed for dest = remote ftp directory only
        params.put("rdir", "");   //Needed for dest = remote ftp directory only
        params.put("user", user);
        params.put("pass", password);

        String uri = "/frontend/" + skin + "/backup/dofullbackup.html";
        http.post(uri, params);

        FTPFile backupWeStarted = null;
        Date start = new Date();
        Date timeoutDate = new Date(start.getTime() + timeoutSeconds * 1000);
        Console.print("Polling for backup we just started");
        while ((backupWeStarted == null) && (new Date().before(timeoutDate))){
            Console.print(".");
            FTPFile currentYoungest = findYoungestBackup(ftp, BACKUP_FILENAME_PATTERN);
            if ((currentYoungest != null) && ((youngestBackupBeforeTheOneWeStarted == null) || (!currentYoungest.getName().equals(youngestBackupBeforeTheOneWeStarted.getName())))){
                backupWeStarted = currentYoungest;
            } else {
                try {
                    Thread.sleep(pollIntervalSeconds * 1000);
                } catch (InterruptedException e) {
                    //Do nothing
                }
            }
        }
        Console.println("");

        if (backupWeStarted != null){

            Console.print("Polling for backup file size to become stable");
            long lastFileSize = 0;
            long currentFileSize = 0;
            Date now = new Date();
            while (((currentFileSize < minFileBytes) || (currentFileSize != lastFileSize)) && (now.before(timeoutDate))){
                Console.print(".");
                backupWeStarted = ftp.getFileDetails(backupWeStarted.getName());
                lastFileSize = currentFileSize;
                currentFileSize = backupWeStarted.getSize();
                try {
                    Thread.sleep(pollIntervalSeconds * 1000);
                } catch (InterruptedException e) {
                    //Do nothing
                }
                now = new Date();
            }
            Console.println("");

            if (now.after(timeoutDate)){
                Console.println("ERROR: Backup file size did not become stable in time");
                System.exit(1);
            }

            Console.println("Backup file size is stable, starting download");

            final PipedInputStream ftpDataInStream = new PipedInputStream();
            try {
                PipedOutputStream ftpDataOutStream = new PipedOutputStream(ftpDataInStream);


            File outFile = new File(outputDirectory, backupWeStarted.getName());
                outFile.createNewFile();
                final FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                new Thread(
                        new Runnable(){
                            public void run(){
                                try {
                                    synchronized (asyncThreadMonitor){
                                        bytesDownloaded = IOUtils.copy(ftpDataInStream, fileOutputStream);
                                        if (bytesDownloaded > 0) bytesDownloaded +=0;
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException("Exception in copy thread", e);
                                }
                            }
                        }
                ).start();

                Console.println("Downloading " + backupWeStarted.getName() + " to " + outFile.getPath());
                ftp.downloadFile(backupWeStarted.getName(), ftpDataOutStream);
                synchronized (asyncThreadMonitor){
                    Console.println("Downloaded " + bytesDownloaded + " bytes successfully");
                }

                Console.println("Deleting " + backupWeStarted.getName() + " on server");
                ftp.deleteFile(backupWeStarted.getName());

                Console.println("Done.");
                ftp.logout();

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            System.exit(0);


        } else {
            Console.println("ERROR: Cannot find the new backup");
            System.exit(1);
        }
    }

    private static FTPFile findYoungestBackup(FTPClient ftp, Pattern fileNamePattern) {
        List<FTPFile> files = ftp.listFilesInDirectory("/");
        FTPFile youngest = null;

        for (FTPFile file: files){
            if (fileNamePattern.matcher(file.getName()).matches()){
                if ((youngest == null) || (youngest.getTimestamp().before(file.getTimestamp()))){
                    youngest = file;
                }
            }
        }
        return youngest;
    }

}
