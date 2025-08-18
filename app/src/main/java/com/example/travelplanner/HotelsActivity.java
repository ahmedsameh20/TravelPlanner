
package com.example.travelplanner;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class HotelsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Spinner spinnerCities;
    private ListView listView;
    private ArrayList<Integer> currentHotelIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);

        dbHelper = new DBHelper(this);

        spinnerCities = findViewById(R.id.spinnerCities);
        listView = findViewById(R.id.hotelsListView);

        loadCities();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >=0 && position < currentHotelIds.size()) {
                int hotelId = currentHotelIds.get(position);
                // ask for booking date
                pickDateAndBook(hotelId, "hotel");
            }
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position >=0 && position < currentHotelIds.size()) {
                int hotelId = currentHotelIds.get(position);
                addToFavorites(hotelId);
                Toast.makeText(this, "Added to favorites: Hotel #" + hotelId, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void loadCities() {
        ArrayList<String> cities = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT name FROM cities ORDER BY name ASC", null);
        while (c.moveToNext()) cities.add(c.getString(0));
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCities.setAdapter(adapter);

        spinnerCities.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadHotels();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadHotels() {
        String city = (String) spinnerCities.getSelectedItem();
        if (city == null) return;
        currentHotelIds.clear();
        ArrayList<String> hotels = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT h.id, h.name, h.price FROM hotels h JOIN cities c ON h.city_id=c.id WHERE c.name=?", new String[]{city});
        while (c.moveToNext()) {
            currentHotelIds.add(c.getInt(0));
            hotels.add(c.getString(1) + " - $" + c.getDouble(2));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hotels);
        listView.setAdapter(adapter);
    }

    private void addToFavorites(int refId) {
        int userId = SessionManager.getUserId(this);
        if (userId == -1) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO favorites(user_id, type, ref_id) VALUES(?,?,?)", new Object[]{userId, "hotel", refId});
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
