package com.example.travelplanner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    private DBHelper dbHelper;
    private ArrayList<Integer> favIds = new ArrayList<>();
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);

        dbHelper = new DBHelper(getContext());
        listView = v.findViewById(R.id.favoritesListView);

        loadFavorites();

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < favIds.size()) {
                int favId = favIds.get(position);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM favorites WHERE id=?", new Object[]{favId});
                loadFavorites();
            }
            return true;
        });

        return v;
    }

    private void loadFavorites() {
        ArrayList<String> favs = new ArrayList<>();
        favIds.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, type, ref_id FROM favorites WHERE user_id=? ORDER BY id DESC",
                new String[]{String.valueOf(SessionManager.getUserId(getContext()))});

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String type = c.getString(1);
            int refId = c.getInt(2);
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

            favIds.add(id);
            favs.add(type.toUpperCase() + ": " + name);
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, favs);
        listView.setAdapter(adapter);
    }
}
