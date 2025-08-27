
package com.example.travelplanner;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listView;
    private final ArrayList<Integer> favIds = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        dbHelper = new DBHelper(this);
        listView = findViewById(R.id.favoritesListView);

        loadFavorites();

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < favIds.size()) {
                int favId = favIds.get(position);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM favorites WHERE id=?", new Object[]{favId});
                Toast.makeText(this, "Removed favorite #" + favId, Toast.LENGTH_SHORT).show();
                loadFavorites();
            }
            return true;
        });
    }

    private void loadFavorites() {
        ArrayList<String> favs = new ArrayList<>();
        favIds.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, type, ref_id FROM favorites WHERE user_id=? ORDER BY id DESC", new String[]{String.valueOf(SessionManager.getUserId(this))});
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String type = c.getString(1);
            int refId = c.getInt(2);
            String name = "";

            if (type.equals("city")) {
                Cursor cc = db.rawQuery("SELECT name FROM cities WHERE id=?", new String[]{String.valueOf(refId)});
                if (cc.moveToFirst()) name = cc.getString(0);
                cc.close();
            } else if (type.equals("hotel")) {
                Cursor cc = db.rawQuery("SELECT name FROM hotels WHERE id=?", new String[]{String.valueOf(refId)});
                if (cc.moveToFirst()) name = cc.getString(0);
                cc.close();
            } else if (type.equals("flight")) {
                Cursor cc = db.rawQuery("SELECT from_city, to_city FROM flights WHERE id=?", new String[]{String.valueOf(refId)});
                if (cc.moveToFirst()) name = cc.getString(0) + " → " + cc.getString(1);
                cc.close();
            }

            favIds.add(id);
            favs.add(type.toUpperCase() + ": " + name + "  [fav#" + id + "]");
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, favs);
        listView.setAdapter(adapter);
    }
}
