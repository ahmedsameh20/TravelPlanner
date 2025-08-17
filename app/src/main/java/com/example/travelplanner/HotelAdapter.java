
package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelVH> {

    private List<Hotel> data = new ArrayList<>();

    public void setData(List<Hotel> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HotelVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new HotelVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelVH holder, int position) {
        Hotel h = data.get(position);
        holder.tv1.setText(h.name);
        holder.tv2.setText(String.valueOf(h.price) + " per night");
        holder.itemView.setOnClickListener(v -> Toast.makeText(v.getContext(), "Book: " + h.name, Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class HotelVH extends RecyclerView.ViewHolder {
        TextView tv1, tv2;
        public HotelVH(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(android.R.id.text1);
            tv2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
