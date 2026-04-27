package com.ssg.readtrack.network;
import com.ssg.readtrack.model.Book;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("books/")
    Call<List<Book>> getBooks();
}
