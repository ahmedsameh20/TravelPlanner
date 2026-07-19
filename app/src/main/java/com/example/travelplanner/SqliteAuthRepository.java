package com.example.travelplanner;

import android.content.Context;
import android.database.Cursor;

public class SqliteAuthRepository implements AuthRepository {

    @Override
    public void ensureSchema(Context ctx) {
        UsersRepo.ensureSchema(ctx);
    }

    @Override
    public void register(Context ctx, String name, String email, String password, AuthCallback cb) {
        long res = UsersRepo.register(ctx, name, email, password);
        if (res > 0) {
            cb.onSuccess(String.valueOf(res), name);
            return;
        }
        String msg;
        if (res == UsersRepo.DUPLICATE_EMAIL) msg = "Email already exists.";
        else if (res == UsersRepo.INVALID_INPUT) msg = "Email & Password required.";
        else msg = "DB error: " + UsersRepo.lastError;
        cb.onError(msg);
    }

    @Override
    public void login(Context ctx, String email, String password, AuthCallback cb) {
        Cursor c = UsersRepo.login(ctx, email, password);
        if (c != null) {
            String id = String.valueOf(c.getInt(0));
            String name = c.getString(1);
            c.close();
            cb.onSuccess(id, name);
        } else {
            cb.onError("Invalid login");
        }
    }
}
