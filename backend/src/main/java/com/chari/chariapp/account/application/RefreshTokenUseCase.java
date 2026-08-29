package com.chari.chariapp.account.application;

public interface RefreshTokenUseCase {

    TokenPair refresh(String refreshToken);
}
