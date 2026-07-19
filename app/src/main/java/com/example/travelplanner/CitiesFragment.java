package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CitiesFragment extends Fragment {

    private CityCardAdapter adapter;
    private List<City> allCities = new ArrayList<>();
    private TravelRepository repo;
    private ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cities, container, false);

        RecyclerView rv = v.findViewById(R.id.rvList);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        progress = v.findViewById(R.id.progress);

        repo = Repo.travel(requireContext());

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

        loadData(rv);

        return v;
    }

    private void loadData(RecyclerView rv) {
        progress.setVisibility(View.VISIBLE);
        repo.getCities(new Callback<List<City>>() {
            @Override
            public void onSuccess(List<City> citiesResult) {
                if (!isAdded()) return;
                allCities = citiesResult;
                String userId = SessionManager.getUserId(requireContext());
                repo.getFavorites(userId, new Callback<List<FavoriteItem>>() {
                    @Override
                    public void onSuccess(List<FavoriteItem> favs) {
                        if (!isAdded()) return;
                        Set<String> keys = new HashSet<>();
                        for (FavoriteItem f : favs) keys.add(f.type + ":" + f.refId);

                        progress.setVisibility(View.GONE);
                        adapter = new CityCardAdapter(requireContext(), repo, new ArrayList<>(allCities), keys, city -> {
                            NavState.requestCityFilter(city.id);
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).selectTab(R.id.nav_hotels);
                            }
                        });
                        rv.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Exception e) {
                        if (!isAdded()) return;
                        showError(e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                showError(e);
            }
        });
    }

    private void showError(Exception e) {
        if (!isAdded()) return;
        progress.setVisibility(View.GONE);
        Toast.makeText(requireContext(), "Failed to load cities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void filter(String query) {
        if (adapter == null) return;
        String q = query == null ? "" : query.toLowerCase().trim();
        List<City> filtered = new ArrayList<>();
        for (City c : allCities) {
            if (q.isEmpty() || c.name.toLowerCase().contains(q)) filtered.add(c);
        }
        adapter.updateData(filtered);
    }
}
