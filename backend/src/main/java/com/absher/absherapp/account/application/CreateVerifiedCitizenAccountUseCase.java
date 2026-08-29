package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.AccountId;

public interface CreateVerifiedCitizenAccountUseCase {

    AccountId create(CreateVerifiedCitizenAccountCommand command);
}
