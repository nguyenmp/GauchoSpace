package com.nguyenmp.gauchospace.parser;

/**
 * Represents all the errors we can encounter when manipulating and reading XML
 */
public class XMLException extends Exception {
    public XMLException() {
        super();
    }

    public XMLException(String message) {
        super(message);
    }

    public XMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLException(Throwable cause) {
        super(cause);
    }

    protected XMLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
