package com.ssg.readtrack.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ssg.readtrack.R;
import com.ssg.readtrack.model.Reading;
import com.ssg.readtrack.model.User;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;
import com.ssg.readtrack.ui.LoginActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    TextView txtUsername;
    TextView txtReadingCount;
    TextView txtCompletedCount;
    Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtUsername = view.findViewById(R.id.txtUsername);
        txtReadingCount = view.findViewById(R.id.txtReadingCount);
        txtCompletedCount = view.findViewById(R.id.txtCompletedCount);
        btnLogout = view.findViewById(R.id.btnLogout);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("app", getContext().MODE_PRIVATE);

        String token = prefs.getString("token", "");

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        loadUser(api, token);

        loadReadings(api, token);

        btnLogout.setOnClickListener(v -> {

            prefs.edit().remove("token").apply();

            Intent intent = new Intent(getContext(), LoginActivity.class);

            startActivity(intent);

            requireActivity().finish();
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

                    for (Reading r : response.body()) {

                        if (r.status.equals("reading")) {
                            reading++;
                        }

                        if (r.status.equals("completed")) {
                            completed++;
                        }
                    }

                    txtReadingCount.setText("Reading: " + reading);

                    txtCompletedCount.setText("Completed: " + completed);
                }
            }

            @Override
            public void onFailure(Call<List<Reading>> call, Throwable t) {

            }
        });
    }
}