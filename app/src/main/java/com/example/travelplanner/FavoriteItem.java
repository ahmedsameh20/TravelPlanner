package com.example.travelplanner;

public class FavoriteItem {
    public final int favId;
    public final String type;   // "city" | "hotel" | "flight"
    public final int refId;
    public final String title;

    public FavoriteItem(int favId, String type, int refId, String title) {
        this.favId = favId;
        this.type = type;
        this.refId = refId;
        this.title = title;
    }
}
