<?php
header('Content-Type: application/json');

// Connessione al database
require 'db_connection.php';

$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
];

try {
    $pdo = new PDO($dsn, $user, $pass, $options);
} catch (PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Errore connessione: ' . $e->getMessage()]);
    exit;
}

// Recupera parametri POST
$matricola = $_POST['matricola'] ?? null;
$voto1 = $_POST['voto1'] ?? null;
$voto2 = $_POST['voto2'] ?? null;

// Validazione iniziale
if (!$matricola || !$voto1 || !$voto2 || $voto1 == $voto2) {
    echo json_encode(['success' => false, 'message' => 'Dati non validi']);
    exit;
}

try {
    // Verifica se lo studente ha già votato
    $stmt = $pdo->prepare("SELECT voto FROM studente WHERE matricola = ?");
    $stmt->execute([$matricola]);
    $studente = $stmt->fetch();

    if (!$studente) {
        echo json_encode(['success' => false, 'message' => 'Studente non trovato']);
        exit;
    }

    if ($studente['voto'] == 1) {
        echo json_encode(['success' => false, 'message' => 'Hai già votato']);
        exit;
    }

    // Inizio transazione
    $pdo->beginTransaction();

    // Imposta voto = 1 per lo studente
    $stmt = $pdo->prepare("UPDATE studente SET voto = 1 WHERE matricola = ?");
    $stmt->execute([$matricola]);

    // Incrementa i voti dei candidati
    $stmt = $pdo->prepare("UPDATE candidato SET numero_voti = numero_voti + 1 WHERE idCandidato = ?");

    $stmt->execute([$voto1]);
    $stmt->execute([$voto2]);

    // Commit finale
    $pdo->commit();

    echo json_encode(['success' => true, 'message' => 'Voto registrato con successo']);

} catch (Exception $e) {
    $pdo->rollBack();
    echo json_encode(['success' => false, 'message' => 'Errore durante il salvataggio: ' . $e->getMessage()]);
}
?>