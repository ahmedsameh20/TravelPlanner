/*package com.example.travelplanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ConfirmedBookings extends ViewModel {
    private final MutableLiveData<List<Booking>> confirmedBookings =
            new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Booking>> getConfirmedBookings() {
        return confirmedBookings;
    }

    public void addBooking(Booking booking) {
        List<Booking> currentList = new ArrayList<>(confirmedBookings.getValue());
        currentList.add(booking);
        confirmedBookings.setValue(currentList);
    }

    public void cancelBooking(Booking booking) {
        List<Booking> currentList = new ArrayList<>(confirmedBookings.getValue());

        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getBookingId().equals(booking.getBookingId())) {
                currentList.get(i).setCancelled(true);
                break;
            }
        }

        confirmedBookings.setValue(currentList);
    }
}*/
