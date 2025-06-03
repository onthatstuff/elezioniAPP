<?php
require 'db_connection.php';

$token = trim($_POST['token'] ?? '');

if (!empty($token)) {
    $stmt = $pdo->prepare("SELECT * FROM studente WHERE token = :token AND token_expiry > NOW() LIMIT 1");
    $stmt->bindParam(':token', $token);
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        // Solo risposta semplice "OK"
        echo "OK";
    } else {
        echo "INVALID";
    }
} else {
    echo "MISSING_TOKEN";
}
?>
