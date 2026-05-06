package com.ssg.readtrack.network;
import com.ssg.readtrack.model.Book;
import com.ssg.readtrack.model.LoginRequest;
import com.ssg.readtrack.model.LoginResponse;
import com.ssg.readtrack.model.Reading;
import com.ssg.readtrack.model.ReadingRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/books/")
    Call<List<Book>> getBooks();

    @POST("api/readings/")
    Call<Void> createReading(
            @Header("Authorization") String token,
            @Body ReadingRequest request
    );

    @GET("api/readings/")
    Call<List<Reading>> getReadings(@Header("Authorization") String token);

    @POST("api/login/")
    Call<LoginResponse> login(@Body LoginRequest request);
}
