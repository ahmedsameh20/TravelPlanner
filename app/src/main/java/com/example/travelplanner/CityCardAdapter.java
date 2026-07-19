package com.example.travelplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CityCardAdapter extends RecyclerView.Adapter<CityCardAdapter.VH> {

    public interface Listener {
        void onCityClick(City city);
    }

    private static final int[] PLACEHOLDERS = {
            R.drawable.bg_placeholder_1, R.drawable.bg_placeholder_2, R.drawable.bg_placeholder_3,
            R.drawable.bg_placeholder_4, R.drawable.bg_placeholder_5, R.drawable.bg_placeholder_6
    };

    private final Context context;
    private final TravelRepository repo;
    private List<City> cities;
    private final Listener listener;

    public CityCardAdapter(Context context, TravelRepository repo, List<City> cities, Listener listener) {
        this.context = context;
        this.repo = repo;
        this.cities = (cities != null) ? cities : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        City city = cities.get(position);
        int userId = SessionManager.getUserId(context);

        holder.tvName.setText(city.name);
        holder.image.setBackgroundResource(PLACEHOLDERS[Math.abs(city.id) % PLACEHOLDERS.length]);

        boolean fav = repo.isFavorite(userId, city.id, "city");
        holder.btnFav.setImageResource(fav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        holder.btnFav.setOnClickListener(v -> {
            if (repo.isFavorite(userId, city.id, "city")) {
                repo.removeFavorite(userId, "city", city.id);
                holder.btnFav.setImageResource(R.drawable.ic_heart_outline);
                Toast.makeText(context, "Removed " + city.name + " from favorites", Toast.LENGTH_SHORT).show();
            } else {
                repo.addFavorite(userId, "city", city.id);
                holder.btnFav.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(context, "Added " + city.name + " to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCityClick(city);
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public void updateData(List<City> newCities) {
        this.cities = (newCities != null) ? newCities : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        View image;
        TextView tvName;
        ImageButton btnFav;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivCityImage);
            tvName = itemView.findViewById(R.id.tvCityName);
            btnFav = itemView.findViewById(R.id.btnFav);
        }
    }
}
