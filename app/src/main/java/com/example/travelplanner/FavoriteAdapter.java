
package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavVH> {

    public interface OnFavAction {
        void onDelete(long favId);
    }

    private List<String> items = new ArrayList<>();
    private List<Long> ids = new ArrayList<>();
    private OnFavAction action;

    public FavoriteAdapter(OnFavAction action) {
        this.action = action;
    }

    public void setData(List<String> data, List<Long> ids) {
        this.items = data;
        this.ids = ids;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavVH holder, int position) {
        holder.tvText.setText(items.get(position));
        holder.btnDelete.setOnClickListener(v -> {
            if (action != null) action.onDelete(ids.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FavVH extends RecyclerView.ViewHolder {
        TextView tvText;
        ImageButton btnDelete;
        public FavVH(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvFavText);
            btnDelete = itemView.findViewById(R.id.btnFavDelete);
        }
    }
}
