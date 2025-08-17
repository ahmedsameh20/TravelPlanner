
package com.example.travelplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FlightsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        EditText etFrom = findViewById(R.id.etFrom);
        EditText etTo = findViewById(R.id.etTo);
        RadioGroup rgClass = findViewById(R.id.rgClass);
        Button btnSearch = findViewById(R.id.btnSearchFlight);

        btnSearch.setOnClickListener(v -> {
            int checked = rgClass.getCheckedRadioButtonId();
            String cls = (checked == R.id.rbEconomy) ? "Economy" : (checked == R.id.rbBusiness) ? "Business" : "Unknown";
            String from = etFrom.getText().toString().trim();
            String to = etTo.getText().toString().trim();

            Toast.makeText(this, "Searching flights: " + from + " → " + to + " (" + cls + ")", Toast.LENGTH_SHORT).show();
        });
    }
}
