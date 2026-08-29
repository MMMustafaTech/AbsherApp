package com.absher.absherapp.account.application;

public interface LoginUseCase {

    TokenPair login(LoginCommand command);
}
