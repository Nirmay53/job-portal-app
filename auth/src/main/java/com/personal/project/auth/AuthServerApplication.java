package com.personal.project.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.personal.project.common", "com.personal.project.auth"})
@EnableJpaRepositories(basePackages = "com.personal.project.common.repository")
@EntityScan(basePackages = "com.personal.project.common.entity")
public class AuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
