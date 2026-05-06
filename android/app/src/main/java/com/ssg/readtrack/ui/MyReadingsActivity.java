package com.ssg.readtrack.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ssg.readtrack.R;
import com.ssg.readtrack.adapter.ReadingAdapter;
import com.ssg.readtrack.model.Reading;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReadingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_readings);

        RecyclerView recycler = findViewById(R.id.recyclerReadings);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String token = prefs.getString("token", "");

        if (token.isEmpty()) {
            Log.e("AUTH", "No token found");

            startActivity(new Intent(this, LoginActivity.class));
            finish();

            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getReadings(token).enqueue(new Callback<List<Reading>>() {
            @Override
            public void onResponse(Call<List<Reading>> call, Response<List<Reading>> response) {
                if (response.code() == 401) {
                    Log.e("AUTH", "Unauthorized - token inválido");

                    startActivity(new Intent(MyReadingsActivity.this, LoginActivity.class));
                    finish();

                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    recycler.setAdapter(new ReadingAdapter(response.body()));
                } else {
                    Log.e("API", "Respuesta no válida");
                }
            }

            @Override
            public void onFailure(Call<List<Reading>> call, Throwable t) {
                Log.e("API", t.getMessage());
            }
        });
    }
}