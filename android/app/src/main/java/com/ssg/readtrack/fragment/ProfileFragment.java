package com.ssg.readtrack.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ssg.readtrack.R;
import com.ssg.readtrack.adapter.BookAdapter;
import com.ssg.readtrack.model.Book;
import com.ssg.readtrack.model.Reading;
import com.ssg.readtrack.model.User;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;
import com.ssg.readtrack.ui.DetailActivity;
import com.ssg.readtrack.ui.LibraryActivity;
import com.ssg.readtrack.ui.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    TextView txtUsername;
    TextView txtReadingCount;
    TextView txtCompletedCount;
    Button btnLogout;
    RecyclerView rvReading, rvWishlist, rvCompleted;
    TextView txtWishlistCount;
    TextView txtSeeAllReading;
    TextView txtSeeAllWishlist;
    TextView txtSeeAllCompleted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtUsername = view.findViewById(R.id.txtUsername);
        txtReadingCount = view.findViewById(R.id.txtReadingCount);
        txtCompletedCount = view.findViewById(R.id.txtCompletedCount);
        btnLogout = view.findViewById(R.id.btnLogout);
        rvReading = view.findViewById(R.id.rvReading);
        rvWishlist = view.findViewById(R.id.rvWishlist);
        rvCompleted = view.findViewById(R.id.rvCompleted);
        txtSeeAllReading = view.findViewById(R.id.txtSeeAllReading);
        txtSeeAllWishlist = view.findViewById(R.id.txtSeeAllWishlist);
        txtSeeAllCompleted = view.findViewById(R.id.txtSeeAllCompleted);

        txtWishlistCount = view.findViewById(R.id.txtWishlistCount);

        txtSeeAllReading.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), LibraryActivity.class);

            intent.putExtra("type", "reading");

            startActivity(intent);
        });

        txtSeeAllWishlist.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), LibraryActivity.class);

            intent.putExtra("type", "wishlist");

            startActivity(intent);
        });

        txtSeeAllCompleted.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), LibraryActivity.class);

            intent.putExtra("type", "completed");

            startActivity(intent);
        });

        rvReading.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );

        rvWishlist.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );

        rvCompleted.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );



        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("app", getContext().MODE_PRIVATE);

        String token = prefs.getString("token", "");

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        loadUser(api, token);

        loadReadings(api, token);

        btnLogout.setOnClickListener(v -> {

            prefs.edit().remove("token").apply();

            Intent intent = new Intent(getContext(), LoginActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });

        return view;
    }

    private void loadUser(ApiService api, String token) {

        api.getMe(token).enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful() && response.body() != null) {

                    txtUsername.setText(response.body().username);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void loadReadings(ApiService api, String token) {

        api.getReadings(token).enqueue(new Callback<List<Reading>>() {

            @Override
            public void onResponse(
                    Call<List<Reading>> call,
                    Response<List<Reading>> response
            ) {

                if (response.isSuccessful() && response.body() != null) {

                    int reading = 0;
                    int completed = 0;
                    int wishlist = 0;

                    List<Book> readingBooks = new ArrayList<>();
                    List<Book> wishlistBooks = new ArrayList<>();
                    List<Book> completedBooks = new ArrayList<>();

                    for (Reading r : response.body()) {

                        if (r.status.equals("reading")) {
                            reading++;
                            readingBooks.add(r.book);
                        }

                        if (r.status.equals("completed")) {
                            completed++;
                            completedBooks.add(r.book);
                        }

                        if (r.status.equals("wishlist")){
                            wishlist++;
                            wishlistBooks.add(r.book);
                        }
                    }

                    if (readingBooks.isEmpty()) {
                        rvReading.setVisibility(View.GONE);
                    }

                    if (completedBooks.isEmpty()) {
                        rvWishlist.setVisibility(View.GONE);
                    }

                    if (wishlistBooks.isEmpty()) {
                        rvWishlist.setVisibility(View.GONE);
                    }

                    txtReadingCount.setText("Reading: " + reading);

                    txtCompletedCount.setText("Completed: " + completed);

                    txtWishlistCount.setText("Wishlist: " + wishlist);

                    // adapters
                    rvReading.setAdapter(
                            new BookAdapter(readingBooks,book -> {

                                Intent intent = new Intent(getContext(), DetailActivity.class);

                                intent.putExtra("title", book.title);
                                intent.putExtra("author", book.author.name);
                                intent.putExtra("total_pages", String.valueOf(book.total_pages));
                                intent.putExtra("cover", book.cover);
                                intent.putExtra("book_id", book.id);

                                startActivity(intent);

                            })
                    );

                    rvWishlist.setAdapter(
                            new BookAdapter(wishlistBooks,book -> {

                                Intent intent = new Intent(getContext(), DetailActivity.class);

                                intent.putExtra("title", book.title);
                                intent.putExtra("author", book.author.name);
                                intent.putExtra("total_pages", String.valueOf(book.total_pages));
                                intent.putExtra("cover", book.cover);
                                intent.putExtra("book_id", book.id);

                                startActivity(intent);

                            })
                    );

                    rvCompleted.setAdapter(
                            new BookAdapter(completedBooks,book -> {

                                Intent intent = new Intent(getContext(), DetailActivity.class);

                                intent.putExtra("title", book.title);
                                intent.putExtra("author", book.author.name);
                                intent.putExtra("total_pages", String.valueOf(book.total_pages));
                                intent.putExtra("cover", book.cover);
                                intent.putExtra("book_id", book.id);

                                startActivity(intent);

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