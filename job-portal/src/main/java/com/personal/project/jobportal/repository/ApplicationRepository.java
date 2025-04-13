package com.personal.project.jobportal.repository;

import com.personal.project.jobportal.model.Application;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationRepository extends MongoRepository<Application, String> {
    List<Application> findByJobId(String jobId);
}
