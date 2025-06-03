<?php
require 'db_connection.php';

$nome = $_POST['nome'] ?? '';
$cognome = $_POST['cognome'] ?? '';
$classe = $_POST['classe'] ?? '';

if (!empty($nome) && !empty($cognome) && !empty($classe)) {
    $stmt = $pdo->prepare("INSERT INTO candidato (nome, cognome, classe) VALUES (:nome, :cognome, :classe)");
    $success = $stmt->execute([
        'nome' => $nome,
        'cognome' => $cognome,
        'classe' => $classe
    ]);
        
    if ($success) {
        echo "OK";
    } else {
        echo "ERROR";
    }
} else {
    echo "MISSING_FIELDS";
}
?>
