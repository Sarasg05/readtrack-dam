package com.ssg.readtrack.ui;

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

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getReadings(1).enqueue(new Callback<List<Reading>>() {
            @Override
            public void onResponse(Call<List<Reading>> call, Response<List<Reading>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recycler.setAdapter(new ReadingAdapter(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<Reading>> call, Throwable t) {
                Log.e("API", t.getMessage());
            }
        });
    }
}