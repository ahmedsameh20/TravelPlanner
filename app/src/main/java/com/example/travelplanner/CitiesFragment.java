package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CitiesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cities, container, false);

        RecyclerView rv = v.findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<City> data = DataProvider.getCities();
        SimpleAdapter<City> ad = new SimpleAdapter<>(data, new SimpleAdapter.Binder<City>() {
            @Override
            public void bind(SimpleAdapter.VH h, City item) {
                h.tv1.setText(item.name);
                h.tv2.setText("ID: " + item.id);
                h.itemView.setOnClickListener(x -> Prefs.toggleFav(requireContext(), "City: " + item.name));
            }

            @Override
            public String asString(City item) {
                return item.name;
            }
        });

        rv.setAdapter(ad);

        SearchView sv = v.findViewById(R.id.search_view);
        if (sv != null) {
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    ad.getFilter().filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ad.getFilter().filter(newText);
                    return true;
                }
            });
        }

        return v;
    }
}
