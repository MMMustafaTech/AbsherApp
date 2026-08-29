package com.chari.chariapp.citizen.application;

public class CitizenAlreadyExistsException extends RuntimeException {

    public CitizenAlreadyExistsException() {
        super("A citizen with this national ID already exists");
    }
}
