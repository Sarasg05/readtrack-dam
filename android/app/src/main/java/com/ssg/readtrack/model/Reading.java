package com.ssg.readtrack.model;

public class Reading {
    public int id;
    public String book;
    public String status;
    public String start_date;
    public String end_date;

    public static class BookItem {
        public int id;
        public String title;
    }
}
