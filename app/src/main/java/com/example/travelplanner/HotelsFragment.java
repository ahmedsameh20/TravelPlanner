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

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HotelsFragment extends Fragment {

    private TravelRepository repo;
    private RecyclerView recyclerHotels;
    private Spinner spinnerCities;
    private ChipGroup chipGroupSort;
    private HotelAdapter adapter;
    private List<Hotel> allHotels = new ArrayList<>();
    private List<City> cities = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_hotels, container, false);

        repo = Repo.travel(requireContext());

        recyclerHotels = v.findViewById(R.id.recyclerHotels);
        recyclerHotels.setLayoutManager(new LinearLayoutManager(requireContext()));

        spinnerCities = v.findViewById(R.id.spinnerCities);
        chipGroupSort = v.findViewById(R.id.chipGroupSort);

        allHotels = repo.getHotels();
        cities = repo.getCities();

        adapter = new HotelAdapter(requireContext(), repo, allHotels);
        recyclerHotels.setAdapter(adapter);

        setupSpinner();
        chipGroupSort.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilters());

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

        int initialPos = 0;
        Integer pendingCityId = NavState.consumePendingCityFilter();
        if (pendingCityId != null) {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).id == pendingCityId) { initialPos = i + 1; break; }
            }
        }
        spinnerCities.setSelection(initialPos);

        spinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (initialPos != 0) applyFilters();
    }

    private void applyFilters() {
        int position = spinnerCities.getSelectedItemPosition();
        List<Hotel> filtered;
        if (position <= 0) {
            filtered = new ArrayList<>(allHotels);
        } else {
            City selected = cities.get(position - 1);
            filtered = new ArrayList<>();
            for (Hotel h : allHotels) if (h.cityId == selected.id) filtered.add(h);
        }

        int checkedId = chipGroupSort.getCheckedChipId();
        if (checkedId == R.id.chipPriceAsc) {
            Collections.sort(filtered, Comparator.comparingDouble(h -> h.price));
        } else if (checkedId == R.id.chipPriceDesc) {
            Collections.sort(filtered, (a, b) -> Double.compare(b.price, a.price));
        } else if (checkedId == R.id.chipRating) {
            List<Hotel> highRated = new ArrayList<>();
            for (Hotel h : filtered) if (HotelMeta.of(h).rating >= 4.0) highRated.add(h);
            filtered = highRated;
        }

        adapter.updateData(filtered);
    }
}
