package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.VH> {
    public interface Listener { void onClick(Hotel h); void onLong(Hotel h); }
    private List<Hotel> data; private Listener listener;
    public HotelAdapter(List<Hotel> data, Listener listener){ this.data=data; this.listener=listener; }
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle,tvSubtitle;
        public VH(View v){ super(v); tvTitle=v.findViewById(R.id.tvTitle); tvSubtitle=v.findViewById(R.id.tvSubtitle); }
    }
    @Override public VH onCreateViewHolder(ViewGroup p,int v){ return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_simple,p,false)); }
    @Override public void onBindViewHolder(VH h,int pos){ Hotel it = data.get(pos); h.tvTitle.setText(it.name); h.tvSubtitle.setText("$" + it.price); h.itemView.setOnClickListener(v->listener.onClick(it)); h.itemView.setOnLongClickListener(v->{ listener.onLong(it); return true; }); }
    @Override public int getItemCount(){ return data.size(); }
}
