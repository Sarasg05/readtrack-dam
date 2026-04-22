package com.ssg.readtrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ssg.readtrack.R;
import com.ssg.readtrack.model.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(Book book);
    }

    private List<Book> books;
    private OnItemClickListener listener;

    public BookAdapter(List<Book> books, OnItemClickListener listener){

        this.books = books;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, total_pages, genres;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.txtTitle);
            author = view.findViewById(R.id.txtAuthor);
            total_pages = view.findViewById(R.id.txtTotalPages);
            genres = view.findViewById(R.id.txtGenres);
            image = view.findViewById(R.id.imgBook);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Book book = books.get(position);
        holder.title.setText(book.title);
        holder.author.setText(book.author);
        holder.total_pages.setText(book.total_pages);
        String genre = "";
        if (book.genres != null && !book.genres.isEmpty()) {
            genre = book.genres.get(0);
        }
        holder.genres.setText(genre);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (listener != null && pos != RecyclerView.NO_POSITION) {
                listener.onItemClick(books.get(pos));
            }
        });
    }

    @Override
    public int getItemCount(){
        return books.size();
    }

}
