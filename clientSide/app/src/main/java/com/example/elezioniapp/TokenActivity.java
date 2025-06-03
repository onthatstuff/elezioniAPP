package com.example.elezioniapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class TokenActivity extends AppCompatActivity {

    private EditText tokenEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.token_layout);

        tokenEditText = findViewById(R.id.textInputEditText);
        Button sendTokenButton = findViewById(R.id.buttonSendToken);

        sendTokenButton.setOnClickListener(v -> {
            String token = tokenEditText.getText().toString().trim();

            if (!token.isEmpty()) {
                verifyToken(token);
            } else {
                Toast.makeText(TokenActivity.this, "Inserisci il token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyToken(String token) {
        String url = "http://192.168.1.48/verify_token.php";  // Sostituisci con il tuo IP/server

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equalsIgnoreCase("OK")) {
                        Intent intent = new Intent(TokenActivity.this, VotazioneActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(TokenActivity.this, "Token non valido", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(TokenActivity.this, "Errore nella connessione", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
