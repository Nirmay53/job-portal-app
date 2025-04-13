package com.personal.project.jobportal.controller;

import com.personal.project.jobportal.exception.DocumentNotFoundException;
import com.personal.project.jobportal.model.Job;
import com.personal.project.jobportal.service.JobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public Page<Job> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String currency,
            Pageable pageable) {
        return jobService.searchJobs(title, location, skills, minSalary, maxSalary, currency, pageable);
    }

    @Deprecated
    @GetMapping("/application-count")
    public ResponseEntity<List<Job>> getJobsWithApplicationCount() {
        return ResponseEntity.ok(jobService.getJobsWithApplicationCount());
    }

    @GetMapping("/latestJobsBasedOnSkills")
    public ResponseEntity<List<Job>> getJobsByPostedDateAndSkills(
        @RequestParam(required = true) Integer daysFromPost,
        @RequestParam(required = true) List<String> skill
    ) {
        return ResponseEntity.ok(jobService.getJobsByPostedDateAndSkills(daysFromPost, skill));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Optional<Job> getJobById(
        @PathVariable(required = true) String id
    ) {
        return jobService.getJobById(id);
    }

    @PostMapping("/post")
    public ResponseEntity<String> postJob(@RequestBody Job jobRequest) {
        try {
            jobService.createJob(jobRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Job created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the job: " + e.getMessage());
        }
    }

    @PutMapping("/update/{jobId}")
    public ResponseEntity<String> updateJob(@PathVariable String jobId, @RequestBody Job jobRequest) {
        try {
            jobService.updateJob(jobId, jobRequest);
            return ResponseEntity.ok("Job updated successfully!");
        } catch (DocumentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the job: " + e.getMessage());
        }
    }
}
