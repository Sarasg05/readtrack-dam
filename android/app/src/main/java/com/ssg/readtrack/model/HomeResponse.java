package com.ssg.readtrack.model;

public class HomeResponse {
    public CurrentBook current_book;
    public int books_read;
    public int goal;

    public static class CurrentBook {
        public String title;
        public int total_pages;
    }
}
