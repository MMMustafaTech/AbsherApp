package com.absher.absherapp.service;

import com.absher.absherapp.entity.User;
import com.absher.absherapp.repository.UserRepository;
import com.absher.absherapp.repository.NationalIdentityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final NationalIdentityRepository nationalRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       NationalIdentityRepository nationalRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.nationalRepository = nationalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String nationalId, String password, String email) {

        if (!nationalRepository.existsByNationalIdNumber(nationalId)) {
            throw new RuntimeException("National ID does not exist");
        }

        if (userRepository.existsByNationalIdNumber(nationalId)) {
            throw new RuntimeException("User already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already used");
        }

        User user = new User();
        user.setNationalIdNumber(nationalId);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        userRepository.save(user);
    }


    public User login(String nationalId, String password) {
        User user = userRepository.findByNationalIdNumber(nationalId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }
}