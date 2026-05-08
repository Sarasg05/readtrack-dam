package com.ssg.readtrack.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ssg.readtrack.R;
import com.ssg.readtrack.adapter.BookAdapter;
import com.ssg.readtrack.model.Book;
import com.ssg.readtrack.model.Reading;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryActivity extends AppCompatActivity {

    RecyclerView rvBooks;
    TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        rvBooks = findViewById(R.id.rvBooks);
        txtTitle = findViewById(R.id.txtTitle);

        rvBooks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        String type = getIntent().getStringExtra("type");

        txtTitle.setText(type);

        loadBooks(type);
    }

    private void loadBooks(String type) {

        SharedPreferences prefs =
                getSharedPreferences("app", MODE_PRIVATE);

        String token = prefs.getString("token", "");

        ApiService api =
                RetrofitClient.getClient().create(ApiService.class);

        api.getReadings(token).enqueue(new Callback<List<Reading>>() {

            @Override
            public void onResponse(
                    Call<List<Reading>> call,
                    Response<List<Reading>> response
            ) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Book> books = new ArrayList<>();

                    for (Reading r : response.body()) {

                        if (r.status.equals(type)) {

                            books.add(r.book);
                        }
                    }

                    rvBooks.setAdapter(
                            new BookAdapter(books, book -> {

                            })
                    );
                }
            }

            @Override
            public void onFailure(Call<List<Reading>> call, Throwable t) {

            }
        });
    }
}