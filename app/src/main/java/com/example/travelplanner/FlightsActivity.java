package com.example.travelplanner;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlightsActivity extends AppCompatActivity {

    private EditText etFrom, etTo;
    private Spinner spinnerClass;
    private RecyclerView rvFlights;
    private List<Flight> flightList;
    private FlightAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        dbHelper = new DBHelper(this);

        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        spinnerClass = findViewById(R.id.spinnerClass);
        rvFlights = findViewById(R.id.rvFlights);

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"", "Economy", "Business"});
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);

        flightList = dbHelper.getAllFlights();

        adapter = new FlightAdapter(this, dbHelper, flightList);
        rvFlights.setLayoutManager(new LinearLayoutManager(this));
        rvFlights.setAdapter(adapter);
    }
}
