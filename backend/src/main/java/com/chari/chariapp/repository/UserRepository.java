package com.chari.chariapp.repository;

import com.chari.chariapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNationalIdNumber(String nationalIdNumber);
    boolean existsByEmail(String email);
    Optional<User> findByNationalIdNumber(String nationalIdNumber);
}