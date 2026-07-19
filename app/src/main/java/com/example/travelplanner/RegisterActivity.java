package com.example.travelplanner;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Repo.auth().ensureSchema(this);

        etName = findViewById(getIdOrAlt(new String[]{"etName","name","username","fullName","et_full_name"}));
        etEmail = findViewById(getIdOrAlt(new String[]{"etEmail","email","etMail","txtEmail","inputEmail"}));
        etPassword = findViewById(getIdOrAlt(new String[]{"etPassword","password","pass","etPass","inputPassword"}));

        Button btn = findViewById(getIdOrAlt(new String[]{"btnCreate","btnSave","btnRegister","register","btnSignup","signUp","buttonRegister"}));
        if (btn != null) {
            btn.setOnClickListener(v -> doRegister());
        }
    }

    private void doRegister(){
        String name = safeText(etName);
        String email = safeText(etEmail);
        String pass = safeText(etPassword);

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        Repo.auth().register(this, name, email, pass, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(String userId, String resultName) {
                Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String safeText(EditText e){ return e==null? "": String.valueOf(e.getText()).trim(); }
    private int getIdOrAlt(String[] names){
        for (String n: names){
            int id = getResources().getIdentifier(n,"id", getPackageName());
            if (id != 0) return id;
        }
        return 0;
    }
}