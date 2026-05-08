package com.ssg.readtrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ssg.readtrack.R;
import com.ssg.readtrack.model.Book;

import java.util.List;

public class BookHorizontalAdapter extends RecyclerView.Adapter<BookHorizontalAdapter.ViewHolder> {

    public interface OnBookClick {
        void onClick(Book book);
    }

    List<Book> books;
    OnBookClick listener;

    public BookHorizontalAdapter(List<Book> books, OnBookClick listener) {
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_horizontal, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Book book = books.get(position);

        holder.txtTitle.setText(book.title);

        Glide.with(holder.itemView.getContext())
                .load(book.cover)
                .into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> {
            listener.onClick(book);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCover;
        TextView txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCover = itemView.findViewById(R.id.imgCover);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
