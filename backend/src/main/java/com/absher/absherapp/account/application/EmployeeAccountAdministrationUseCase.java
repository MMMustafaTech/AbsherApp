package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.AccountId;

public interface EmployeeAccountAdministrationUseCase {
    AccountId provisionEmployee(ProvisionEmployeeAccountCommand command);
    void changeEmployeeStatus(ChangeEmployeeAccountStatusCommand command);
}
