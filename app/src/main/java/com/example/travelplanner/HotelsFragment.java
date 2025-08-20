package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HotelsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hotels, container, false);

        RecyclerView rv = v.findViewById(R.id.rvHotels);
        if (rv != null) rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Hotel> allHotels = DataProvider.getHotels(requireContext());
        SimpleAdapter<Hotel> ad = new SimpleAdapter<>(allHotels, new SimpleAdapter.Binder<Hotel>() {
            @Override
            public void bind(SimpleAdapter.VH h, Hotel item) {
                h.title.setText(item.name);
                h.subtitle.setText("$" + item.price);
                h.itemView.setOnClickListener(x -> Prefs.toggleFav(requireContext(), "Hotel: " + item.id));
            }

            @Override
            public String asString(Hotel item) {
                return item.name;
            }
        });
        if (rv != null) rv.setAdapter(ad);

        Spinner sp = v.findViewById(R.id.spinnerCities);
        if (sp != null) {
            List<City> cities = DataProvider.getCities();
            List<String> names = new ArrayList<>();
            names.add("All");
            for (City c : cities) names.add(c.name);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, names);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(spinnerAdapter);

            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String sel = names.get(position);
                    if ("All".equals(sel)) {
                        ad.updateData(allHotels);
                    } else {
                        int cityId = -1;
                        for (City c : cities) if (c.name.equals(sel)) cityId = c.id;
                        List<Hotel> filtered = new ArrayList<>();
                        for (Hotel hObj : allHotels) if (hObj.cityId == cityId) filtered.add(hObj);
                        ad.updateData(filtered);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    ad.updateData(allHotels);
                }
            });
        }

        return v;
    }
}
