
package com.example.travelplanner;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class FlightsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText etFrom, etTo;
    private Spinner spinnerClass;
    private ListView listView;
    private ArrayList<Integer> currentFlightIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights);

        dbHelper = new DBHelper(this);

        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        spinnerClass = findViewById(R.id.spinnerClass);
        listView = findViewById(R.id.flightsListView);
        Button btnSearch = findViewById(R.id.btnSearchFlights);

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Economy","Business"});
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);

        btnSearch.setOnClickListener(v -> loadFlights());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position>=0 && position < currentFlightIds.size()) {
                int flightId = currentFlightIds.get(position);
                pickDateAndBook(flightId, "flight");
            }
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position>=0 && position < currentFlightIds.size()) {
                int flightId = currentFlightIds.get(position);
                addToFavorites(flightId);
                Toast.makeText(this, "Added to favorites: Flight #" + flightId, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void loadFlights() {
        String from = etFrom.getText().toString().trim();
        String to = etTo.getText().toString().trim();
        String cls = (String) spinnerClass.getSelectedItem();
        if (from.isEmpty() || to.isEmpty()) return;

        currentFlightIds.clear();
        ArrayList<String> flights = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, from_city, to_city, class, price FROM flights WHERE from_city=? AND to_city=? AND class=?", new String[]{from, to, cls});
        while (c.moveToNext()) {
            currentFlightIds.add(c.getInt(0));
            flights.add(c.getString(1) + " → " + c.getString(2) + " (" + c.getString(3) + ") - $" + c.getDouble(4));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, flights);
        listView.setAdapter(adapter);
    }

    private void addToFavorites(int refId) {
        int userId = SessionManager.getUserId(this);
        if (userId == -1) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO favorites(user_id, type, ref_id) VALUES(?,?,?)", new Object[]{userId, "flight", refId});
    }

    private void pickDateAndBook(int refId, String type) {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH), d = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month+1) + "/" + year;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put("user_id", SessionManager.getUserId(this));
            v.put("type", type);
            v.put("ref_id", refId);
            v.put("date", date);
            long id = db.insert("bookings", null, v);
            if (id>0) Toast.makeText(this, "Booked " + type + " on " + date, Toast.LENGTH_SHORT).show();
        }, y, m, d);
        dp.show();
    }
}
