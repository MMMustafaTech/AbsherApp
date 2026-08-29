package com.chari.chariapp.account.application;

import com.chari.chariapp.account.domain.AccountId;

public interface CreateAccountUseCase {

    AccountId create(CreateAccountCommand command);
}
