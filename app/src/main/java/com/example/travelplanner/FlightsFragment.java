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

    private FlightAdapter adapter;
    private List<Flight> data;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_flights, container, false);

        dbHelper = new DBHelper(requireContext());

        RecyclerView rv = v.findViewById(R.id.rvFlights);
        if (rv != null) rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        data = dbHelper.getAllFlights();

        adapter = new FlightAdapter(requireContext(), dbHelper, data);
        if (rv != null) rv.setAdapter(adapter);

        EditText etFrom = v.findViewById(R.id.etFrom);
        EditText etTo = v.findViewById(R.id.etTo);
        Spinner spClass = v.findViewById(R.id.spinnerClass);
        Button btnSearch = v.findViewById(R.id.btnSearchFlights);

        if (spClass != null) {
            ArrayAdapter<String> classAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, new String[]{"", "Economy", "Business"});
            classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spClass.setAdapter(classAdapter);
        }

        if (btnSearch != null) {
            btnSearch.setOnClickListener(click -> {
                String from = etFrom != null ? etFrom.getText().toString().trim().toLowerCase() : "";
                String to = etTo != null ? etTo.getText().toString().trim().toLowerCase() : "";
                String cls = spClass != null && spClass.getSelectedItem() != null ?
                        spClass.getSelectedItem().toString().trim().toLowerCase() : "";

                adapter.filterFlights(from, to, cls);
            });
        }

        return v;
    }
}
