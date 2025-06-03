<?php
// Includi il file di connessione al database
require 'db_connection.php';  // Assicurati che il file si chiami così e sia nella stessa cartella

$email = $_POST['email'] ?? '';
$email = trim($email); // Togli eventuali spazi accidentali

if (filter_var($email, FILTER_VALIDATE_EMAIL)) { 
    if (str_ends_with($email, '@isistassinari.edu.it')) {

        // Controlla se l'email esiste nella tabella Studenti
        $stmt = $pdo->prepare("SELECT COUNT(*) FROM studente WHERE Mail = :email");
        $stmt->execute(['email' => $email]);
        $countStudenti = $stmt->fetchColumn();

        if ($countStudenti > 0) {
            // Email trovata tra gli studenti
            // Prima esegui send_token.php
              // Includi ed esegui send_token.php

            // Ora invia la risposta al client
            echo "OK_STUDENT";  // Risposta al client
            require 'send_token.php';
        } else {
            // Se non trovata tra gli studenti, controlla tra gli admin
            $stmt = $pdo->prepare("SELECT COUNT(*) FROM admin WHERE Email = :email");
            $stmt->execute(['email' => $email]);
            $countAdmin = $stmt->fetchColumn();

            if ($countAdmin > 0) {
                echo "OK_ADMIN";  // Email trovata tra gli admin
            } else {
                echo "EMAIL_NOT_FOUND";  // Email non presente in nessuna delle due tabelle
            }
        }

    } else {
        echo "INVALID_DOMAIN"; 
    }
} else {
    echo "INVALID_FORMAT";  
}
?>
