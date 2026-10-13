package com.nisum.bci.exception;

import com.nisum.bci.constants.Constants;

public class EmailAlreadyExistsException extends BciException {
    public EmailAlreadyExistsException() {
        super(Constants.MSG_EMAIL_ALREADY_EXISTS);
    }
}
