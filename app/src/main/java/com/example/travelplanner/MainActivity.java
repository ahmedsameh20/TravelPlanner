package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCities = findViewById(R.id.btnCities);
        Button btnFlights = findViewById(R.id.btnFlights);
        Button btnHotels = findViewById(R.id.btnHotels);
        Button btnFavorites = findViewById(R.id.btnFavorites);

        btnCities.setOnClickListener(v ->
                startActivity(new Intent(this, CitiesActivity.class)));

        btnFlights.setOnClickListener(v ->
                startActivity(new Intent(this, FlightsActivity.class)));

        btnHotels.setOnClickListener(v ->
                startActivity(new Intent(this, HotelsActivity.class)));

        btnFavorites.setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
