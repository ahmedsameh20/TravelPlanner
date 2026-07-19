package com.example.travelplanner;

import android.content.Context;

/**
 * Single access point for the app's data layer. Swapping local SQLite for a
 * real backend (e.g. Firestore/Firebase Auth) later is a one-line change
 * here, not a rewrite of every screen.
 */
public final class Repo {
    private static volatile TravelRepository travelRepo;
    private static volatile AuthRepository authRepo;

    private Repo() {}

    public static TravelRepository travel(Context ctx) {
        if (travelRepo == null) {
            synchronized (Repo.class) {
                if (travelRepo == null) travelRepo = new FirestoreTravelRepository();
            }
        }
        return travelRepo;
    }

    public static AuthRepository auth() {
        if (authRepo == null) {
            synchronized (Repo.class) {
                if (authRepo == null) authRepo = new FirebaseAuthRepository();
            }
        }
        return authRepo;
    }
}
