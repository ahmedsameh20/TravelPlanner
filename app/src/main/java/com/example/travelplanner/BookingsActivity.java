package com.example.travelplanner;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class BookingsActivity extends AppCompatActivity {

    private DBHelper db;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        db = new DBHelper(this);
        lv = findViewById(R.id.bookingsListView);

        loadBookings();
    }

    private void loadBookings() {
        int userId = SessionManager.getUserId(this);
        List<String> data = db.getAllBookings(userId);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data));
    }
}
