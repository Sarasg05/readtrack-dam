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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieData;

import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    TextView txtBooksCompleted;
    TextView txtPagesRead;
    TextView txtGoal;
    TextView txtProgress;

    ProgressBar progressBar;
    PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        txtBooksCompleted = view.findViewById(R.id.txtBooksCompleted);
        txtPagesRead = view.findViewById(R.id.txtPagesRead);
        txtGoal = view.findViewById(R.id.txtGoal);
        txtProgress = view.findViewById(R.id.txtProgress);

        progressBar = view.findViewById(R.id.progressBar);
        pieChart = view.findViewById(R.id.pieChart);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("app", getContext().MODE_PRIVATE);

        String token = prefs.getString("token", "");

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getStats(token).enqueue(new Callback<StatsResponse>() {

            @Override
            public void onResponse(Call<StatsResponse> call, Response<StatsResponse> response) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {

                    StatsResponse stats = response.body();

                    txtBooksCompleted.setText(
                            "Books completed: " + (String.valueOf(stats.books_completed))
                    );

                    txtPagesRead.setText(
                            "Pages read: " + (String.valueOf(stats.pages_read))
                    );

                    txtGoal.setText(
                            "Goal: " + stats.target_books
                    );

                    txtProgress.setText(
                            "Progress: " + stats.progress + "%"
                    );

                    progressBar.setProgress(stats.progress);

                    setupChart(stats);

                }else {
                    Log.e("STATS_ERROR", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("STATS_ERROR", "Error loading stats", t);
            }
        });

        return view;
    }

    private void setupChart(StatsResponse stats) {

        int completed = stats.books_completed;
        int target = stats.target_books;
        int remaining = Math.max(target - completed, 0);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(completed, "Completed"));
        entries.add(new PieEntry(remaining, "Remaining"));

        PieDataSet dataSet = new PieDataSet(entries, "Goal Progress");

        dataSet.setColors(new int[]{
                android.R.color.holo_green_light,
                android.R.color.holo_red_light
        }, getContext());

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Goal");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);

        pieChart.invalidate();

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);

        pieChart.setEntryLabelTextSize(12f);
        pieChart.setCenterText("Goal Progress");
        pieChart.setCenterTextSize(14f);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}