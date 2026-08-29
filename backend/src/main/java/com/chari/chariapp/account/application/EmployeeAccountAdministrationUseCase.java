package com.chari.chariapp.account.application;

import com.chari.chariapp.account.domain.AccountId;

public interface EmployeeAccountAdministrationUseCase {
    AccountId provisionEmployee(ProvisionEmployeeAccountCommand command);
    void changeEmployeeStatus(ChangeEmployeeAccountStatusCommand command);
}
