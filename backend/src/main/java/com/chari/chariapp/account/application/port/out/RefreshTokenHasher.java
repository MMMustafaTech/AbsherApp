package com.chari.chariapp.account.application.port.out;

public interface RefreshTokenHasher {

    String hash(String token);
}
