package com.example.travelplanner;

public class Hotel {
    public int id;
    public int cityId;
    public String name;
    public double price;
    private boolean favorite = false;

    public Hotel(int id, int cityId, String name, double price) {
        this.id = id;
        this.cityId = cityId;
        this.name = name;
        this.price = price;
    }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean fav) { this.favorite = fav; }

    @Override
    public String toString() {
        return name + " - $" + price;
    }
}
