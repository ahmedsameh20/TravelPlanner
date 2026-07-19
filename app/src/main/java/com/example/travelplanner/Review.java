package com.example.travelplanner;

public class Review {
    public final String author;
    public final double rating;
    public final String comment;
    public final int daysAgo;

    public Review(String author, double rating, String comment, int daysAgo) {
        this.author = author;
        this.rating = rating;
        this.comment = comment;
        this.daysAgo = daysAgo;
    }
}
