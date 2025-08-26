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

    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_hotels, container, false);

        dbHelper = new DBHelper(requireContext());

        RecyclerView recyclerHotels = v.findViewById(R.id.recyclerHotels);
        recyclerHotels.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Hotel> hotels = dbHelper.getAllHotels();

        // استخدم النسخة الجديدة من HotelAdapter
        HotelAdapter adapter = new HotelAdapter(requireContext(), dbHelper, hotels);

        recyclerHotels.setAdapter(adapter);

        return v;
    }
}
