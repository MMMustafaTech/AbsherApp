package com.absher.absherapp.account.application;

public record LoginCommand(String emailLookup, String rawPassword) {
}
