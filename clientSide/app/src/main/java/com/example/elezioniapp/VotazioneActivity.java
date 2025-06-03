package com.example.elezioniapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VotazioneActivity extends AppCompatActivity {

    private LinearLayout candidatiContainer;
    private Button buttonSendToken;
    private ArrayList<CheckBox> checkBoxList = new ArrayList<>();
    private final int MAX_SELECTION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.votazione_layout);

        candidatiContainer = findViewById(R.id.candidatiContainer);
        buttonSendToken = findViewById(R.id.buttonSendToken);
        buttonSendToken.setEnabled(false);

        caricaCandidati();
    }

    private void caricaCandidati() {
        String url = "https://TUO_DOMINIO/richiedi_candidati.php"; // Cambia con il tuo URL

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject candidato = response.getJSONObject(i);
                                String nome = candidato.getString("nome");
                                String cognome = candidato.getString("cognome");
                                String classe = candidato.getString("classe");

                                String displayText = nome + " " + cognome + " (" + classe + ")";

                                CheckBox checkBox = new CheckBox(VotazioneActivity.this);
                                checkBox.setText(displayText);
                                checkBox.setTextSize(18);
                                checkBox.setTextColor(getResources().getColor(android.R.color.black));
                                checkBox.setPadding(10, 20, 10, 20);

                                candidatiContainer.addView(checkBox);
                                checkBoxList.add(checkBox);

                                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> gestisciSelezione());
                            }
                        } catch (JSONException e) {
                            Toast.makeText(VotazioneActivity.this, "Errore parsing", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VotazioneActivity.this, "Errore rete", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void gestisciSelezione() {
        int selezionati = 0;

        for (CheckBox cb : checkBoxList) {
            if (cb.isChecked()) selezionati++;
        }

        // Disabilita gli altri se 2 sono già selezionati
        for (CheckBox cb : checkBoxList) {
            if (!cb.isChecked()) {
                cb.setEnabled(selezionati < MAX_SELECTION);
            }
        }

        // Attiva il bottone solo se esattamente 2 selezionati
        buttonSendToken.setEnabled(selezionati == MAX_SELECTION);
    }
}
