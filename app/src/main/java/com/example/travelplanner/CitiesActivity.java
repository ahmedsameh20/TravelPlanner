
package com.example.travelplanner;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CitiesActivity extends AppCompatActivity {

    private DBHelper db;
    private CityAdapter adapter;
    private Spinner spinner;
    private RecyclerView rv;
    private Button btnFilter;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        db = new DBHelper(this);
        spinner = findViewById(R.id.spinnerCountry);
        rv = findViewById(R.id.rvCities);
        btnFilter = findViewById(R.id.btnPopupFilter);

        // RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CityAdapter(position -> {
            selectedPosition = position;
            // show context menu for the item view
        });
        rv.setAdapter(adapter);

        // Register context menu on RecyclerView items
        registerForContextMenu(rv);

        // Load data
        loadCountriesToSpinner();
        loadCities(null);

        // Spinner filter action
        findViewById(R.id.btnApplyFilter).setOnClickListener(v -> {
            String country = spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : null;
            if (country != null && country.equals("All")) country = null;
            loadCities(country);
        });

        // Popup menu example
        btnFilter.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_popup_filters, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.filter_by_name) {
                Toast.makeText(this, "Filter by name clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.filter_by_country) {
                Toast.makeText(this, "Filter by country clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void loadCountriesToSpinner() {
        // Build unique list of countries from cities table
        Cursor c = db.getAllCities();
        Set<String> countries = new HashSet<>();
        countries.add("All");
        if (c != null) {
            while (c.moveToNext()) {
                int idx = c.getColumnIndex(DBHelper.COL_CITY_COUNTRY);
                if (idx >= 0) countries.add(c.getString(idx));
            }
            c.close();
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(countries));
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);
    }

    private void loadCities(String country) {
        Cursor c = (country == null) ? db.getAllCities() : db.getCitiesByCountry(country);
        List<City> list = new ArrayList<>();
        if (c != null) {
            int idIdx = c.getColumnIndex(DBHelper.COL_CITY_ID);
            int nameIdx = c.getColumnIndex(DBHelper.COL_CITY_NAME);
            int countryIdx = c.getColumnIndex(DBHelper.COL_CITY_COUNTRY);
            int descIdx = c.getColumnIndex(DBHelper.COL_CITY_DESC);
            while (c.moveToNext()) {
                long id = c.getLong(idIdx);
                String name = c.getString(nameIdx);
                String countryVal = c.getString(countryIdx);
                String desc = c.getString(descIdx);
                list.add(new City(id, name, countryVal, desc));
            }
            c.close();
        }
        adapter.setData(list);
    }

    // Context menu for long-pressed item
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cities_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (selectedPosition >= 0) {
            City selected = adapter.getItem(selectedPosition);
            if (item.getItemId() == R.id.ctx_add_favorite) {
                Toast.makeText(this, "Added to favorites: " + selected.name, Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.ctx_delete_city) {
                int rows = db.deleteCity(selected.id);
                Toast.makeText(this, "Deleted rows: " + rows, Toast.LENGTH_SHORT).show();
                loadCities(null);
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }
}
