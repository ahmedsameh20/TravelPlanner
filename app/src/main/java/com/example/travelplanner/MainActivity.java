package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            int id = item.getItemId();
            if (id == R.id.nav_cities) f = new CitiesFragment();
            else if (id == R.id.nav_hotels) f = new HotelsFragment();
            else if (id == R.id.nav_flights) f = new FlightsFragment();
            else if (id == R.id.nav_favorites) f = new FavoritesFragment();
            else if (id == R.id.nav_bookings) f = new BookingHistoryFragment();
            if (f != null) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
            return true;
        });
        if (s == null) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CitiesFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SessionManager.clear(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_about) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
