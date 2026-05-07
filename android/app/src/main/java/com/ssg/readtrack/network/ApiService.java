package com.ssg.readtrack.network;
import com.ssg.readtrack.model.Book;
import com.ssg.readtrack.model.HomeResponse;
import com.ssg.readtrack.model.LoginRequest;
import com.ssg.readtrack.model.LoginResponse;
import com.ssg.readtrack.model.Reading;
import com.ssg.readtrack.model.ReadingRequest;
import com.ssg.readtrack.model.StatsResponse;
import com.ssg.readtrack.model.User;

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

    @GET("api/stats/")
    Call<StatsResponse> getStats(@Header("Authorization") String token);

    @GET("api/me/")
    Call<User> getMe(@Header("Authorization") String token);

    @GET("api/home/")
    Call<HomeResponse> getHome(@Header("Authorization") String token);
}
