
package com.example.travelplanner;

public class Hotel {
    public long id;
    public long cityId;
    public String name;
    public double price;

    public Hotel(long id, long cityId, String name, double price) {
        this.id = id;
        this.cityId = cityId;
        this.name = name;
        this.price = price;
    }
}
