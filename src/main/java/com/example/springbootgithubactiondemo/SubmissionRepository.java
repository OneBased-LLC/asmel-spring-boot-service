package com.example.springbootgithubactiondemo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionRepository extends MongoRepository<Submission, String> {
    // Custom queries can be defined here if necessary
}