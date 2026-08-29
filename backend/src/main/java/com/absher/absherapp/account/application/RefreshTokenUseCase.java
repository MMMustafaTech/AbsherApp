package com.absher.absherapp.account.application;

public interface RefreshTokenUseCase {

    TokenPair refresh(String refreshToken);
}
