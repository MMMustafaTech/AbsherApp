package com.absher.absherapp.citizen.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.PhoneReference;

public record RequestCitizenPhoneVerificationCommand(CitizenId citizenId, PhoneReference phone, AccountId operatorId) {
}
