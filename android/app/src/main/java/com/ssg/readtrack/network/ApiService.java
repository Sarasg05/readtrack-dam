package com.ssg.readtrack.network;
import com.ssg.readtrack.model.Book;
import com.ssg.readtrack.model.ReadingRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("api/books/")
    Call<List<Book>> getBooks();

    @POST("api/readings/")
    Call<Void> createReading(@Body ReadingRequest request);
}
