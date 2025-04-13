package com.personal.project.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.personal.project.common.entity.User;
import com.personal.project.common.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean validateUser(String userName, String password) {
        Optional<User> user = userRepository.findByName(userName);
        if(user.isPresent()) {
            return encoder.matches(password, user.get().getPassword());
        } else {
            return false;
        }
    }

    public User getUserDetails(String userName) {
        return userRepository.findByName(userName).get();
    }
}
