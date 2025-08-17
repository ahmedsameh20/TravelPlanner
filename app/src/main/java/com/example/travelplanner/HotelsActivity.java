
package com.example.travelplanner;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public class HotelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);

        Spinner sp = findViewById(R.id.spinnerHotelCity);
        RecyclerView rv = findViewById(R.id.rvHotels);
        Button btnLoad = findViewById(R.id.btnLoadHotels);

        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList("Cairo", "Dubai", "Paris", "Istanbul")));

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CityAdapter(position -> {})); // placeholder adapter to satisfy RecyclerView usage

        btnLoad.setOnClickListener(v -> Toast.makeText(this, "Load hotels for: " + sp.getSelectedItem(), Toast.LENGTH_SHORT).show());
    }
}
