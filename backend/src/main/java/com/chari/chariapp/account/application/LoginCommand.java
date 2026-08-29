package com.chari.chariapp.account.application;

public record LoginCommand(String emailLookup, String rawPassword) {
}
