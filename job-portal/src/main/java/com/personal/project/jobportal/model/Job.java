package com.personal.project.jobportal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "jobs")
public class Job {
    @Id
    private String id;
    private String title;
    private String description;
    private String location;
    private String company;
    private List<String> skills;
    private String postedBy;
    private LocalDate postedAt;
    private Salary salary;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int applicationCount = 0;
}