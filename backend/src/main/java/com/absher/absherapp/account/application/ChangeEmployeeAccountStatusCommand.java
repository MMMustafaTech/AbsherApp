package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountStatus;

public record ChangeEmployeeAccountStatusCommand(AccountId administratorId, AccountId employeeId, AccountStatus status) {
}
