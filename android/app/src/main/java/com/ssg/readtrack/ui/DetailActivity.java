package com.ssg.readtrack.ui;

import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
    import android.widget.RatingBar;
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
    import com.ssg.readtrack.model.ReviewRequest;
import com.ssg.readtrack.model.ReviewResponse;
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

        int bookId = getIntent().getIntExtra("book_id", -1);

        title = findViewById(R.id.txtTitleDetail);
        author = findViewById(R.id.txtAuthorDetail);
        total_pages = findViewById(R.id.txtTotalPagesDetail);
        genres = findViewById(R.id.txtGenresDetail);
        image = findViewById(R.id.imgBookDetail);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText etReview = findViewById(R.id.etReview);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);

        String token = prefs.getString("token", "");

        api.getMyReview(token, bookId)
                .enqueue(new Callback<ReviewResponse>() {

                    @Override
                    public void onResponse(
                            Call<ReviewResponse> call,
                            Response<ReviewResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ReviewResponse review = response.body();

                            ratingBar.setRating(review.rating);

                            etReview.setText(review.comment);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ReviewResponse> call,
                            Throwable t
                    ) {

                    }
                });


        // Recibir datos
        String t = getIntent().getStringExtra("title");
        String a = getIntent().getStringExtra("author");
        String p = getIntent().getStringExtra("total_pages");
        String g = getIntent().getStringExtra("genres");

        String synopsis = getIntent().getStringExtra("synopsis");
        TextView txtSynopsis = findViewById(R.id.txtSynopsis);

        txtSynopsis.setText(synopsis != null ? synopsis : "No synopsis available");


        String cover = getIntent().getStringExtra("cover");
        if (cover != null) {
            Glide.with(this).load(cover).into(image);
        }

        // Mostrar datos
        title.setText(t != null ? t : "");
        author.setText(a != null ? a : "");
        total_pages.setText(p != null ? p : "");
        genres.setText(g != null ? g : "");

        ImageButton btnStart = findViewById(R.id.btnReading);
        btnStart.setOnClickListener(v -> {
            if (bookId == -1) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                return;
            }

            if (token.isEmpty()) {
                Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
                return;
            }

            ReadingRequest req = new ReadingRequest(bookId, "reading");

            api.createReading(token, req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(DetailActivity.this, "Started reading", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        ImageButton btnCompleted = findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(v -> {
            if (bookId == -1) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                return;
            }

            if (token.isEmpty()) {
                Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
                return;
            }

            ReadingRequest req = new ReadingRequest(bookId, "completed");

            api.createReading(token, req).enqueue(new Callback<Void>() {
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

        ImageButton btnWishlist = findViewById(R.id.btnWishlist);
        btnWishlist.setOnClickListener(v -> {


            if (token.isEmpty()) {
                Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
                return;
            }

            ReadingRequest req = new ReadingRequest(bookId, "wishlist");

            api.createReading(token, req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(DetailActivity.this, "Added to wishlist ⭐", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {

            if (!fromUser) {
                return;
            }

            String comment = etReview.getText().toString();

            ReviewRequest req = new ReviewRequest(bookId, (int) rating, comment);

            api.createReview(token, req).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(
                                DetailActivity.this,
                                "Rating saved ⭐",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    Toast.makeText(
                            DetailActivity.this,
                            "Error saving review",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        Button btnSendReview = findViewById(R.id.btnSendReview);

        btnSendReview.setOnClickListener(v -> {

            String comment = etReview.getText().toString();

            ReviewRequest req =
                    new ReviewRequest(bookId, (int) ratingBar.getRating(), comment);

            api.createReview(token, req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(
                                DetailActivity.this,
                                "Review posted",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    Toast.makeText(
                            DetailActivity.this,
                            "Error",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

        });

    }
}