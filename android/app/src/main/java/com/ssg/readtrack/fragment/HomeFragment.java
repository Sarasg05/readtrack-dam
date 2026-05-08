package com.ssg.readtrack.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ssg.readtrack.R;
import com.ssg.readtrack.adapter.BookAdapter;
import com.ssg.readtrack.model.Book;
import com.ssg.readtrack.model.HomeResponse;
import com.ssg.readtrack.model.User;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;
import com.ssg.readtrack.ui.DetailActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    TextView tvCurrentBook, tvBooks, tvPages, tvGoal;
    ProgressBar progressGoal;
    RecyclerView rvRecommended;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvCurrentBook = view.findViewById(R.id.tvCurrentBook);
        tvBooks = view.findViewById(R.id.tvBooks);
        tvPages = view.findViewById(R.id.tvPages);
        tvGoal = view.findViewById(R.id.tvGoal);
        progressGoal = view.findViewById(R.id.progressGoal);
        rvRecommended = view.findViewById(R.id.rvRecommended);

        rvRecommended.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        loadHome();
        loadRecommended();

        return view;
    }

    private String getToken() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("app", getContext().MODE_PRIVATE);

        return "Bearer " + prefs.getString("token", "");
    }

    private void loadHome() {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getHome(getToken()).enqueue(new Callback<HomeResponse>() {

            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                HomeResponse data = response.body();

                // CURRENT BOOK
                if (data.current_book != null) {
                    tvCurrentBook.setText(data.current_book.title);
                } else {
                    tvCurrentBook.setText("No current book");
                }

                // STATS
                tvBooks.setText("Books: " + data.books_read);
                tvGoal.setText("Goal: " + data.goal);

                int progress = (data.goal == 0) ? 0 :
                        (data.books_read * 100 / data.goal);

                progressGoal.setProgress(progress);
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.e("HOME", t.getMessage());
            }
        });
    }

    private void loadRecommended() {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getBooks().enqueue(new Callback<List<Book>>() {

            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<Book> books = response.body();

                BookAdapter adapter = new BookAdapter(books, book -> {

                    Intent intent = new Intent(getContext(), DetailActivity.class);

                    intent.putExtra("title", book.title);
                    intent.putExtra("author", book.author.name);
                    intent.putExtra("total_pages", String.valueOf(book.total_pages));
                    intent.putExtra("cover", book.cover);
                    intent.putExtra("book_id", book.id);

                    if (book.genres != null && !book.genres.isEmpty()) {
                        intent.putExtra("genres", book.genres.get(0));
                    }

                    startActivity(intent);
                });

                rvRecommended.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e("BOOKS", t.getMessage());
            }
        });
    }
}