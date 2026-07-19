package com.example.travelplanner;

import android.content.Context;
import android.database.Cursor;

/**
 * Contract for registration/login. {@link SqliteAuthRepository} wraps the
 * existing {@link UsersRepo}; a future Firebase Auth implementation can drop
 * in behind {@link Repo#auth} without touching Login/Register activities.
 */
public interface AuthRepository {
    long DUPLICATE_EMAIL = -2L;
    long DB_ERROR = -3L;
    long INVALID_INPUT = -4L;

    void ensureSchema(Context ctx);

    /** Returns rowId (&gt;0) on success; DUPLICATE_EMAIL / DB_ERROR / INVALID_INPUT otherwise. */
    long register(Context ctx, String name, String email, String password);

    /** Returns a Cursor at (id, name) on success (caller must close), null otherwise. */
    Cursor login(Context ctx, String email, String password);

    String lastError();
}
