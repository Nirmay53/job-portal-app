package com.personal.project.jobportal.controller;

import com.personal.project.jobportal.model.Application;
import com.personal.project.jobportal.service.ApplicationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyForJob(@RequestBody Application applicationRequest) {
        applicationRequest.setAppliedAt(LocalDateTime.now());
        applicationService.applyForJob(applicationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Application submitted successfully!");
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<String> updateApplicationStatus(@PathVariable String applicationId, @RequestParam String status) {
        applicationService.updateApplicationStatus(applicationId, status);
        return ResponseEntity.ok("Application status updated successfully!");
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJobId(@PathVariable String jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJobId(jobId));
    }

    @GetMapping("/summary-by-status")
    public ResponseEntity<List<Map<String, Object>>> getApplicationSummaryByStatus() {
        return ResponseEntity.ok(applicationService.getApplicationSummaryByStatus());
    }
}
