<?php
$host = '127.0.0.1'; 
$dbname = 'serverelezioni';
$username = 'luigi';
$password = 'Diobello10!';  

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    // Imposta il modo di gestione degli errori su eccezioni
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    //echo "Connessione al database riuscita!";
} catch (PDOException $e) {
    echo "Errore di connessione: " . $e->getMessage();
}
?>
