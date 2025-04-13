package com.personal.project.auth.entity;

import lombok.Data;

@Data
public class JWTHeader {

    public JWTHeader (String alg, String type) {
        this.alg = alg;
        this.type = type;
    }
    
    private String alg;
    private String type;
}