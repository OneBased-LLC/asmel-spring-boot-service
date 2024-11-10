package com.example.springbootgithubactiondemo;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;


@Document(collection = "submissions")
public class Submission {

    @Id  // Primary key field
    private UUID id;

    private LocalDateTime date;

    private String userId;

    private String documentUrl;

    private String bookName;

    public Submission(UUID id, LocalDateTime date, String userId, String documentUrl, String bookName) {
        this.id = id;
        this.date = date;
        this.userId = userId;
        this.documentUrl = documentUrl;
        this.bookName = bookName;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDocumentUrl() {
        return documentUrl;
    }
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

}


