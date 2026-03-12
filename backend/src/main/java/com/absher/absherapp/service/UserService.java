package com.absher.absherapp.service;

import com.absher.absherapp.entity.User;
import com.absher.absherapp.repository.UserRepository;
import com.absher.absherapp.repository.NationalIdentityRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final NationalIdentityRepository nationalRepository;

    public UserService(UserRepository userRepository,
                       NationalIdentityRepository nationalRepository) {
        this.userRepository = userRepository;
        this.nationalRepository = nationalRepository;
    }

    public void register(String nationalId, String password) {

        if (!nationalRepository.existsByNationalIdNumber(nationalId)) {
            throw new RuntimeException("National ID does not exist");
        }

        if (userRepository.existsByNationalIdNumber(nationalId)) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setNationalIdNumber(nationalId);
        user.setPassword(password);

        userRepository.save(user);
    }
}