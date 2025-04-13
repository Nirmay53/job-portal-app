package com.personal.project.auth.entity;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfo {
    private String userName;
    private Set<String> roles;
    
    public UserInfo() {
    }

    public UserInfo(String userName, Set<String> roles) {
        this.userName = userName;
        this.roles = roles;
    }
}