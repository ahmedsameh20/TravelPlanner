package com.example.travelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.VH> {

    private List<Hotel> hotels;
    private Context context;
    private DBHelper dbHelper;

    public HotelAdapter(Context context, DBHelper dbHelper, List<Hotel> hotels) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.hotels = (hotels != null) ? hotels : new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Hotel h = hotels.get(position);
        int userId = SessionManager.getUserId(context);

        holder.tvHotelName.setText(h.name);
        holder.tvHotelDetails.setText("Price: $" + h.price);

        boolean fav = dbHelper.isFavorite(userId, h.id, "hotel");
        holder.btnFav.setImageResource(fav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);


        holder.btnFav.setOnClickListener(v -> {
            if (dbHelper.isFavorite(userId, h.id, "hotel")) {
                dbHelper.removeFromFavorites(userId, "hotel", h.id);
                holder.btnFav.setImageResource(R.drawable.ic_heart_outline);
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addToFavorites(userId, "hotel", h.id);
                holder.btnFav.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        });


        holder.btnConfirm.setOnClickListener(v -> pickDateAndBook(h));
    }

    private void pickDateAndBook(Hotel hotel) {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH), d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, dayOfMonth, 0, 0, 0);
            long dateMs = picked.getTimeInMillis();
            String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(picked.getTime());
            String details = hotel.name + " — $" + hotel.price;

            new AlertDialog.Builder(context)
                    .setTitle("Confirm booking")
                    .setMessage("Book " + hotel.name + " for " + dateStr + " at $" + hotel.price + "?")
                    .setPositiveButton("Book", (dialog, which) -> {
                        long id = dbHelper.insertBooking(SessionManager.getUserId(context), "hotel", hotel.id, details, dateMs);
                        if (id > 0) {
                            Toast.makeText(context, "Hotel booked for " + dateStr + "\n" + hotel.name, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }, y, m, d);
        dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dp.show();
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public void updateData(List<Hotel> newHotels) {
        this.hotels = newHotels;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvHotelName, tvHotelDetails;
        ImageButton btnFav;
        Button btnConfirm;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvHotelDetails = itemView.findViewById(R.id.tvHotelDetails);
            btnFav = itemView.findViewById(R.id.btnFav);
            btnConfirm = itemView.findViewById(R.id.btnConfirmBooking);
        }
    }
}
