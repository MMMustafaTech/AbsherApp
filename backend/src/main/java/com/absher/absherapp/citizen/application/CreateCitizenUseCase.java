package com.absher.absherapp.citizen.application;

import com.absher.absherapp.citizen.domain.CitizenId;

public interface CreateCitizenUseCase {

    CitizenId create(CreateCitizenCommand command);
}
