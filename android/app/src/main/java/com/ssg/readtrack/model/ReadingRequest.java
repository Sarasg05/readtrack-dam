package com.ssg.readtrack.model;

public class ReadingRequest {
    public int book;
    public String status;

    public ReadingRequest(int book, String status) {
        this.book = book;
        this.status = status;
    }
}
