package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class FlightsFragment extends Fragment {

    private FlightAdapter adapter;
    private List<Flight> data;
    private TravelRepository repo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_flights, container, false);

        repo = Repo.travel(requireContext());

        RecyclerView rv = v.findViewById(R.id.rvFlights);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        data = repo.getFlights();

        adapter = new FlightAdapter(requireContext(), repo, data);
        rv.setAdapter(adapter);

        EditText etFrom = v.findViewById(R.id.etFrom);
        EditText etTo = v.findViewById(R.id.etTo);
        Spinner spClass = v.findViewById(R.id.spinnerClass);
        Button btnSearch = v.findViewById(R.id.btnSearchFlights);
        ImageButton btnSwap = v.findViewById(R.id.btnSwap);
        ChipGroup chipGroupSort = v.findViewById(R.id.chipGroupSortFlights);

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"", "Economy", "Business"});
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClass.setAdapter(classAdapter);

        btnSwap.setOnClickListener(click -> {
            String tmp = etFrom.getText().toString();
            etFrom.setText(etTo.getText().toString());
            etTo.setText(tmp);
        });

        btnSearch.setOnClickListener(click -> {
            String from = etFrom.getText().toString().trim().toLowerCase();
            String to = etTo.getText().toString().trim().toLowerCase();
            String cls = spClass.getSelectedItem() != null ? spClass.getSelectedItem().toString().trim().toLowerCase() : "";
            adapter.filterFlights(from, to, cls);
        });

        chipGroupSort.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int checkedId = chipGroupSort.getCheckedChipId();
            if (checkedId == R.id.chipFlightPriceAsc) adapter.sortByPrice(true);
            else if (checkedId == R.id.chipFlightPriceDesc) adapter.sortByPrice(false);
            else if (checkedId == R.id.chipFlightDuration) adapter.sortByDuration();
        });

        return v;
    }
}
