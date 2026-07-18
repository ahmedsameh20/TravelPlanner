package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private BookingHistoryAdapter adapter;
    private DBHelper db;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_booking_history, container, false);

        rv = v.findViewById(R.id.rvBookings);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = new DBHelper(requireContext());
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
        List<Booking> list = db.getAllBookings(userId);
        adapter.replaceData(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onConfirm(Booking b) {
        db.updateBookingStatus(userId, b.id, true, false);
        Toast.makeText(requireContext(), "Booking confirmed", Toast.LENGTH_SHORT).show();
        reload();
    }

    @Override
    public void onCancel(Booking b) {
        db.updateBookingStatus(userId, b.id, false, true);
        Toast.makeText(requireContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
        reload();
    }

    @Override
    public void onDelete(Booking b) {
        db.deleteBooking(userId, b.id);
        Toast.makeText(requireContext(), "Booking deleted", Toast.LENGTH_SHORT).show();
        reload();
    }
}
