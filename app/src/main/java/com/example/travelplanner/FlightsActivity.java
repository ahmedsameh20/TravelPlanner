package com.example.travelplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FlightsActivity extends AppCompatActivity {

    private EditText etFrom, etTo;
    private Spinner spinnerClass;
    private Button btnSearch;
    private RecyclerView rvFlights;
    private ArrayList<Flight> flightList = new ArrayList<>();
    private FlightAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        spinnerClass = findViewById(R.id.spinnerClass);
        btnSearch = findViewById(R.id.btnSearchFlights);
        rvFlights = findViewById(R.id.rvFlights);

        spinnerClass.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Economy", "Business"}));


        flightList.add(new Flight(1,"Cairo","Paris","Economy",500));
        flightList.add(new Flight(2,"Cairo","London","Business",1200));
        flightList.add(new Flight(3,"New York","Tokyo","Economy",800));

        adapter = new FlightAdapter(this, flightList, flight -> {});
        rvFlights.setAdapter(adapter);
        rvFlights.setLayoutManager(new LinearLayoutManager(this));


        btnSearch.setOnClickListener(v -> {

        });
    }
}
