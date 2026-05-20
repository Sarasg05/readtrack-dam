package com.ssg.readtrack.model;

import java.util.List;

public class Book {
    public int id;
    public String title;
    public Author author;
    public int total_pages;
    public String synopsis;
    public List<String> genres;
    public String cover;

    public float average_rating;
    public int reviews_count;
}
