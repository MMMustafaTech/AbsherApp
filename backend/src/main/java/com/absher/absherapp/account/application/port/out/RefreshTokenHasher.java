package com.absher.absherapp.account.application.port.out;

public interface RefreshTokenHasher {

    String hash(String token);
}
