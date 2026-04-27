package com.ssg.readtrack.model;

public class ReadingRequest {
    public int user;
    public int book;
    public String status;

    public ReadingRequest(int user, int book, String status) {
        this.user = user;
        this.book = book;
        this.status = status;
    }
}
