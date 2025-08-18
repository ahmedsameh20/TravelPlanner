
package com.example.travelplanner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CitiesActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_simple);

        dbHelper = new DBHelper(this);

        ListView listView = findViewById(R.id.simpleListView);
        ArrayList<String> cities = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, name FROM cities ORDER BY name ASC", null);
        while (c.moveToNext()) {
            cities.add(c.getInt(0) + ". " + c.getString(1));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, cities);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            int refId = extractId((String) parent.getItemAtPosition(position));
            addToFavorites(refId);
            Toast.makeText(this, "Added to favorites: City #" + refId, Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private int extractId(String prefixed) {
        try {
            int dot = prefixed.indexOf('.');
            return Integer.parseInt(prefixed.substring(0, dot));
        } catch (Exception e) {
            return -1;
        }
    }

    private void addToFavorites(int refId) {
        int userId = SessionManager.getUserId(this);
        if (userId == -1) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO favorites(user_id, type, ref_id) VALUES(?,?,?)", new Object[]{userId, "city", refId});
    }
}
