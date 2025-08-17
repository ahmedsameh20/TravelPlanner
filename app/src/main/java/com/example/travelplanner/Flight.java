
package com.example.travelplanner;

public class Flight {
    public long id;
    public String from;
    public String to;
    public String cls;
    public double price;

    public Flight(long id, String from, String to, String cls, double price) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.cls = cls;
        this.price = price;
    }
}
