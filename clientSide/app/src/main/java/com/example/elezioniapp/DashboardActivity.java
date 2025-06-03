package com.example.elezioniapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private LinearLayout layoutStudenti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dashboard_layout);

        layoutStudenti = findViewById(R.id.layoutStudenti);
        ImageView imageAdd = findViewById(R.id.imageAdd);
        ImageView imageDelete = findViewById(R.id.imageDelete);

        imageAdd.setOnClickListener(v -> showAddStudentDialog());
        imageDelete.setOnClickListener(v -> eliminaStudentiSelezionati());

        caricaCandidatiDaServer();
    }

    private void showAddStudentDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_add_student, null);

        EditText editNome = view.findViewById(R.id.editNome);
        EditText editCognome = view.findViewById(R.id.editCognome);
        EditText editClasse = view.findViewById(R.id.editClasse);
        Button btnInvia = view.findViewById(R.id.btnInvia);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnInvia.setOnClickListener(v -> {
            String nome = editNome.getText().toString().trim();
            String cognome = editCognome.getText().toString().trim();
            String classe = editClasse.getText().toString().trim();

            if (nome.isEmpty() || cognome.isEmpty() || classe.isEmpty()) {
                Toast.makeText(this, "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            } else {
                sendStudentData(nome, cognome, classe, dialog);
            }
        });

        dialog.show();
    }

    private void sendStudentData(String nome, String cognome, String classe, AlertDialog dialog) {
        String url = "http://192.168.1.48/add_candidato.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Studente aggiunto", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    caricaCandidatiDaServer();
                },
                error -> {
                    String msg = error.getMessage();
                    if (msg == null && error.networkResponse != null) {
                        msg = new String(error.networkResponse.data);
                    }
                    Toast.makeText(this, "Errore aggiunta: " + msg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nome", nome);
                params.put("cognome", cognome);
                params.put("classe", classe);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void aggiungiCandidatoAllaLista(String nome, String cognome, String classe) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View studenteView = inflater.inflate(R.layout.item_studente, layoutStudenti, false);

        TextView textStudente = studenteView.findViewById(R.id.textStudente);
        CheckBox checkbox = studenteView.findViewById(R.id.checkboxStudente);

        textStudente.setText("👤 " + nome + " " + cognome + " - Classe: " + classe);

        layoutStudenti.addView(studenteView);
    }

    private void eliminaStudentiSelezionati() {
        int count = layoutStudenti.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View item = layoutStudenti.getChildAt(i);
            CheckBox checkBox = item.findViewById(R.id.checkboxStudente);
            TextView textStudente = item.findViewById(R.id.textStudente);

            if (checkBox != null && checkBox.isChecked() && textStudente != null) {
                String testo = textStudente.getText().toString().replace("👤 ", "");
                String[] parti = testo.split(" - Classe: ");
                String[] nomeCognome = parti[0].split(" ");
                String nome = nomeCognome[0];
                String cognome = nomeCognome[1];
                String classe = parti[1];

                rimuoviStudenteDalDatabase(nome, cognome, classe);
            }
        }

        // Ricarica la lista da server dopo eliminazioni
        caricaCandidatiDaServer();
    }

    private void rimuoviStudenteDalDatabase(String nome, String cognome, String classe) {
        String url = "http://192.168.1.48/remove_candidato.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Studente eliminato", Toast.LENGTH_SHORT).show(),
                error -> {
                    String msg = error.getMessage();
                    if (msg == null && error.networkResponse != null) {
                        msg = new String(error.networkResponse.data);
                    }
                    Toast.makeText(this, "Errore eliminazione: " + msg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nome", nome);
                params.put("cognome", cognome);
                params.put("classe", classe);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void caricaCandidatiDaServer() {
        String url = "http://192.168.1.48/richiedi_candidati.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("DashboardActivity", "Raw response: " + response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        runOnUiThread(() -> {
                            layoutStudenti.removeAllViews();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject candidato = jsonArray.getJSONObject(i);
                                    String nome = candidato.getString("nome");
                                    String cognome = candidato.getString("cognome");
                                    String classe = candidato.getString("classe");

                                    aggiungiCandidatoAllaLista(nome, cognome, classe);
                                } catch (JSONException e) {
                                    Toast.makeText(this, "Errore parsing candidato: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(this, "Errore parsing dati: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String msg = error.getMessage();
                    if (msg == null && error.networkResponse != null) {
                        msg = new String(error.networkResponse.data);
                    }
                    Toast.makeText(this, "Errore caricamento dati: " + msg, Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
