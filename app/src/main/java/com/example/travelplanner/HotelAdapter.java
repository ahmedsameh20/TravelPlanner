package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.VH> {

    public interface Listener {
        void onClick(Hotel h);
        void onToggleFavorite(Hotel h);
    }

    private List<Hotel> hotels;
    private Listener listener;

    public HotelAdapter(List<Hotel> hotels, Listener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvHotelName, tvHotelDetails;
        ImageButton btnFav;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvHotelDetails = itemView.findViewById(R.id.tvHotelDetails);
            btnFav = itemView.findViewById(R.id.btnFav);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotel, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Hotel h = hotels.get(position);

        holder.tvHotelName.setText(h.name);
        holder.tvHotelDetails.setText("Price: $" + h.price);


        holder.btnFav.setImageResource(
                h.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
        );


        holder.btnFav.setOnClickListener(v -> {
            h.setFavorite(!h.isFavorite());
            notifyItemChanged(position);
            if (listener != null) listener.onToggleFavorite(h);
        });


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(h);
        });
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }
}