package com.absher.absherapp.request.application;

/** A client-safe conflict with the current passport request workflow state. */
public class PassportRequestConflictException extends RuntimeException {

    public PassportRequestConflictException(String message) {
        super(message);
    }
}
