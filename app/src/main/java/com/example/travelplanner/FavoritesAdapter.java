package com.example.travelplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.VH> {

    public interface Listener {
        void onRemove(FavoriteItem item);
        void onClick(FavoriteItem item);
    }

    private static final int[] PLACEHOLDERS = {
            R.drawable.bg_placeholder_1, R.drawable.bg_placeholder_2, R.drawable.bg_placeholder_3,
            R.drawable.bg_placeholder_4, R.drawable.bg_placeholder_5, R.drawable.bg_placeholder_6
    };

    private final Context context;
    private List<FavoriteItem> items;
    private final Listener listener;

    public FavoritesAdapter(Context context, List<FavoriteItem> items, Listener listener) {
        this.context = context;
        this.items = (items != null) ? items : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FavoriteItem item = items.get(position);
        holder.tvTitle.setText(item.title == null || item.title.isEmpty() ? ("#" + item.refId) : item.title);
        holder.tvType.setText(capitalize(item.type));
        holder.thumb.setBackgroundResource(PLACEHOLDERS[Math.abs(item.refId) % PLACEHOLDERS.length]);

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(item);
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<FavoriteItem> newItems) {
        this.items = (newItems != null) ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        View thumb;
        TextView tvTitle, tvType;
        ImageButton btnRemove;

        VH(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.ivThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
