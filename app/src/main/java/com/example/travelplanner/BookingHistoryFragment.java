/*package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnConfirmCancel;
    private BookingHistoryAdapter adapter;
    private ConfirmedBookings viewModel;

    private boolean isCurrent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_past_history, container, false);

        recyclerView = view.findViewById(R.id.rv_booking_history);
        btnConfirmCancel = view.findViewById(R.id.btn_confirm_cancel);

        viewModel = new ViewModelProvider(requireActivity()).get(ConfirmedBookings.class);

        Bundle args = getArguments();
        List<Booking> bookings = null;
        if (args != null) {
            bookings = args.getParcelableArrayList("bookings");
            isCurrent = args.getBoolean("isCurrent");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookingHistoryAdapter(bookings, isCurrent);
        recyclerView.setAdapter(adapter);

        btnConfirmCancel.setVisibility(isCurrent ? View.VISIBLE : View.GONE);

        viewModel.getConfirmedBookings().observe(getViewLifecycleOwner(), updatedBookings -> {
            adapter.updateData(updatedBookings, isCurrent);
        });

        return view;
    }
}*/

