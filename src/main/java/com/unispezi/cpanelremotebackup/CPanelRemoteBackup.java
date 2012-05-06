package com.unispezi.cpanelremotebackup;

import com.unispezi.cpanelremotebackup.archive.Archiver;
import com.unispezi.cpanelremotebackup.ftp.FTPClient;
import com.unispezi.cpanelremotebackup.http.HTTPClient;
import com.unispezi.cpanelremotebackup.tools.Log;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Main class, contains main function and high level logic
 */
public class CPanelRemoteBackup {
    private static final Pattern BACKUP_FILENAME_PATTERN = Pattern.compile("backup-.*\\.tar\\.gz");
    private static volatile long bytesDownloaded;
    private static final Object asyncThreadMonitor = new Object();

    private String user;
    private String password;
    private String hostName;
    private boolean isSecure;
    private FTPClient ftp;
    private HTTPClient http;
    long timeoutSeconds = 300;
    long pollIntervalSeconds = 15;
    long minFileBytes = 5000;
    String skin = "x3";
    private String outputDirectory;
    private static final int VERIFY_BUFFER_SIZE_BYTES = 100000;

    public CPanelRemoteBackup(String user, String password, String hostName, boolean isSecure, String outputDirectory) {
        this.user = user;
        this.password = password;
        this.hostName = hostName;
        this.isSecure = isSecure;
        this.outputDirectory = outputDirectory;
    }


    public static void main(String[] args) {

        //TODO implement command line options
        String user = System.getProperty("username");
        String password = System.getProperty("password");
        String hostName = System.getProperty("hostname");
        String outputDirectory = System.getProperty("outdir");
        boolean secure = true;

        CPanelRemoteBackup backup = new CPanelRemoteBackup(user, password, hostName, secure, outputDirectory);
        backup.main();
    }


    private void main() {

        // Init
        initFtp();
        initHttp();

        // Read existing backups on server (if any) so we can recognize the new one
        String prevBackupName = findYoungestBackup(ftp, BACKUP_FILENAME_PATTERN);
        String prevBackupNameLogStr = prevBackupName != null ? prevBackupName : "none";
        Log.debug("Youngest backup in home directory before we started: " + prevBackupNameLogStr);

        // Ask server to start new backup
        com.unispezi.cpanelremotebackup.tools.Console.println("Starting CPanel backup");
        triggerBackup();

        // Polling steps (find backup, wait for backup to reach final size) start next. Start the timeout clock
        Date timeoutDate = new Date(new Date().getTime() + timeoutSeconds * 1000);

        // Find backup we just started
        String backupName = findBackupWeStarted(prevBackupName, timeoutDate);
        if (backupName == null) {
            com.unispezi.cpanelremotebackup.tools.Console.println("ERROR: Cannot find the new backup (timeout polling for file)");
            System.exit(1);
        }

        // Wait till CPanel is done writing to it
        boolean isStable = waitForBackupSizeToBecomeStable(timeoutDate, backupName);
        if (!isStable) {
            com.unispezi.cpanelremotebackup.tools.Console.println("ERROR: Backup file size did not become stable in time");
            System.exit(1);
        }

        File downloaded = downloadBackupToFile(backupName, outputDirectory);
        com.unispezi.cpanelremotebackup.tools.Console.print("Verifying downloaded file ...");
        Archiver archiver = new Archiver(VERIFY_BUFFER_SIZE_BYTES);
        if (archiver.verifyDownloadedBackup(downloaded)) {
            com.unispezi.cpanelremotebackup.tools.Console.println("OK");
        } else {
            com.unispezi.cpanelremotebackup.tools.Console.println("ERROR");
            com.unispezi.cpanelremotebackup.tools.Console.println("ERROR: Could not verify downloaded file");
            System.exit(1);
        }

        com.unispezi.cpanelremotebackup.tools.Console.println("Deleting " + backupName + " on server");
        ftp.deleteFile(backupName);

        com.unispezi.cpanelremotebackup.tools.Console.println("Done.");
        ftp.logout();

    }

