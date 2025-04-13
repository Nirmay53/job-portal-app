package com.personal.project.auth.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.project.auth.entity.JWTPayload;
import com.personal.project.auth.entity.UserInfo;
import com.personal.project.auth.service.AuthService;
import com.personal.project.auth.service.JWTService;
import com.personal.project.common.entity.User;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    JWTService jwtService;

    @GetMapping("/login")
    public String issueToken(@RequestParam String userName, @RequestParam String password) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        if(!authService.validateUser(userName, password)) {
            return "Invalid credentials :/";
        }
        User userDetails = authService.getUserDetails(userName);
        System.out.println("User details: " + userDetails.toString());
        return jwtService.generateToken(userDetails);
    }

    @PostMapping("/validateToken")
    public boolean validateToken(@RequestBody String token) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        return jwtService.validateToken(token);
    }

    @PostMapping("/userinfo")
    public UserInfo getUserJwtPayload(@RequestBody String token) {
        JWTPayload payload = jwtService.extractJwtPayload(token);
        UserInfo userInfo = new UserInfo(payload.getUserName(), payload.getRoles());
        return userInfo;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@RequestBody User userDetails) {
        return ResponseEntity.ok("");
    }
}
