package com.example.travelplanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.VH> {

    private static final int[] PLACEHOLDERS = {
            R.drawable.bg_placeholder_1, R.drawable.bg_placeholder_2, R.drawable.bg_placeholder_3,
            R.drawable.bg_placeholder_4, R.drawable.bg_placeholder_5, R.drawable.bg_placeholder_6
    };

    private List<Hotel> hotels;
    private final Context context;
    private final TravelRepository repo;

    public HotelAdapter(Context context, TravelRepository repo, List<Hotel> hotels) {
        this.context = context;
        this.repo = repo;
        this.hotels = (hotels != null) ? hotels : new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Hotel h = hotels.get(position);
        HotelMeta meta = HotelMeta.of(h);
        int userId = SessionManager.getUserId(context);

        holder.tvHotelName.setText(h.name);
        holder.tvHotelDetails.setText("$" + h.price + " / night");
        holder.tvRating.setText(String.valueOf(meta.rating));
        holder.tvReviews.setText("(" + meta.reviewCount + " reviews)");
        holder.ivHotelImage.setBackgroundResource(PLACEHOLDERS[(meta.imageVariant - 1) % PLACEHOLDERS.length]);

        if (meta.badge != null) {
            holder.tvBadge.setVisibility(View.VISIBLE);
            holder.tvBadge.setText(meta.badge);
            if ("Best Price".equals(meta.badge)) {
                holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_best_price);
                holder.tvBadge.setTextColor(context.getColor(R.color.badgeBestPriceText));
            } else {
                holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_popular);
                holder.tvBadge.setTextColor(context.getColor(R.color.badgePopularText));
            }
        } else {
            holder.tvBadge.setVisibility(View.GONE);
        }

        holder.rowAmenities.removeAllViews();
        for (String amenity : meta.amenities) {
            holder.rowAmenities.addView(buildAmenityChip(amenity));
        }

        boolean fav = repo.isFavorite(userId, h.id, "hotel");
        holder.btnFav.setImageResource(fav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        holder.btnFav.setOnClickListener(v -> {
            if (repo.isFavorite(userId, h.id, "hotel")) {
                repo.removeFavorite(userId, "hotel", h.id);
                holder.btnFav.setImageResource(R.drawable.ic_heart_outline);
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                repo.addFavorite(userId, "hotel", h.id);
                holder.btnFav.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnConfirm.setOnClickListener(v -> BookingFlow.bookHotel(context, h, null));

        holder.cardRoot.setOnClickListener(v -> {
            Intent intent = new Intent(context, HotelDetailActivity.class);
            intent.putExtra(HotelDetailActivity.EXTRA_HOTEL_ID, h.id);
            context.startActivity(intent);
        });
    }

    private View buildAmenityChip(String amenity) {
        TextView tv = new TextView(context);
        tv.setText(amenity);
        tv.setTextSize(11f);
        tv.setTextColor(context.getColor(R.color.textSecondary));
        tv.setPadding(dp(8), dp(3), dp(8), dp(3));
        tv.setBackgroundResource(R.drawable.bg_chip_grey);
        int icon = iconFor(amenity);
        if (icon != 0) {
            tv.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
            tv.setCompoundDrawablePadding(dp(4));
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dp(6));
        tv.setLayoutParams(lp);
        return tv;
    }

    private int iconFor(String amenity) {
        switch (amenity) {
            case "WiFi": return R.drawable.ic_wifi;
            case "Pool": return R.drawable.ic_pool;
            case "Breakfast": return R.drawable.ic_breakfast;
            default: return 0;
        }
    }

    private int dp(int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public void updateData(List<Hotel> newHotels) {
        this.hotels = newHotels;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        View cardRoot, ivHotelImage;
        TextView tvHotelName, tvHotelDetails, tvRating, tvReviews, tvBadge;
        LinearLayout rowAmenities;
        ImageButton btnFav;
        Button btnConfirm;

        public VH(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardRoot);
            ivHotelImage = itemView.findViewById(R.id.ivHotelImage);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvHotelDetails = itemView.findViewById(R.id.tvHotelDetails);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviews = itemView.findViewById(R.id.tvReviews);
            tvBadge = itemView.findViewById(R.id.tvBadge);
            rowAmenities = itemView.findViewById(R.id.rowAmenities);
            btnFav = itemView.findViewById(R.id.btnFav);
            btnConfirm = itemView.findViewById(R.id.btnConfirmBooking);
        }
    }
}
