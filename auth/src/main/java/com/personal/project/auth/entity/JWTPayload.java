package com.personal.project.auth.entity;

import java.time.Instant;
import java.util.Set;

import com.personal.project.common.entity.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
@Getter
@Setter
@ToString
public class JWTPayload {
    private String sub;
    private String email;
    private String userName;
    private long iat;
    private long exp;
    private Set<String> roles;

    public JWTPayload() {
    }
    
    public JWTPayload(User userDetails, int expirationSecs) {
        long issuedAt = Instant.now().getEpochSecond();
        this.sub = String.valueOf(userDetails.getId());
        this.email = userDetails.getEmail();
        this.userName = userDetails.getName();
        this.iat = issuedAt;
        this.exp = issuedAt + expirationSecs;
        this.roles = userDetails.getRoles();
    }
}