
package com.example.travelplanner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BookingsActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        dbHelper = new DBHelper(this);
        listView = findViewById(R.id.bookingsListView);

        loadBookings();
    }

    private void loadBookings() {
        ArrayList<String> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT type, ref_id, date FROM bookings WHERE user_id=?", new String[]{String.valueOf(SessionManager.getUserId(this))});

        while (c.moveToNext()) {
            String type = c.getString(0);
            int refId = c.getInt(1);
            String date = c.getString(2);
            String name = "";

            if (type.equals("hotel")) {
                Cursor cc = db.rawQuery("SELECT name FROM hotels WHERE id=?", new String[]{String.valueOf(refId)});
                if (cc.moveToFirst()) name = cc.getString(0);
                cc.close();
            } else if (type.equals("flight")) {
                Cursor cc = db.rawQuery("SELECT from_city, to_city FROM flights WHERE id=?", new String[]{String.valueOf(refId)});
                if (cc.moveToFirst()) name = cc.getString(0) + " → " + cc.getString(1);
                cc.close();
            }
            bookings.add(type.toUpperCase() + ": " + name + " at " + date);
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, bookings);
        listView.setAdapter(adapter);
    }
}
