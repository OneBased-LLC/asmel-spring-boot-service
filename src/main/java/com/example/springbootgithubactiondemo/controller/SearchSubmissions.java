package com.example.springbootgithubactiondemo.controller;

public class SearchSubmissions {
    private String userId;
    public SearchSubmissions( String userId) { this.userId = userId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
