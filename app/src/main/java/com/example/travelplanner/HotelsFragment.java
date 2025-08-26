package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HotelsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_hotels, container, false);

        RecyclerView recyclerHotels = v.findViewById(R.id.recyclerHotels);
        recyclerHotels.setLayoutManager(new LinearLayoutManager(requireContext()));

        DBHelper dbHelper = new DBHelper(requireContext());
        List<Hotel> hotels = dbHelper.getAllHotels();

        HotelAdapter adapter = new HotelAdapter(hotels, new HotelAdapter.Listener() {
            @Override
            public void onClick(Hotel h) {

            }

            @Override
            public void onToggleFavorite(Hotel h) {

                if (h.isFavorite()) {
                    Prefs.toggleFav(requireContext(), "Hotel: " + h.id);
                }
            }
        });

        recyclerHotels.setAdapter(adapter);

        return v;
    }
}
