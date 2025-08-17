
package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityVH> {

    public interface OnCityLongClick {
        void onLongClick(int position);
    }

    private List<City> data = new ArrayList<>();
    private OnCityLongClick longClick;

    public CityAdapter(OnCityLongClick longClick) {
        this.longClick = longClick;
    }

    public void setData(List<City> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    public City getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public CityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CityVH holder, int position) {
        City c = data.get(position);
        holder.tvName.setText(c.name);
        holder.tvCountry.setText(c.country);
        holder.tvDesc.setText(c.description);

        holder.itemView.setOnLongClickListener(v -> {
            if (longClick != null) longClick.onLongClick(holder.getAdapterPosition());
            return false; // allow contextMenu to show
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class CityVH extends RecyclerView.ViewHolder {
        TextView tvName, tvCountry, tvDesc;
        public CityVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            tvDesc = itemView.findViewById(R.id.tvDesc);
        }
    }
}
