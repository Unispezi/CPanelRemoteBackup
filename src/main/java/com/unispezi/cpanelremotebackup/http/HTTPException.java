package com.unispezi.cpanelremotebackup.http;

/**
 * HTTP client exception. Hides the Apache Commons HTTP client V4 exception
 * classes in case the client should ever be changed.
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
