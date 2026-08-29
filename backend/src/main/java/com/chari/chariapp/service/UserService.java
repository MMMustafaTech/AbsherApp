package com.chari.chariapp.service;

import com.chari.chariapp.dto.UserResponse;
import com.chari.chariapp.entity.Passport;
import com.chari.chariapp.entity.User;
import com.chari.chariapp.exception.BadRequestException;
import com.chari.chariapp.exception.NotFoundException;
import com.chari.chariapp.repository.PassportRepository;
import com.chari.chariapp.repository.UserRepository;
import com.chari.chariapp.repository.NationalIdRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final NationalIdRepository nationalRepository;
    private final PasswordEncoder passwordEncoder;
    private final PassportRepository passportRepository;

    public UserService(UserRepository userRepository,
                       NationalIdRepository nationalRepository,
                       PasswordEncoder passwordEncoder, PassportRepository passportRepository) {

        this.userRepository = userRepository;
        this.nationalRepository = nationalRepository;
        this.passwordEncoder = passwordEncoder;
        this.passportRepository = passportRepository;
    }

    public UserResponse register(String nationalId, String password, String email) {

        if (!nationalRepository.existsByNationalIdNumber(nationalId)) {
            throw new NotFoundException("National ID does not exist");
        }

        if (userRepository.existsByNationalIdNumber(nationalId)) {
            throw new BadRequestException("User already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already used");
        }

        User user = new User();
        user.setNationalIdNumber(nationalId);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        userRepository.save(user);

        Passport passport = passportRepository
                .findByNationalIdNumber(nationalId)
                .orElseThrow(() -> new NotFoundException("Passport not found"));

        return new UserResponse(
                user.getId(),
                user.getNationalIdNumber(),
                user.getEmail(),
                passport.getName()
        );
    }


    public UserResponse login(String nationalId, String password) {
        User user = userRepository.findByNationalIdNumber(nationalId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }
        Passport passport = passportRepository
                .findByNationalIdNumber(nationalId)
                .orElseThrow(() -> new NotFoundException("Passport not found"));

        return new UserResponse(
                user.getId(),
                user.getNationalIdNumber(),
                user.getEmail(),
                passport.getName()
        );
    }
}