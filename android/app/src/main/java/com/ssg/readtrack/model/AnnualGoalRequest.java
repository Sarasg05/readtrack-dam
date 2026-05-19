package com.ssg.readtrack.model;

public class AnnualGoalRequest {

    public int year;
    public int target_books;

    public AnnualGoalRequest(int year, int target_books) {
        this.year = year;
        this.target_books = target_books;
    }
}
