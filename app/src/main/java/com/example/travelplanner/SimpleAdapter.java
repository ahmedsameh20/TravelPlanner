package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter<T> extends RecyclerView.Adapter<SimpleAdapter.VH> implements Filterable {

    public interface Binder<T> {
        void bind(VH holder, T item);
        String asString(T item);
    }

    public static class VH extends RecyclerView.ViewHolder {
        public TextView title, subtitle, tv1, tv2;
        public VH(@NonNull View itemView) {
            super(itemView);
            // support both naming conventions used across project
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
            tv1 = title;
            tv2 = subtitle;
        }
    }

    private List<T> items;
    private List<T> original;
    private Binder<T> binder;

    public SimpleAdapter(List<T> items, Binder<T> binder) {
        this.items = (items != null) ? items : new ArrayList<>();
        this.original = new ArrayList<>(this.items);
        this.binder = binder;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        binder.bind(holder, items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<T> newItems) {
        this.items = (newItems != null) ? newItems : new ArrayList<>();
        this.original = new ArrayList<>(this.items);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = (constraint == null) ? "" : constraint.toString().toLowerCase().trim();
                List<T> filtered = new ArrayList<>();
                if (query.isEmpty()) {
                    filtered.addAll(original);
                } else {
                    for (T item : original) {
                        String s = binder.asString(item);
                        if (s != null && s.toLowerCase().contains(query)) filtered.add(item);
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
                items = (List<T>) (results.values != null ? results.values : new ArrayList<T>());
                notifyDataSetChanged();
            }
        };
    }
}
