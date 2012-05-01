package com.unispezi.cpanelremotebackup.connector;

/**
 * Created with IntelliJ IDEA.
 * User: Carsten
 * Date: 01.05.12
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class HTTPException extends RuntimeException {
    public HTTPException() {
    }

    public HTTPException(String message) {
        super(message);
    }

    public HTTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public HTTPException(Throwable cause) {
        super(cause);
    }
}
