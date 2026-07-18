package com.example.travelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UsersRepo.ensureSchema(this);

        etEmail = findAnyEditText(new String[]{"etEmail","email","etMail","txtEmail","inputEmail"});
        etPassword = findAnyEditText(new String[]{"etPassword","password","pass","etPass","inputPassword"});

        Button btnLogin = findAnyButton(new String[]{"btnLogin","login","buttonLogin"});
        Button btnRegister = findAnyButton(new String[]{"btnRegister","register","buttonRegister"});

        if (btnLogin != null) btnLogin.setOnClickListener(v -> doLogin());
        if (btnRegister != null) btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        String email = safeText(etEmail);
        String pass = safeText(etPassword);
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor c = UsersRepo.login(this, email, pass);
        if (c != null) {
            int id = c.getInt(0);
            String name = c.getString(1);
            c.close();
            SessionManager.saveUser(this, id, name);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
        }
    }

    private static String safeText(EditText e){ return e==null? "": String.valueOf(e.getText()).trim(); }
    private EditText findAnyEditText(String[] names){
        for (String n: names){
            int id = getResources().getIdentifier(n, "id", getPackageName());
            if (id != 0){
                EditText et = findViewById(id);
                if (et != null) return et;
            }
        }
        return null;
    }
    private Button findAnyButton(String[] names){
        for (String n: names){
            int id = getResources().getIdentifier(n, "id", getPackageName());
            if (id != 0){
                Button b = findViewById(id);
                if (b != null) return b;
            }
        }
        return null;
    }
}