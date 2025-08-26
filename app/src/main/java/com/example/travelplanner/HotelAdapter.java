package com.example.travelplanner;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        // تحديث أيقونة القلب حسب قاعدة البيانات
        boolean fav = dbHelper.isFavorite(userId, h.id, "hotel");
        holder.btnFav.setImageResource(fav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        // الضغط على القلب يضيف أو يحذف من المفضلات
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

        // زر الحجز
        holder.btnConfirm.setOnClickListener(v -> pickDateAndBook(h));
    }

    private void pickDateAndBook(Hotel hotel) {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH), d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("user_id", SessionManager.getUserId(context));
            values.put("type", "hotel");
            values.put("ref_id", hotel.id);
            values.put("date", date);

            long id = db.insert("bookings", null, values);
            if (id > 0) {
                Toast.makeText(context, "Hotel booked on " + date + "\n" + hotel.name, Toast.LENGTH_SHORT).show();
            }
        }, y, m, d);
        dp.show();
    }

    @Override
    public int getItemCount() {
        return hotels.size();
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
