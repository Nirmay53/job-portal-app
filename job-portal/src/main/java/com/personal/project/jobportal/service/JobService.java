package com.personal.project.jobportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.UpdateResult;
import com.personal.project.jobportal.exception.DocumentNotFoundException;
import com.personal.project.jobportal.model.Job;
import com.personal.project.jobportal.repository.JobRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private JobRepository jobRepository;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Page<Job> searchJobs(String title, String location, List<String> skills, 
                                Double minSalary, Double maxSalary, String currency , Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            operations.add(Aggregation.match(Criteria.where("title").regex(title, "i")));
        }

        if (location != null && !location.isEmpty()) {
            operations.add(Aggregation.match(Criteria.where("location").is(location)));
        }

        if (skills != null && !skills.isEmpty()) {
            operations.add(Aggregation.match(Criteria.where("skills").in(skills)));
        }

        if (minSalary != null || maxSalary != null) {
            Criteria salaryCriteria = new Criteria();
            if (minSalary != null) salaryCriteria.and("salary.min").gte(minSalary);
            if (maxSalary != null) salaryCriteria.and("salary.max").lte(maxSalary);
            operations.add(Aggregation.match(salaryCriteria));
        }

        if (currency != null && !currency.isEmpty()) {
            operations.add(Aggregation.match(Criteria.where("salary.currency").is(currency)));
        }

        if(operations.isEmpty()) {
            operations.add(Aggregation.match(new Criteria()));
        }

        Aggregation countAggregation = Aggregation.newAggregation(operations);
        long total = mongoTemplate.aggregate(countAggregation, "jobs", Job.class).getMappedResults().size();

        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));
        
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Job> jobs = mongoTemplate.aggregate(aggregation, "jobs", Job.class).getMappedResults();

        return new PageImpl<>(jobs, pageable, total);
    }

    @Deprecated
    public List<Job> getJobsWithApplicationCount() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.lookup("applications", "_id", "jobId", "applications"),
            Aggregation.project("title", "location", "company")
                .and("applications").size().as("applicationCount")
        );

        AggregationResults<Job> results = mongoTemplate.aggregate(aggregation, "jobs", Job.class);
        return results.getMappedResults();
    }

    public List<Job> getJobsByPostedDateAndSkills(int numberOfDays, List<String> skills) {
        LocalDate minDate = LocalDate.now().minusDays(numberOfDays);
        return jobRepository.findJobsByPostedDateAndSkills(minDate, skills);
    }

    public Optional<Job> getJobById(String id) {
        return jobRepository.findById(id);
    }

    public void createJob(Job jobRequest) {
        jobRepository.save(jobRequest);
    }

    public void updateJob(String jobId, Job updateRequest) throws DocumentNotFoundException {
        Query query = new Query(Criteria.where("_id").is(jobId));
    
        Update update = new Update();
        if (updateRequest.getTitle() != null) update.set("title", updateRequest.getTitle());
        if (updateRequest.getDescription() != null) update.set("description", updateRequest.getDescription());
        if (updateRequest.getCompany() != null) update.set("company", updateRequest.getCompany());
        if (updateRequest.getLocation() != null) update.set("location", updateRequest.getLocation());
        if (updateRequest.getSkills() != null) update.set("skills", updateRequest.getSkills());
        if (updateRequest.getSalary() != null) update.set("salary", updateRequest.getSalary());
        update.set("updatedAt", LocalDate.now());
        
        UpdateResult result = mongoTemplate.updateFirst(query, update, Job.class);
    
        if (result.getMatchedCount() == 0) {
            throw new DocumentNotFoundException("Job with ID " + jobId + " not found");
        }
    }

}
