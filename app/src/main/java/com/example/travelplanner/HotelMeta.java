package com.example.travelplanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Deterministic, seeded-by-id "realism" data for a Hotel — no DB schema changes. */
public final class HotelMeta {
    public final double rating;
    public final int reviewCount;
    public final List<String> amenities;
    public final String description;
    public final int imageVariant; // 1-6, picks a placeholder gradient
    public final String badge;     // "Popular" | "Best Price" | null

    private static final String[] AMENITY_POOL = {"WiFi", "Pool", "Breakfast", "Parking", "AC"};
    private static final String[] DESCRIPTIONS = {
            "A comfortable stay with easy access to the city's main attractions.",
            "Modern rooms and attentive service just minutes from the center.",
            "A relaxed retreat with great views and a warm welcome.",
            "Well-located and well-reviewed, popular with returning travelers.",
            "Bright, spacious rooms with everything you need for a smooth stay.",
    };

    private HotelMeta(double rating, int reviewCount, List<String> amenities, String description, int imageVariant, String badge) {
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.amenities = amenities;
        this.description = description;
        this.imageVariant = imageVariant;
        this.badge = badge;
    }

    public static HotelMeta of(Hotel hotel) {
        Random r = new Random(hotel.id * 7919L + 104729L);

        double rating = Math.round((3.5 + r.nextDouble() * 1.5) * 10) / 10.0;
        int reviewCount = 20 + r.nextInt(380);

        List<String> pool = new ArrayList<>(Arrays.asList(AMENITY_POOL));
        Collections.shuffle(pool, r);
        int amenityCount = 2 + r.nextInt(3); // 2-4
        List<String> amenities = new ArrayList<>(pool.subList(0, Math.min(amenityCount, pool.size())));

        String description = DESCRIPTIONS[r.nextInt(DESCRIPTIONS.length)];
        int imageVariant = 1 + r.nextInt(6);

        String badge = null;
        if (rating >= 4.6) badge = "Popular";
        else if (hotel.price < 130) badge = "Best Price";

        return new HotelMeta(rating, reviewCount, amenities, description, imageVariant, badge);
    }
}
