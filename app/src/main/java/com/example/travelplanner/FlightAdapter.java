package com.example.travelplanner;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;
import java.util.Set;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.VH> {

    private List<Flight> data;
    private List<Flight> dataFull;
    private final Context context;
    private final TravelRepository repo;
    private final Set<String> favoriteKeys;

    public FlightAdapter(Context context, TravelRepository repo, List<Flight> data, Set<String> favoriteKeys) {
        this.context = context;
        this.repo = repo;
        this.data = (data != null) ? data : new ArrayList<>();
        this.dataFull = new ArrayList<>(this.data);
        this.favoriteKeys = favoriteKeys;
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
        FlightMeta meta = FlightMeta.of(f);
        String userId = SessionManager.getUserId(context);
        String favKey = "flight:" + f.id;

        holder.tvAirline.setText(meta.airline);
        holder.tvDepartTime.setText(meta.departTime);
        holder.tvArriveTime.setText(meta.arriveTime);
        holder.tvFrom.setText(f.from);
        holder.tvTo.setText(f.to);
        holder.tvDuration.setText(meta.durationLabel());
        holder.tvStops.setText(meta.stopsLabel());
        holder.tvClassPrice.setText(f.cls + " · $" + f.price);

        holder.ivFavorite.setImageResource(favoriteKeys.contains(favKey) ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        holder.ivFavorite.setOnClickListener(v -> {
            if (favoriteKeys.contains(favKey)) {
                repo.removeFavorite(userId, "flight", f.id, new Callback<Void>() {
                    @Override public void onSuccess(Void value) {
                        favoriteKeys.remove(favKey);
                        holder.ivFavorite.setImageResource(R.drawable.ic_heart_outline);
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                repo.addFavorite(userId, "flight", f.id, new Callback<Void>() {
                    @Override public void onSuccess(Void value) {
                        favoriteKeys.add(favKey);
                        holder.ivFavorite.setImageResource(R.drawable.ic_heart_filled);
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.btnConfirm.setOnClickListener(v -> BookingFlow.bookFlight(context, f, null));

        holder.cardRoot.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlightDetailActivity.class);
            intent.putExtra(FlightDetailActivity.EXTRA_FLIGHT_ID, f.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Flight> newData) {
        this.data = newData;
        this.dataFull = new ArrayList<>(newData);
        notifyDataSetChanged();
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

    public void sortByPrice(boolean ascending) {
        if (ascending) {
            data.sort((a, b) -> Double.compare(a.price, b.price));
        } else {
            data.sort((a, b) -> Double.compare(b.price, a.price));
        }
        notifyDataSetChanged();
    }

    public void sortByDuration() {
        data.sort((a, b) -> Integer.compare(FlightMeta.of(a).durationMin, FlightMeta.of(b).durationMin));
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        View cardRoot;
        TextView tvAirline, tvDepartTime, tvArriveTime, tvFrom, tvTo, tvDuration, tvStops, tvClassPrice;
        ImageView ivFavorite;
        Button btnConfirm;

        public VH(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardRoot);
            tvAirline = itemView.findViewById(R.id.tvAirline);
            tvDepartTime = itemView.findViewById(R.id.tvDepartTime);
            tvArriveTime = itemView.findViewById(R.id.tvArriveTime);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvStops = itemView.findViewById(R.id.tvStops);
            tvClassPrice = itemView.findViewById(R.id.tvClassPrice);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            btnConfirm = itemView.findViewById(R.id.btnConfirmFlight);
        }
    }
}
