package com.chari.chariapp.account.application;

public interface LoginUseCase {

    TokenPair login(LoginCommand command);
}
