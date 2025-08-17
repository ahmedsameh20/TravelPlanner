
package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

        btnCities.setOnClickListener(v -> startActivity(new Intent(this, CitiesActivity.class)));
        btnFlights.setOnClickListener(v -> startActivity(new Intent(this, FlightsActivity.class)));
        btnHotels.setOnClickListener(v -> startActivity(new Intent(this, HotelsActivity.class)));
        btnFavorites.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
    }

    // Options Menu + Submenu example
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sub_sort_az) {
            Toast.makeText(this, "Sort A-Z (global)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sub_sort_za) {
            Toast.makeText(this, "Sort Z-A (global)", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
