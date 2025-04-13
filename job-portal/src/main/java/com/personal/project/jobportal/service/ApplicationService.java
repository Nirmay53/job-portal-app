package com.personal.project.jobportal.service;

import com.personal.project.jobportal.exception.DocumentNotFoundException;
import com.personal.project.jobportal.model.Application;
import com.personal.project.jobportal.model.Job;
import com.personal.project.jobportal.repository.ApplicationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ApplicationRepository applicationRepository;

    public void applyForJob(Application applicationRequest) {

        Query jobQuery = new Query(Criteria.where("_id").is(applicationRequest.getJobId()));
        boolean jobExists = mongoTemplate.exists(jobQuery, Job.class);

        if (!jobExists) {
            throw new DocumentNotFoundException("Job not found for ID: " + applicationRequest.getJobId());
        }

        Application application = new Application();
        application.setJobId(applicationRequest.getJobId());
        application.setUserId(applicationRequest.getUserId());
        application.setResume(applicationRequest.getResume());
        application.setAppliedAt(LocalDateTime.now());

        applicationRepository.save(application);

        Query query = new Query(Criteria.where("_id").is(applicationRequest.getJobId()));
        Update update = new Update().inc("applicationCount", 1);
        mongoTemplate.updateFirst(query, update, Job.class);
    }

    public void updateApplicationStatus(String applicationId, String status) {
        Query query = new Query(Criteria.where("_id").is(applicationId));

        if(!mongoTemplate.exists(query, Application.class)) {
            throw new DocumentNotFoundException("Application not found for ID: " + applicationId);
        }

        Update update = new Update().set("status", status);
        update.set("updatedAt", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, Application.class);
    }

    public List<Application> getApplicationsByJobId(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getApplicationSummaryByStatus() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("status").count().as("applicationCount"),
            Aggregation.project("applicationCount").and("_id").as("status")
        );

        AggregationResults<?> results = mongoTemplate.aggregate(aggregation, "applications", Map.class);

        List<Map<String, Object>> mappedResults = results.getMappedResults()
            .stream()
            .map(obj -> (Map<String, Object>) obj)
            .toList();
 
        return mappedResults;
    }
}
