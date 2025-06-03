<?php
// Includi il file di connessione al database
require 'db_connection.php';

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';
//echo "DEBUG_EMAIL_RECEIVED: $email\n"; // Solo per debug, poi togli

if (filter_var($email, FILTER_VALIDATE_EMAIL)) {
    // Prepara la query per cercare l'utente
    $stmt = $pdo->prepare("SELECT Password FROM admin WHERE Email = :email");
    $stmt->execute(['email' => $email]);
    $storedPassword = $stmt->fetchColumn();
    //echo $storedPassword;
    

    if ($password === $storedPassword) {
        // La password è corretta
        echo "OK";
    } else {
        // La password è errata
        echo "INVALID_PASSWORD";
    }
} else {
    echo "INVALID_EMAIL";
}
?>
