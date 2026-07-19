package com.example.travelplanner;

/** Tiny in-memory bridge for cross-fragment navigation intents (e.g. "show Hotels filtered by this city"). */
public final class NavState {
    private static Integer pendingCityId;

    private NavState() {}

    public static void requestCityFilter(int cityId) {
        pendingCityId = cityId;
    }

    public static Integer consumePendingCityFilter() {
        Integer v = pendingCityId;
        pendingCityId = null;
        return v;
    }
}
