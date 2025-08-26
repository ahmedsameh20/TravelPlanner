package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class BookingsFragment extends Fragment {

    private DBHelper db;
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        db = new DBHelper(requireContext());
        lv = view.findViewById(R.id.bookingsListView);

        loadBookings();

        return view;
    }

    private void loadBookings() {
        int userId = SessionManager.getUserId(requireContext());
        List<String> data = db.getAllBookings(userId);
        lv.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, data));
    }
}
