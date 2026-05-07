package com.ssg.readtrack.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssg.readtrack.R;
import com.ssg.readtrack.adapter.BookAdapter;
import com.ssg.readtrack.model.Book;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;
import com.ssg.readtrack.ui.DetailActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadBooks();

        return view;
    }

    private void loadBooks() {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<List<Book>> call = apiService.getBooks();

        call.enqueue(new Callback<List<Book>>() {

            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Book> books = response.body();

                    BookAdapter adapter = new BookAdapter(books, book -> {

                        Intent intent = new Intent(getContext(), DetailActivity.class);

                        intent.putExtra("title", book.title);
                        intent.putExtra("author", book.author);
                        intent.putExtra("total_pages", String.valueOf(book.total_pages));
                        intent.putExtra("cover", book.cover);
                        intent.putExtra("book_id", book.id);

                        if (book.genres != null && !book.genres.isEmpty()) {
                            intent.putExtra("genres", book.genres.get(0));
                        }

                        startActivity(intent);
                    });

                    recyclerView.setAdapter(adapter);

                } else {
                    Log.e("API", "Invalid response");
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e("API", t.getMessage());
            }
        });
    }
}