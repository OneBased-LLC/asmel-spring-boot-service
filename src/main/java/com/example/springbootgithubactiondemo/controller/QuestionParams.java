package com.example.springbootgithubactiondemo.controller;

public class QuestionParams {
    private String[] questions;

    public String name;

    public String bookName;

    public String userId;

    public QuestionParams(String name, String[] questions, String bookName, String userId) {
        this.name = name;
        this.questions = questions;
        this.bookName = bookName;
        this.userId = userId;
    }

    public String[] getQuestions() {
        return questions;
    }
    public void setQuestions(String[] questions) {
        this.questions = questions;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
