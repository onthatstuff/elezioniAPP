package com.example.elezioniapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Abilita Edge-to-Edge per gestire gli inset di sistema
        EdgeToEdge.enable(this);

        setContentView(R.layout.login_activity);

        // Imposta il listener per applicare il padding necessario per gli inset di sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.editTextEmailAddress);
        Button button = findViewById(R.id.buttonSendToken);

        button.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Validazione lato client: dominio email
            if (!email.endsWith("@isistassinari.edu.it")) {
                Toast.makeText(this, "Usa una email del dominio isistassinari.edu.it", Toast.LENGTH_SHORT).show();
                return;
            }

            checkEmailOnServer(email);
        });
    }

    private void checkEmailOnServer(String email) {
        String url = "http://192.168.1.48/check_email.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response == null || response.trim().isEmpty()) {
                        Toast.makeText(this, "Risposta vuota dal server", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String cleanResponse = response.trim();
                    android.util.Log.d("SERVER_RESPONSE", "Risposta server: [" + cleanResponse + "]");
                    switch (cleanResponse) {
                        case "OK_STUDENT":
                            startActivity(new Intent(LoginActivity.this, TokenActivity.class));
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            finish();
                            break;

                        case "OK_ADMIN":
                            Intent intentAdmin = new Intent(LoginActivity.this, DashboardLoginActivity.class);
                            intentAdmin.putExtra("email", email);
                            startActivity(intentAdmin);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            break;

                        case "INVALID_DOMAIN":
                            Toast.makeText(this, "Dominio email non valido", Toast.LENGTH_SHORT).show();
                            break;

                        case "INVALID_FORMAT":
                            Toast.makeText(this, "Formato email errato", Toast.LENGTH_SHORT).show();
                            break;

                        case "EMAIL_NOT_FOUND":
                            Toast.makeText(this, "Email non trovata", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(this, "Risposta sconosciuta: " + cleanResponse, Toast.LENGTH_SHORT).show();
                            break;
                    }
                },
                error -> Toast.makeText(this, "Errore di connessione al server!", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }
}
