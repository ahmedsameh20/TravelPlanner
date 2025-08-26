package com.example.travelplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.VH> implements Filterable {

    private List<Flight> data;
    private List<Flight> original;
    private Context context;
    private OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClicked(Flight flight);
    }

    public FlightAdapter(Context context, List<Flight> data, OnFavoriteClickListener listener) {
        this.context = context;
        this.data = (data != null) ? data : new ArrayList<>();
        this.original = new ArrayList<>(this.data);
        this.listener = listener;
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
        holder.title.setText(f.from + " → " + f.to);
        holder.subtitle.setText(f.cls + " - $" + f.price);

        holder.ivFavorite.setImageResource(f.isFavorite() ?
                R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        holder.ivFavorite.setVisibility(View.VISIBLE);

        holder.ivFavorite.setOnClickListener(v -> {
            f.setFavorite(!f.isFavorite());
            notifyItemChanged(position);
            if (listener != null) listener.onFavoriteClicked(f);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView ivFavorite;
        public VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvFlightTitle);
            subtitle = itemView.findViewById(R.id.tvFlightSubtitle);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = (constraint == null) ? "" : constraint.toString().toLowerCase().trim();
                List<Flight> filtered = new ArrayList<>();
                if (query.isEmpty()) {
                    filtered.addAll(original);
                } else {
                    for (Flight f : original) {
                        String s = f.from + " " + f.to + " " + f.cls;
                        if (s.toLowerCase().contains(query)) {
                            filtered.add(f);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filtered;
                results.count = filtered.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                data = (List<Flight>) (results.values != null ? results.values : new ArrayList<Flight>());
                notifyDataSetChanged();
            }
        };
    }
}