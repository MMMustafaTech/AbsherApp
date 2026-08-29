package com.chari.chariapp.citizen.application;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.citizen.domain.PhoneReference;

public record RequestCitizenPhoneVerificationCommand(CitizenId citizenId, PhoneReference phone, AccountId operatorId) {
}
