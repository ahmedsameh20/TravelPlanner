package com.example.travelplanner;

import java.util.Locale;
import java.util.Random;

/** Deterministic, seeded-by-id "realism" schedule data for a Flight. */
public final class FlightMeta {
    public final String departTime; // "HH:mm"
    public final String arriveTime; // "HH:mm"
    public final int durationMin;
    public final String airline;
    public final int stops; // 0 or 1

    private static final String[] AIRLINES = {
            "Nile Air", "Sky Wings", "Horizon Airlines", "Blue Falcon", "Trans Egypt", "Atlas Air"
    };

    private FlightMeta(String departTime, String arriveTime, int durationMin, String airline, int stops) {
        this.departTime = departTime;
        this.arriveTime = arriveTime;
        this.durationMin = durationMin;
        this.airline = airline;
        this.stops = stops;
    }

    public static FlightMeta of(Flight flight) {
        Random r = new Random(flight.id * 6151L + 15485863L);

        int departHour = 5 + r.nextInt(17); // 5..21
        int departMin = r.nextInt(60);
        int durationMin = 60 + r.nextInt(600); // 1h - 11h
        int stops = r.nextInt(10) < 3 ? 1 : 0; // ~30% one-stop

        int totalDepartMin = departHour * 60 + departMin;
        int totalArriveMin = (totalDepartMin + durationMin) % (24 * 60);

        String departTime = String.format(Locale.ROOT, "%02d:%02d", totalDepartMin / 60, totalDepartMin % 60);
        String arriveTime = String.format(Locale.ROOT, "%02d:%02d", totalArriveMin / 60, totalArriveMin % 60);

        String airline = AIRLINES[r.nextInt(AIRLINES.length)];

        return new FlightMeta(departTime, arriveTime, durationMin, airline, stops);
    }

    public String durationLabel() {
        int h = durationMin / 60, m = durationMin % 60;
        return m > 0 ? (h + "h " + m + "m") : (h + "h");
    }

    public String stopsLabel() {
        return stops == 0 ? "Direct" : (stops + " stop");
    }
}
