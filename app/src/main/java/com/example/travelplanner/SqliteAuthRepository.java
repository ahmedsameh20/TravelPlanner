package com.example.travelplanner;

import android.content.Context;
import android.database.Cursor;

public class SqliteAuthRepository implements AuthRepository {

    @Override
    public void ensureSchema(Context ctx) {
        UsersRepo.ensureSchema(ctx);
    }

    @Override
    public long register(Context ctx, String name, String email, String password) {
        return UsersRepo.register(ctx, name, email, password);
    }

    @Override
    public Cursor login(Context ctx, String email, String password) {
        return UsersRepo.login(ctx, email, password);
    }

    @Override
    public String lastError() {
        return UsersRepo.lastError;
    }
}
