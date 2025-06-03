package com.example.elezioniapp;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DashboardLoginActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dashboardlogin_layout);

        // Recupera l'email passata tramite Intent
        email = getIntent().getStringExtra("email");

        // Mostra un Toast per confermare che l'email è stata passata
        Toast.makeText(this, "Email: " + email, Toast.LENGTH_SHORT).show();

        passwordEditText = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            checkPassword(email, password); // Fai la richiesta per verificare la password
        });
    }

    private void checkPassword(String email, String password) {
        // L'URL del file PHP per verificare l'email e la password
        String url = "http://192.168.1.48/login.php";

        // Crea la richiesta POST
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Controlla la risposta del server
                    if (response.equals("OK")) {
                        // Se la risposta è OK, procedi con l'intent verso DashboardActivity
                        Intent intent = new Intent(DashboardLoginActivity.this, DashboardActivity.class);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    } else {
                        // Se la risposta non è OK, mostra un errore
                        Toast.makeText(this, "Password errata", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Gestisci gli errori di connessione
                    Toast.makeText(this, "Errore di connessione al server!", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);  // Aggiungi l'email
                params.put("password", password);  // Aggiungi la password
                return params;
            }
        };

        // Invia la richiesta alla coda Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }
}
