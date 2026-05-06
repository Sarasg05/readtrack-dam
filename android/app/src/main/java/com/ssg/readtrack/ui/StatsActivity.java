package com.ssg.readtrack.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssg.readtrack.R;
import com.ssg.readtrack.model.StatsResponse;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsActivity extends AppCompatActivity {

    TextView txtBooks, txtPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        txtBooks = findViewById(R.id.txtBooks);
        txtPages = findViewById(R.id.txtPages);

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String token = prefs.getString("token", "");

        if (token.isEmpty()) {
            finish();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getStats(token).enqueue(new Callback<StatsResponse>() {
            @Override
            public void onResponse(Call<StatsResponse> call, Response<StatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatsResponse stats = response.body();

                    txtBooks.setText("Books read: " + stats.books_read);
                    txtPages.setText("Pages read: " + stats.pages_read);
                }
            }

            @Override
            public void onFailure(Call<StatsResponse> call, Throwable t) {
            }
        });
    }
}