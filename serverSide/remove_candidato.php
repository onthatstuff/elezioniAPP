<?php
require 'db_connection.php';

$nome = $_POST['nome'] ?? '';
$cognome = $_POST['cognome'] ?? '';
$classe = $_POST['classe'] ?? '';

if (!empty($nome) && !empty($cognome) && !empty($classe)) {
    $stmt = $pdo->prepare("DELETE FROM candidato WHERE Nome = :nome AND Cognome = :cognome AND Classe = :classe");
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
