package com.example.travelplanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Deterministic, seeded-by-id synthetic reviews — no DB table needed. */
public final class ReviewGenerator {
    private ReviewGenerator() {}

    private static final String[] AUTHORS = {
            "Sara M.", "Ahmed K.", "Laura B.", "Omar T.", "Nadia F.", "James R.",
            "Yasmin A.", "Karim S.", "Emily W.", "Youssef H.", "Maria P.", "David L."
    };

    private static final String[] COMMENTS_POSITIVE = {
            "Great location and very clean rooms. Would stay again.",
            "Staff were incredibly helpful and the breakfast was excellent.",
            "Exactly as described, comfortable bed and quiet at night.",
            "Loved the view from our room, checked in without any hassle.",
            "Good value for the price, close to everything we wanted to see.",
    };
    private static final String[] COMMENTS_MIXED = {
            "Nice stay overall, though the WiFi was a bit slow.",
            "Room was smaller than expected but the service made up for it.",
            "Decent hotel, a little noisy from the street in the evening.",
    };

    public static List<Review> forHotel(int hotelId) {
        Random r = new Random(hotelId * 92821L + 49297L);
        int count = 3 + r.nextInt(3); // 3-5
        List<Review> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String author = AUTHORS[r.nextInt(AUTHORS.length)];
            boolean positive = r.nextInt(10) < 8; // mostly positive
            String comment = positive
                    ? COMMENTS_POSITIVE[r.nextInt(COMMENTS_POSITIVE.length)]
                    : COMMENTS_MIXED[r.nextInt(COMMENTS_MIXED.length)];
            double rating = positive ? (4.0 + r.nextInt(11) / 10.0) : (3.0 + r.nextInt(11) / 10.0);
            rating = Math.min(5.0, Math.round(rating * 10) / 10.0);
            int daysAgo = 1 + r.nextInt(180);
            out.add(new Review(author, rating, comment, daysAgo));
        }
        return out;
    }
}
