package com.absher.absherapp.repository;

import com.absher.absherapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNationalIdNumber(String nationalIdNumber);

}