package com.personal.project.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.project.common.entity.User;

public interface UserRepository extends JpaRepository<User, Long>  {
    Optional<User> findByName(String userName);
}
