package com.chari.chariapp.citizen.application;

import com.chari.chariapp.citizen.domain.CitizenId;

public interface CreateCitizenUseCase {

    CitizenId create(CreateCitizenCommand command);
}
