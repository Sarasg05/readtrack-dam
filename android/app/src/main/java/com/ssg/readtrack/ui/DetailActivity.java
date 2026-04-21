package com.ssg.readtrack.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.ssg.readtrack.R;

public class DetailActivity extends AppCompatActivity {

    TextView title, author, total_pages, genres;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title = findViewById(R.id.txtTitle);
        author = findViewById(R.id.txtAuthor);
        total_pages = findViewById(R.id.txtTotalPages);
        genres = findViewById(R.id.txtGenres);
        image = findViewById(R.id.imgBook);

        // Recibir datos
        String t = getIntent().getStringExtra("title");
        String a = getIntent().getStringExtra("author");
        String p = getIntent().getStringExtra("total_pages");
        String g = getIntent().getStringExtra("genres");

        String cover = getIntent().getStringExtra("cover");
        if (cover != null){
            Glide.with(this).load(cover).into(image);
        }

        // Mostrar datos
        title.setText(t != null ? t : "");
        author.setText(a != null ? a : "");
        total_pages.setText(p != null ? p : "");
        genres.setText(g != null ? g : "");


    }
}