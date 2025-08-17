
package com.example.travelplanner;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private DBHelper db;
    private RecyclerView rv;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        db = new DBHelper(this);
        rv = findViewById(R.id.rvFavorites);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteAdapter(new FavoriteAdapter.OnFavAction() {
            @Override
            public void onDelete(long favId) {
                db.deleteFavorite(favId);
                loadFavorites();
                Toast.makeText(FavoritesActivity.this, "Favorite removed", Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);
        loadFavorites();
    }

    private void loadFavorites() {
        Cursor c = db.getAllFavorites();
        List<String> items = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        if (c != null) {
            int idIdx = c.getColumnIndex(DBHelper.COL_FAV_ID);
            int typeIdx = c.getColumnIndex(DBHelper.COL_FAV_TYPE);
            int refIdx = c.getColumnIndex(DBHelper.COL_FAV_REF);
            while (c.moveToNext()) {
                long id = c.getLong(idIdx);
                String type = c.getString(typeIdx);
                long ref = c.getLong(refIdx);
                String text = type + " (id=" + ref + ")";
                items.add(text);
                ids.add(id);
            }
            c.close();
        }
        adapter.setData(items, ids);
    }
}
