package com.chari.chariapp.account.application;

public class WeakPasswordException extends RuntimeException {

    public WeakPasswordException() {
        super("Password must contain between 12 and 128 characters");
    }
}
