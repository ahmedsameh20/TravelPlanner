package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlightsFragment extends Fragment {

    private FlightAdapter ad;
    private List<Flight> data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_flights, container, false);

        RecyclerView rv = v.findViewById(R.id.rvFlights);
        if (rv != null) rv.setLayoutManager(new LinearLayoutManager(requireContext()));


        data = DataProvider.getFlights(requireContext());


        ad = new FlightAdapter(requireContext(), data, flight -> {

            Prefs.toggleFav(requireContext(), "Flight: " + flight.id);
        });

        if (rv != null) rv.setAdapter(ad);


        EditText etFrom = v.findViewById(R.id.etFrom);
        EditText etTo = v.findViewById(R.id.etTo);
        Spinner spClass = v.findViewById(R.id.spinnerClass);
        Button btn = v.findViewById(R.id.btnSearchFlights);


        if (spClass != null) {
            ArrayAdapter<String> classAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, new String[]{"", "Economy", "Business"});
            classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spClass.setAdapter(classAdapter);
        }


        if (btn != null) {
            btn.setOnClickListener(click -> {
                String from = etFrom != null ? etFrom.getText().toString().trim() : "";
                String to = etTo != null ? etTo.getText().toString().trim() : "";
                String cls = spClass != null && spClass.getSelectedItem() != null ? spClass.getSelectedItem().toString() : "";

                String filter = (from + " " + to + " " + cls).trim();
                ad.getFilter().filter(filter);
            });
        }

        return v;
    }
}
