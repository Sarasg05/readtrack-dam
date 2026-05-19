package com.ssg.readtrack.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ssg.readtrack.model.User;
import com.ssg.readtrack.network.ApiService;
import com.ssg.readtrack.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                getSharedPreferences("app", MODE_PRIVATE);

        String token = prefs.getString("token", "");

        // NO TOKEN
        if (token.isEmpty()) {

            startActivity(
                    new Intent(this, LoginActivity.class)
            );

            finish();
            return;
        }

        // VALIDATE TOKEN
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getMe("Bearer " + token).enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {

                    // TOKEN OK
                    startActivity(
                            new Intent(SplashActivity.this, MainActivity.class)
                    );

                } else {

                    // TOKEN INVALID
                    prefs.edit().remove("token").apply();

                    startActivity(
                            new Intent(SplashActivity.this, LoginActivity.class)
                    );
                }

                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                // ERROR -> LOGIN
                startActivity(
                        new Intent(SplashActivity.this, LoginActivity.class)
                );

                finish();
            }
        });
    }
}