package com.nisum.bci.exception;

public class BciException extends RuntimeException {
    public BciException(String msg) {
        super(msg);
    }

    public BciException(String msg, Exception exp) {
        super(msg, exp);
    }

    public BciException(Throwable throwable) {
        super(throwable);
    }
}
