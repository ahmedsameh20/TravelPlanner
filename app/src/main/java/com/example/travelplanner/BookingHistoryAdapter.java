package com.example.travelplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.VH> {

    public interface Listener {
        void onConfirm(Booking b);
        void onCancel(Booking b);
        void onDelete(Booking b);
    }

    private final SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private final Listener listener;
    private List<Booking> data = new ArrayList<>();

    public BookingHistoryAdapter(List<Booking> initial, Listener l) {
        if (initial != null) data = initial;
        this.listener = l;
    }

    public void replaceData(List<Booking> list) {
        data = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Booking b = data.get(position);

        h.tvRef.setText("Booking #" + b.id);
        h.tvDetails.setText(b.details != null ? b.details : "");
        h.tvDate.setText((b.dateMs > 0) ? df.format(new Date(b.dateMs)) : "");

        if (b.isCancelled) {
            h.tvStatus.setText("Cancelled");
            h.tvStatus.setBackgroundResource(R.drawable.bg_chip_red);
        } else if (b.isConfirmed) {
            h.tvStatus.setText("Confirmed");
            h.tvStatus.setBackgroundResource(R.drawable.bg_chip_green);
        } else {
            h.tvStatus.setText("Pending");
            h.tvStatus.setBackgroundResource(R.drawable.bg_chip_grey);
        }

        h.btnConfirm.setVisibility(b.isCancelled || b.isConfirmed ? View.GONE : View.VISIBLE);
        h.btnCancel.setVisibility(b.isCancelled ? View.GONE : View.VISIBLE);

        h.btnConfirm.setOnClickListener(v -> { if (listener != null) listener.onConfirm(b); });
        h.btnCancel.setOnClickListener(v -> { if (listener != null) listener.onCancel(b); });
        h.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(b); });
    }

    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvRef, tvDetails, tvDate, tvStatus;
        Button btnConfirm, btnCancel, btnDelete;
        VH(@NonNull View itemView) {
            super(itemView);
            tvRef = itemView.findViewById(R.id.tvRef);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
