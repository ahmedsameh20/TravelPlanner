package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.VH> {
    public interface Listener { void onClick(City c); void onLong(City c); }
    private List<City> data; private Listener listener;
    public CityAdapter(List<City> data, Listener listener){ this.data=data; this.listener=listener; }
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle,tvSubtitle;
        public VH(View v){ super(v); tvTitle=v.findViewById(R.id.tvTitle); tvSubtitle=v.findViewById(R.id.tvSubtitle); }
    }
    @Override public VH onCreateViewHolder(ViewGroup p,int v){ return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_simple,p,false)); }
    @Override public void onBindViewHolder(VH h,int pos){ City c = data.get(pos); h.tvTitle.setText(c.name); h.tvSubtitle.setText("City ID: " + c.id); h.itemView.setOnClickListener(v->listener.onClick(c)); h.itemView.setOnLongClickListener(v->{ listener.onLong(c); return true; }); }
    @Override public int getItemCount(){ return data.size(); }
}
