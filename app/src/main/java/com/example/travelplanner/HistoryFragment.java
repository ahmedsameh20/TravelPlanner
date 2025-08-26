/*package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RadioGroup choices;
    private ConfirmedBookings sharedBookingViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        choices = view.findViewById(R.id.rg_history_choices);
        RadioButton currentBookings = view.findViewById(R.id.rbtn_current);
        RadioButton pastBookings = view.findViewById(R.id.rbtn_past);

        sharedBookingViewModel = new ViewModelProvider(requireActivity())
                .get(ConfirmedBookings.class);

        // Default: show current bookings
        if (savedInstanceState == null) {
            updateFragments(sharedBookingViewModel.getConfirmedBookings().getValue(), true);
            currentBookings.setChecked(true);
        }

        choices.setOnCheckedChangeListener((group, checkedId) -> {
            List<Booking> allBookings = sharedBookingViewModel.getConfirmedBookings().getValue();
            if (checkedId == currentBookings.getId()) {
                updateFragments(allBookings, true);
            } else if (checkedId == pastBookings.getId()) {
                updateFragments(allBookings, false);
            }
        });

        return view;
    }

    private void updateFragments(List<Booking> allBookings, boolean isCurrent) {
        if (allBookings == null) return;

        List<Booking> filteredList = new ArrayList<>();
        Date today = new Date();

        for (Booking booking : allBookings) {
            boolean isPast = booking.getBookingDate().before(today);
            if (isCurrent && !isPast) {
                filteredList.add(booking);
            } else if (!isCurrent && isPast) {
                filteredList.add(booking);
            }
        }

        Fragment fragmentToShow = new BookingHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("bookings", new ArrayList<>(filteredList));
        bundle.putBoolean("isCurrent", isCurrent);
        fragmentToShow.setArguments(bundle);

        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = childFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragmentToShow);
        transaction.commit();
    }
}*/
