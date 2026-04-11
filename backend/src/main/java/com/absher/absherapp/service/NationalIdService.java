package com.absher.absherapp.service;

import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.dto.NationalIdResponse;
import com.absher.absherapp.entity.NationalIdentity;
import com.absher.absherapp.repository.NationalIdRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class NationalIdService {

    private final NationalIdRepository
            repository;

    public NationalIdService(NationalIdRepository repository) {
        this.repository = repository;

    }

    public List<NationalIdentity> getAll() {

        return repository.findAll();
    }

    public NationalIdResponse getByIdNumber(String nationalIdNumber) {
        NationalIdentity identity = repository.findByNationalIdNumber(nationalIdNumber)
                .orElseThrow(() -> new NotFoundException("National ID not found"));

        NationalIdResponse response = new NationalIdResponse();
        response.setNationalIdNumber(identity.getNationalIdNumber());
        response.setFirstName(identity.getName());
        response.setLastName(identity.getLastName());
        response.setNationality(identity.getNationality());
        response.setDataofBirth(identity.getDateOfBirth().toString());

        return response;


    }

}
