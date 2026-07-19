package com.example.travelplanner;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class HotelDetailActivity extends AppCompatActivity {

    public static final String EXTRA_HOTEL_ID = "hotel_id";

    private static final int[] PLACEHOLDERS = {
            R.drawable.bg_placeholder_1, R.drawable.bg_placeholder_2, R.drawable.bg_placeholder_3,
            R.drawable.bg_placeholder_4, R.drawable.bg_placeholder_5, R.drawable.bg_placeholder_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        int hotelId = getIntent().getIntExtra(EXTRA_HOTEL_ID, -1);
        TravelRepository repo = Repo.travel(this);
        repo.getHotel(hotelId, new Callback<Hotel>() {
            @Override
            public void onSuccess(Hotel hotel) {
                if (hotel == null) { finish(); return; }
                repo.getCities(new Callback<List<City>>() {
                    @Override
                    public void onSuccess(List<City> cities) {
                        City city = null;
                        for (City c : cities) if (c.id == hotel.cityId) { city = c; break; }
                        render(hotel, city);
                    }

                    @Override
                    public void onError(Exception e) {
                        render(hotel, null);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(HotelDetailActivity.this, "Failed to load hotel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void render(Hotel hotel, City city) {
        HotelMeta meta = HotelMeta.of(hotel);

        findViewById(R.id.ivHero).setBackgroundResource(PLACEHOLDERS[(meta.imageVariant - 1) % PLACEHOLDERS.length]);

        ((TextView) findViewById(R.id.tvName)).setText(hotel.name);
        ((TextView) findViewById(R.id.tvCity)).setText(city != null ? city.name : "");
        ((TextView) findViewById(R.id.tvRating)).setText(String.valueOf(meta.rating));
        ((TextView) findViewById(R.id.tvReviews)).setText("(" + meta.reviewCount + " reviews)");
        ((TextView) findViewById(R.id.tvDescription)).setText(meta.description);
        ((TextView) findViewById(R.id.tvPrice)).setText("$" + hotel.price + " / night");

        LinearLayout rowAmenities = findViewById(R.id.rowAmenities);
        for (String amenity : meta.amenities) {
            rowAmenities.addView(buildAmenityView(amenity));
        }

        LinearLayout reviewsContainer = findViewById(R.id.reviewsContainer);
        List<Review> reviews = ReviewGenerator.forHotel(hotel.id);
        for (Review r : reviews) {
            reviewsContainer.addView(buildReviewView(r));
        }

        findViewById(R.id.btnBookNow).setOnClickListener(v -> BookingFlow.bookHotel(this, hotel, null));
    }

    private LinearLayout buildAmenityView(String amenity) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dp(16));
        row.setLayoutParams(lp);

        int icon = iconFor(amenity);
        if (icon != 0) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(icon);
            LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(dp(18), dp(18));
            ivLp.setMarginEnd(dp(4));
            iv.setLayoutParams(ivLp);
            row.addView(iv);
        }

        TextView tv = new TextView(this);
        tv.setText(amenity);
        tv.setTextSize(13f);
        tv.setTextColor(getColor(R.color.textPrimary));
        row.addView(tv);

        return row;
    }

    private LinearLayout buildReviewView(Review r) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(12);
        col.setLayoutParams(lp);

        TextView header = new TextView(this);
        header.setText(r.author + "  •  ★ " + r.rating + "  •  " + r.daysAgo + "d ago");
        header.setTextSize(13f);
        header.setTypeface(header.getTypeface(), android.graphics.Typeface.BOLD);
        header.setTextColor(getColor(R.color.textPrimary));
        col.addView(header);

        TextView comment = new TextView(this);
        comment.setText(r.comment);
        comment.setTextSize(13f);
        comment.setTextColor(getColor(R.color.textSecondary));
        col.addView(comment);

        return col;
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
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
