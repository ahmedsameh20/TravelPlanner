package com.example.travelplanner;

/** Generic async result callback used by {@link TravelRepository} and {@link AuthRepository}. */
public interface Callback<T> {
    void onSuccess(T value);

    default void onError(Exception e) { }
}
