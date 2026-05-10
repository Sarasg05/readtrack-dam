package com.ssg.readtrack.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ssg.readtrack.R;
import com.ssg.readtrack.model.StatsResponse;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsFragment extends Fragment {

    TextView txtBooksCompleted;
    TextView txtPagesRead;
    TextView txtGoal;
    TextView txtProgress;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        txtBooksCompleted = view.findViewById(R.id.txtBooksCompleted);
        txtPagesRead = view.findViewById(R.id.txtPagesRead);
        txtGoal = view.findViewById(R.id.txtGoal);
        txtProgress = view.findViewById(R.id.txtProgress);

        progressBar = view.findViewById(R.id.progressBar);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("app", getContext().MODE_PRIVATE);

        String token = prefs.getString("token", "");

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getStats("Token " + token).enqueue(new Callback<StatsResponse>() {

            @Override
            public void onResponse(Call<StatsResponse> call, Response<StatsResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    StatsResponse stats = response.body();

                    txtBooksCompleted.setText(
                            "Books completed: " + stats.books_completed
                    );

                    txtPagesRead.setText(
                            "Pages read: " + stats.pages_read
                    );

                    txtGoal.setText(
                            "Goal: " + stats.target_books
                    );

                    txtProgress.setText(
                            "Progress: " + stats.progress + "%"
                    );

                    progressBar.setProgress(stats.progress);
                }else {
                    Log.e("STATS_ERROR", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatsResponse> call, Throwable t) {
                Log.e("STATS_ERROR", "Error loading stats", t);
            }
        });

        return view;
    }
}