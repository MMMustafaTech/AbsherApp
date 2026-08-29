package com.absher.absherapp.request.domain;

/** Raised when a request is asked to make an invalid state transition. */
public class PassportRequestTransitionException extends RuntimeException {

    public PassportRequestTransitionException(String message) {
        super(message);
    }
}
