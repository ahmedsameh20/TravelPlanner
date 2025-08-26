package com.example.travelplanner;

public class Flight {
    public int id;
    public String from;
    public String to;
    public String cls;
    public double price;
    private boolean favorite = false;

    public Flight(int id, String from, String to, String cls, double price) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.cls = cls;
        this.price = price;
    }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
