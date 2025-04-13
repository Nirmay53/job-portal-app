package com.personal.project.jobportal.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "applications")
public class Application {
    @Id
    private String id;
    private String jobId;
    private String userId;
    private String resume;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private String status = "Pending";
}