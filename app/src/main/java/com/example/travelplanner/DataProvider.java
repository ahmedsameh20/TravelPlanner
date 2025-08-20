package com.example.travelplanner;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class DataProvider {

    // Fetch flights from SQLite via DBHelper
    public static List<Flight> getFlights(Context ctx) {
        DBHelper db = new DBHelper(ctx);
        return db.getAllFlights();
    }

    // Fetch hotels from SQLite via DBHelper
    public static List<Hotel> getHotels(Context ctx) {
        DBHelper db = new DBHelper(ctx);
        return db.getAllHotels();
    }

    // Simple static list of cities for Spinner/use in other fragments.
    public static List<City> getCities() {
        List<City> l = new ArrayList<>();
        l.add(new City(1, "Cairo"));
        l.add(new City(2, "Paris"));
        l.add(new City(3, "Dubai"));
        l.add(new City(4, "London"));
        l.add(new City(5, "New York"));
        l.add(new City(6, "Luxor"));
        l.add(new City(7, "Aswan"));
        return l;
    }
}
