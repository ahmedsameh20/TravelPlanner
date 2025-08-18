
package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.getUserId(this) == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        Button btnCities = findViewById(R.id.btnCities);
        Button btnFlights = findViewById(R.id.btnFlights);
        Button btnHotels = findViewById(R.id.btnHotels);
        Button btnFavorites = findViewById(R.id.btnFavorites);
        Button btnBookings = findViewById(R.id.btnBookings);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + SessionManager.getUserName(this));

        btnCities.setOnClickListener(v -> startActivity(new Intent(this, CitiesActivity.class)));
        btnFlights.setOnClickListener(v -> startActivity(new Intent(this, FlightsActivity.class)));
        btnHotels.setOnClickListener(v -> startActivity(new Intent(this, HotelsActivity.class)));
        btnFavorites.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
        btnBookings.setOnClickListener(v -> startActivity(new Intent(this, BookingsActivity.class)));
        btnLogout.setOnClickListener(v -> {
            SessionManager.logout(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
