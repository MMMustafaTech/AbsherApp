package com.chari.chariapp.account.application;

import com.chari.chariapp.account.domain.AccountId;

public interface CreateVerifiedCitizenAccountUseCase {

    AccountId create(CreateVerifiedCitizenAccountCommand command);
}