    private File downloadBackupToFile(String backupName, String outputDirectory) {
        final PipedInputStream ftpDataInStream = new PipedInputStream();
        File outFile = null;
        try {
            PipedOutputStream ftpDataOutStream = new PipedOutputStream(ftpDataInStream);

            outFile = new File(outputDirectory, backupName);
            if (outFile.createNewFile()){
                final FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                new Thread(
                        new Runnable() {
                            public void run() {
                                try {
                                    synchronized (asyncThreadMonitor) {
                                        bytesDownloaded = IOUtils.copy(ftpDataInStream, fileOutputStream);
                                        if (bytesDownloaded > 0) bytesDownloaded += 0;
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException("Exception in copy thread", e);
                                }
                            }
                        }
                ).start();
            } else {
                com.unispezi.cpanelremotebackup.tools.Console.println("ERROR: Cannot create \"" + outFile + "\", file already exists.");
            }

            com.unispezi.cpanelremotebackup.tools.Console.println("Downloading " + backupName + " to " + outFile.getPath());
            ftp.downloadFile(backupName, ftpDataOutStream);
            synchronized (asyncThreadMonitor) {
                com.unispezi.cpanelremotebackup.tools.Console.println("Downloaded " + bytesDownloaded + " bytes successfully");
            }
        } catch (IOException e) {
            com.unispezi.cpanelremotebackup.tools.Console.println("ERROR: Cannot download backup: " + e);
        }
        return outFile;
    }


    private boolean waitForBackupSizeToBecomeStable(Date timeoutDate, String backupName) {
        com.unispezi.cpanelremotebackup.tools.Console.print("Polling for backup file size to become stable");
        long lastFileSize = 0;
        long currentFileSize = 0;
        Date now = new Date();
        while (((currentFileSize < minFileBytes) || (currentFileSize != lastFileSize)) && (now.before(timeoutDate))) {
            com.unispezi.cpanelremotebackup.tools.Console.print(".");
            FTPFile backupWeStarted = ftp.getFileDetails(backupName);
            lastFileSize = currentFileSize;
            currentFileSize = backupWeStarted.getSize();
            try {
                Thread.sleep(pollIntervalSeconds * 1000);
            } catch (InterruptedException e) {
                //Do nothing
            }
            now = new Date();
        }
        com.unispezi.cpanelremotebackup.tools.Console.println(": " + currentFileSize + " bytes");

        return !now.after(timeoutDate);
    }

    private String findBackupWeStarted(String prevBackupName, Date timeoutDate) {
        String backupWeStartedName = null;
        com.unispezi.cpanelremotebackup.tools.Console.print("Polling for backup we just started");
        while ((backupWeStartedName == null) && (new Date().before(timeoutDate))) {
            com.unispezi.cpanelremotebackup.tools.Console.print(".");
            String youngestBackupName = findYoungestBackup(ftp, BACKUP_FILENAME_PATTERN);
            if ((youngestBackupName != null) && ((prevBackupName == null) || (!youngestBackupName.equals(prevBackupName)))) {
                backupWeStartedName = youngestBackupName;
            } else {
                try {
                    Thread.sleep(pollIntervalSeconds * 1000);
                } catch (InterruptedException e) {
                    //Do nothing
                }
            }
        }
        com.unispezi.cpanelremotebackup.tools.Console.println(": " + backupWeStartedName);
        return backupWeStartedName;
    }

    private void triggerBackup() {
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
    }

    private void initHttp() {
        int port;
        if (isSecure) {
            port = 2083;
        } else {
            port = 2082;
        }

        http = new HTTPClient(hostName, port, isSecure, user, password);
    }

    private String findYoungestBackup(FTPClient ftp, Pattern fileNamePattern) {
        List<FTPFile> files = ftp.listFilesInDirectory("/");
        FTPFile youngest = null;

        for (FTPFile file : files) {
            if (fileNamePattern.matcher(file.getName()).matches()) {
                if ((youngest == null) || (youngest.getTimestamp().before(file.getTimestamp()))) {
                    youngest = file;
                }
            }
        }
        if (youngest != null) {
            return youngest.getName();
        } else {
            return null;
        }
    }




    public void initFtp() {
        ftp = new FTPClient(hostName, user, password);
        ftp.connect();
        ftp.login();
    }
}
