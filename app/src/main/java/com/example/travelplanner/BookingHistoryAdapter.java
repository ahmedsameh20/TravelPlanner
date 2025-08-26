package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private List<Booking> bookingList;
    private boolean isCurrent;
    private final List<Booking> selectedForCancellation = new ArrayList<>();

    public BookingHistoryAdapter(List<Booking> bookingList, boolean isCurrent) {
        this.bookingList = bookingList != null ? bookingList : new ArrayList<>();
        this.isCurrent = isCurrent;
    }

    @NonNull
    @Override
    public BookingHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_current_past_history, parent, false);
        return new BookingHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHistoryAdapter.ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.bookingInfoTextView.setText(booking.getDetails());

        if (isCurrent) {
            holder.cancelCheckBox.setVisibility(View.VISIBLE);
            holder.cancelCheckBox.setOnCheckedChangeListener(null);

            holder.cancelCheckBox.setChecked(selectedForCancellation.contains(booking));

            holder.cancelCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedForCancellation.contains(booking)) {
                        selectedForCancellation.add(booking);
                    }
                } else {
                    selectedForCancellation.remove(booking);
                }
            });
        } else {
            holder.cancelCheckBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public List<Booking> getSelectedForCancellation() {
        return selectedForCancellation;
    }

    public void updateData(List<Booking> newBookings, boolean isCurrent) {
        this.bookingList = newBookings != null ? newBookings : new ArrayList<>();
        this.isCurrent = isCurrent;
        selectedForCancellation.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView bookingInfoTextView;
        public CheckBox cancelCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingInfoTextView = itemView.findViewById(R.id.tv_booking_info);
            cancelCheckBox = itemView.findViewById(R.id.cb_cancel);
        }
    }
}
