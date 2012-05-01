package com.unispezi.cpanelremotebackup.ftp;

/**
 * Created with IntelliJ IDEA.
 * User: Carsten
 * Date: 01.05.12
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
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
