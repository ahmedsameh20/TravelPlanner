package com.example.travelplanner;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseAuthRepository implements AuthRepository {

    private FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    @Override
    public void ensureSchema(Context ctx) {
        // no-op: Firebase Auth needs no local schema setup
    }

    @Override
    public void register(Context ctx, String name, String email, String password, AuthCallback cb) {
        auth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) { cb.onError("Registration failed"); return; }
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    user.updateProfile(profile)
                            .addOnCompleteListener(t -> cb.onSuccess(user.getUid(), name));
                })
                .addOnFailureListener(e -> cb.onError(friendlyMessage(e)));
    }

    @Override
    public void login(Context ctx, String email, String password, AuthCallback cb) {
        auth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) { cb.onError("Invalid login"); return; }
                    String name = user.getDisplayName();
                    cb.onSuccess(user.getUid(), name == null ? "" : name);
                })
                .addOnFailureListener(e -> cb.onError(friendlyMessage(e)));
    }

    private String friendlyMessage(Exception e) {
        String m = e.getMessage();
        return m == null ? "Authentication failed" : m;
    }
}
