<?php
header('Content-Type: application/json');

// Attiva error reporting per debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Connessione al database
require 'db_connection.php';

try {
    // Prepara ed esegue la query
    $stmt = $pdo->prepare("SELECT nome, cognome, classe FROM candidato");
    $stmt->execute();

    // Recupera tutti i risultati
    $candidati = $stmt->fetchAll(PDO::FETCH_ASSOC);

    if (!$candidati) {
        // Nessun candidato trovato, ritorna array vuoto
        echo json_encode([]);
    } else {
        // Restituisce i dati in formato JSON
        echo json_encode($candidati);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "error" => "Errore del server: " . $e->getMessage()
    ]);
}
?>
