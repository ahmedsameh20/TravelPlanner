package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HotelsFragment extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView recyclerHotels;
    private Spinner spinnerCities;
    private HotelAdapter adapter;
    private List<Hotel> allHotels = new ArrayList<>();
    private List<City> cities = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_hotels, container, false);

        dbHelper = new DBHelper(requireContext());

        recyclerHotels = v.findViewById(R.id.recyclerHotels);
        recyclerHotels.setLayoutManager(new LinearLayoutManager(requireContext()));

        spinnerCities = v.findViewById(R.id.spinnerCities);

        allHotels = dbHelper.getAllHotels();
        cities = dbHelper.getAllCities();

        adapter = new HotelAdapter(requireContext(), dbHelper, allHotels);
        recyclerHotels.setAdapter(adapter);

        setupSpinner();

        return v;
    }

    private void setupSpinner() {
        List<String> cityNames = new ArrayList<>();
        cityNames.add("All cities");
        for (City c : cities) cityNames.add(c.name);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, cityNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCities.setAdapter(spinnerAdapter);

        spinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Hotel> filtered;
                if (position == 0) {
                    filtered = allHotels;
                } else {
                    City selected = cities.get(position - 1);
                    filtered = new ArrayList<>();
                    for (Hotel h : allHotels) {
                        if (h.cityId == selected.id) filtered.add(h);
                    }
                }
                adapter.updateData(filtered);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
