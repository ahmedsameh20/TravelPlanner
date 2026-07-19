package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryFragment extends Fragment implements BookingHistoryAdapter.Listener {

    private RecyclerView rv;
    private TextView tvEmpty;
    private ProgressBar progress;
    private BookingHistoryAdapter adapter;
    private TravelRepository repo;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_booking_history, container, false);

        rv = v.findViewById(R.id.rvBookings);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        progress = v.findViewById(R.id.progress);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        repo = Repo.travel(requireContext());
        userId = SessionManager.getUserId(requireContext());

        adapter = new BookingHistoryAdapter(new ArrayList<>(), this);
        rv.setAdapter(adapter);

        reload();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        if (progress != null) progress.setVisibility(View.VISIBLE);
        repo.getBookings(userId, new Callback<List<Booking>>() {
            @Override
            public void onSuccess(List<Booking> list) {
                if (!isAdded()) return;
                if (progress != null) progress.setVisibility(View.GONE);
                adapter.replaceData(list);
                tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                if (progress != null) progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load bookings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConfirm(Booking b) {
        repo.updateBookingStatus(userId, b.id, true, false, new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Booking confirmed", Toast.LENGTH_SHORT).show();
                reload();
            }
        });
    }

    @Override
    public void onCancel(Booking b) {
        repo.updateBookingStatus(userId, b.id, false, true, new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                reload();
            }
        });
    }

    @Override
    public void onDelete(Booking b) {
        repo.deleteBooking(userId, b.id, new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Booking deleted", Toast.LENGTH_SHORT).show();
                reload();
            }
        });
    }
}
