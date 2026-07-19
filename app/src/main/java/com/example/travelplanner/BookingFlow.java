package com.example.travelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** Shared date-pick + confirm + insertBooking flow used by list cards and detail screens. */
public final class BookingFlow {
    private BookingFlow() {}

    public interface OnBooked {
        void onBooked();
    }

    public static void bookHotel(Context context, Hotel hotel, OnBooked callback) {
        pickDateAndConfirm(context, "Book " + hotel.name, hotel.price, (dateStr, dateMs) -> {
            String details = hotel.name + " — $" + hotel.price;
            long id = Repo.travel(context).insertBooking(SessionManager.getUserId(context), "hotel", hotel.id, details, dateMs);
            if (id > 0) {
                Toast.makeText(context, "Hotel booked for " + dateStr + "\n" + hotel.name, Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onBooked();
            }
        });
    }

    public static void bookFlight(Context context, Flight flight, OnBooked callback) {
        String title = "Book " + flight.from + " → " + flight.to;
        pickDateAndConfirm(context, title, flight.price, (dateStr, dateMs) -> {
            String details = flight.from + " → " + flight.to + " (" + flight.cls + ") — $" + flight.price;
            long id = Repo.travel(context).insertBooking(SessionManager.getUserId(context), "flight", flight.id, details, dateMs);
            if (id > 0) {
                Toast.makeText(context, "Flight booked for " + dateStr + "\n" + flight.from + " → " + flight.to, Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onBooked();
            }
        });
    }

    private interface OnDatePicked {
        void onPicked(String dateStr, long dateMs);
    }

    private static void pickDateAndConfirm(Context context, String title, double price, OnDatePicked onPicked) {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH), d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, dayOfMonth, 0, 0, 0);
            long dateMs = picked.getTimeInMillis();
            String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(picked.getTime());

            new AlertDialog.Builder(context)
                    .setTitle("Confirm booking")
                    .setMessage(title + " for " + dateStr + " at $" + price + "?")
                    .setPositiveButton("Book", (dialog, which) -> onPicked.onPicked(dateStr, dateMs))
                    .setNegativeButton("Cancel", null)
                    .show();
        }, y, m, d);
        dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dp.show();
    }
}
