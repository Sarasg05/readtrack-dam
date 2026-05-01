package com.ssg.readtrack.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.ssg.readtrack.R;
import com.ssg.readtrack.model.ReadingRequest;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    TextView title, author, total_pages, genres;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        title = findViewById(R.id.txtTitleDetail);
        author = findViewById(R.id.txtAuthorDetail);
        total_pages = findViewById(R.id.txtTotalPagesDetail);
        genres = findViewById(R.id.txtGenresDetail);
        image = findViewById(R.id.imgBookDetail);

        // Recibir datos
        String t = getIntent().getStringExtra("title");
        String a = getIntent().getStringExtra("author");
        String p = getIntent().getStringExtra("total_pages");
        String g = getIntent().getStringExtra("genres");

        int bookId = getIntent().getIntExtra("book_id", -1);

        String cover = getIntent().getStringExtra("cover");
        if (cover != null){
            Glide.with(this).load(cover).into(image);
        }

        // Mostrar datos
        title.setText(t != null ? t : "");
        author.setText(a != null ? a : "");
        total_pages.setText(p != null ? p : "");
        genres.setText(g != null ? g : "");

        Button btnStart = findViewById(R.id.btnStartReading);

        btnStart.setOnClickListener(v -> {
            if (bookId == -1) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService api = RetrofitClient.getClient().create(ApiService.class);

            ReadingRequest req = new ReadingRequest(1, bookId, "reading");

            api.createReading(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(DetailActivity.this, "Started reading", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        Button btnCompleted = findViewById(R.id.btnCompleted);

        btnCompleted.setOnClickListener(v -> {
            if (bookId == -1) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService api = RetrofitClient.getClient().create(ApiService.class);

            ReadingRequest req = new ReadingRequest(1, bookId, "completed");

            api.createReading(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(DetailActivity.this, "Marked as completed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }
}