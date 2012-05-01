package com.unispezi.cpanelremotebackup.ftp;

/**
 * FTP client exception. Hides the Apache FTP client exception
 * classes in case the client should ever be changed.
 */
public class FTPException extends RuntimeException {
    public FTPException() {
    }

    public FTPException(String message) {
        super(message);
    }

    public FTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public FTPException(Throwable cause) {
        super(cause);
    }
}
