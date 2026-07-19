package com.example.travelplanner;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class FlightDetailActivity extends AppCompatActivity {

    public static final String EXTRA_FLIGHT_ID = "flight_id";

    private static final int[] PLACEHOLDERS = {
            R.drawable.bg_placeholder_1, R.drawable.bg_placeholder_2, R.drawable.bg_placeholder_3,
            R.drawable.bg_placeholder_4, R.drawable.bg_placeholder_5, R.drawable.bg_placeholder_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        int flightId = getIntent().getIntExtra(EXTRA_FLIGHT_ID, -1);
        TravelRepository repo = Repo.travel(this);
        Flight flight = repo.getFlight(flightId);
        if (flight == null) {
            finish();
            return;
        }

        FlightMeta meta = FlightMeta.of(flight);

        findViewById(R.id.ivHero).setBackgroundResource(PLACEHOLDERS[flight.id % PLACEHOLDERS.length]);

        ((TextView) findViewById(R.id.tvAirline)).setText(meta.airline);
        ((TextView) findViewById(R.id.tvRoute)).setText(flight.from + " → " + flight.to);
        ((TextView) findViewById(R.id.tvDepartTime)).setText(meta.departTime);
        ((TextView) findViewById(R.id.tvArriveTime)).setText(meta.arriveTime);
        ((TextView) findViewById(R.id.tvFrom)).setText(flight.from);
        ((TextView) findViewById(R.id.tvTo)).setText(flight.to);
        ((TextView) findViewById(R.id.tvDuration)).setText(meta.durationLabel());
        ((TextView) findViewById(R.id.tvStops)).setText(meta.stopsLabel());
        ((TextView) findViewById(R.id.tvClass)).setText("Class: " + flight.cls);
        ((TextView) findViewById(R.id.tvPrice)).setText("$" + flight.price);

        findViewById(R.id.btnBookNow).setOnClickListener(v -> BookingFlow.bookFlight(this, flight, null));
    }
}
