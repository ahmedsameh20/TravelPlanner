package com.example.travelplanner;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.VH> {

    private List<Flight> data;
    private List<Flight> dataFull;
    private Context context;
    private DBHelper dbHelper;

    public FlightAdapter(Context context, DBHelper dbHelper, List<Flight> data) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.data = (data != null) ? data : new ArrayList<>();
        this.dataFull = new ArrayList<>(this.data);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_flight, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Flight f = data.get(position);
        int userId = SessionManager.getUserId(context);

        holder.title.setText(f.from + " → " + f.to);
        holder.subtitle.setText(f.cls + " - $" + f.price);

        boolean fav = dbHelper.isFavorite(userId, f.id, "flight");
        holder.ivFavorite.setImageResource(fav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        holder.ivFavorite.setOnClickListener(v -> {
            if (dbHelper.isFavorite(userId, f.id, "flight")) {
                dbHelper.removeFromFavorites(userId, "flight", f.id);
                holder.ivFavorite.setImageResource(R.drawable.ic_heart_outline);
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addToFavorites(userId, "flight", f.id);
                holder.ivFavorite.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnConfirm.setOnClickListener(v -> pickDateAndBook(f));
    }

    private void pickDateAndBook(Flight flight) {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH), d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("user_id", SessionManager.getUserId(context));
            values.put("type", "flight");
            values.put("ref_id", flight.id);
            values.put("date", date);

            long id = db.insert("bookings", null, values);
            if (id > 0) {
                Toast.makeText(context, "Flight booked on " + date + "\n" + flight.from + " → " + flight.to, Toast.LENGTH_SHORT).show();
            }
        }, y, m, d);
        dp.show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void filterFlights(String from, String to, String cls) {
        List<Flight> filteredList = new ArrayList<>();
        for (Flight flight : dataFull) {
            boolean match = (from.isEmpty() || flight.from.toLowerCase().contains(from)) &&
                    (to.isEmpty() || flight.to.toLowerCase().contains(to)) &&
                    (cls.isEmpty() || flight.cls.toLowerCase().equals(cls));
            if (match) filteredList.add(flight);
        }
        data.clear();
        data.addAll(filteredList);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView ivFavorite;
        Button btnConfirm;

        public VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvFlightTitle);
            subtitle = itemView.findViewById(R.id.tvFlightSubtitle);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            btnConfirm = itemView.findViewById(R.id.btnConfirmFlight);
        }
    }
}
