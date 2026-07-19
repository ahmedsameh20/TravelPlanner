package com.example.travelplanner;

import android.content.Context;

/**
 * Contract for registration/login. {@link SqliteAuthRepository} wraps the
 * existing {@link UsersRepo}; {@link FirebaseAuthRepository} wraps Firebase
 * Auth. Both sit behind {@link Repo#auth}.
 */
public interface AuthRepository {
    void ensureSchema(Context ctx);

    void register(Context ctx, String name, String email, String password, AuthCallback cb);

    void login(Context ctx, String email, String password, AuthCallback cb);

    interface AuthCallback {
        void onSuccess(String userId, String name);
        void onError(String message);
    }
}
