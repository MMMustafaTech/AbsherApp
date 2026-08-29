package com.absher.absherapp.account.application;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String field) {
        super("An account already exists for " + field);
    }
}
