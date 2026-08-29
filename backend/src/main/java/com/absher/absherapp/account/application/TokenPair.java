package com.absher.absherapp.account.application;

import java.time.Instant;

public record TokenPair(String accessToken, Instant accessTokenExpiresAt, String refreshToken) {
}
