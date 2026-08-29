package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.AccountId;

public interface CreateAccountUseCase {

    AccountId create(CreateAccountCommand command);
}
