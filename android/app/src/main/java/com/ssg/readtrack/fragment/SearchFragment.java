package com.ssg.readtrack.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ssg.readtrack.R;
import com.ssg.readtrack.adapter.BookAdapter;
import com.ssg.readtrack.model.Book;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;
import com.ssg.readtrack.ui.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    EditText etSearch;

    TextView txtEmpty;

    List<Book> allBooks = new ArrayList<>();
    List<Book> filteredBooks = new ArrayList<>();

    BookAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerSearch);
        etSearch = view.findViewById(R.id.etSearch);
        txtEmpty = view.findViewById(R.id.txtEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BookAdapter(filteredBooks, book -> {

            Intent intent = new Intent(getContext(), DetailActivity.class);

            intent.putExtra("book_id", book.id);

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadBooks();

        setupSearch();

        return view;
    }

    private void loadBooks() {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    allBooks.clear();
                    allBooks.addAll(response.body());

                    filteredBooks.clear();
                    filteredBooks.addAll(allBooks);

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {

            }
        });
    }

    private void setupSearch() {

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filterBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filterBooks(String query) {

        String q = query.toLowerCase().trim();

        filteredBooks.clear();

        for (Book book : allBooks) {

            String title = book.title != null ? book.title.toLowerCase() : "";
            String author = book.author != null && book.author.name != null
                    ? book.author.name.toLowerCase()
                    : "";

            if (title.contains(q) || author.contains(q)) {
                filteredBooks.add(book);
            }
        }

        if (filteredBooks.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            txtEmpty.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }
}