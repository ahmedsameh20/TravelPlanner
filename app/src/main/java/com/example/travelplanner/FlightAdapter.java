package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.VH> {
    public interface Listener { void onClick(Flight f); void onLong(Flight f); }
    private List<Flight> data; private Listener listener;
    public FlightAdapter(List<Flight> data, Listener listener){ this.data=data; this.listener=listener; }
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle,tvSubtitle;
        public VH(View v){ super(v); tvTitle=v.findViewById(R.id.tvTitle); tvSubtitle=v.findViewById(R.id.tvSubtitle); }
    }
    @Override public VH onCreateViewHolder(ViewGroup p,int v){ return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_simple,p,false)); }
    @Override public void onBindViewHolder(VH h,int pos){ Flight it = data.get(pos); h.tvTitle.setText(it.from + " → " + it.to); h.tvSubtitle.setText(it.cls + " - $" + it.price); h.itemView.setOnClickListener(v->listener.onClick(it)); h.itemView.setOnLongClickListener(v->{ listener.onLong(it); return true; }); }
    @Override public int getItemCount(){ return data.size(); }
}
