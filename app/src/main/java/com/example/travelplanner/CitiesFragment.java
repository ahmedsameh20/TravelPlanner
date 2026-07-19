package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CitiesFragment extends Fragment {

    private CityCardAdapter adapter;
    private List<City> allCities = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cities, container, false);

        RecyclerView rv = v.findViewById(R.id.rvList);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        TravelRepository repo = Repo.travel(requireContext());
        allCities = repo.getCities();

        adapter = new CityCardAdapter(requireContext(), repo, new ArrayList<>(allCities), city -> {
            NavState.requestCityFilter(city.id);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(R.id.nav_hotels);
            }
        });
        rv.setAdapter(adapter);

        SearchView sv = v.findViewById(R.id.search_view);
        if (sv != null) {
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });
        }

        return v;
    }

    private void filter(String query) {
        String q = query == null ? "" : query.toLowerCase().trim();
        List<City> filtered = new ArrayList<>();
        for (City c : allCities) {
            if (q.isEmpty() || c.name.toLowerCase().contains(q)) filtered.add(c);
        }
        adapter.updateData(filtered);
    }
}
