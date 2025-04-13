package com.personal.project.jobportal.repository;

import com.personal.project.jobportal.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends MongoRepository<Job, String> {
    Optional<Job> findById(String id);

    @Query(value = "{ 'createdAt': { $gte: ?0 }, 'skills': { $in: ?1 } }", sort = "{ 'createdAt': -1 }")
    List<Job> findJobsByPostedDateAndSkills(LocalDate minDate, List<String> skills);
}
