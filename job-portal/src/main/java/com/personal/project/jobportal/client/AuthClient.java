package com.personal.project.jobportal.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.personal.project.jobportal.model.UserInfo;

@Component
public class AuthClient {

    private final RestTemplate restTemplate;
    @Value("${auth.service.base-url}")
    private String authBaseUrl;

    public AuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isValidToken(String token) {
        String validateTokenUrl = authBaseUrl + "/auth/validateToken";
        System.out.println(validateTokenUrl);
        Boolean result = restTemplate.postForObject(validateTokenUrl, token, Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    public UserInfo getUserInfo(String token) {
        String userInfoUrl = authBaseUrl + "/auth/userinfo";
        UserInfo userInfo = restTemplate.postForObject(userInfoUrl, token, UserInfo.class);
        return userInfo;
    }
}

