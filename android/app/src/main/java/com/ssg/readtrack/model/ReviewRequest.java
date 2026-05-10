package com.ssg.readtrack.model;

public class ReviewRequest {

    public int book;
    public int rating;
    public String comment;

    public ReviewRequest(int book, int rating) {
        this.book = book;
        this.rating = rating;
        this.comment = "";
    }

    public ReviewRequest(int book, int rating, String comment) {
        this.book = book;
        this.rating = rating;
        this.comment = comment;
    }
}
