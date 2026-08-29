package com.absher.absherapp.citizen.application;

public class CitizenAlreadyExistsException extends RuntimeException {

    public CitizenAlreadyExistsException() {
        super("A citizen with this national ID already exists");
    }
}
