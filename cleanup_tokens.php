<?php
require 'db_connection.php';

$stmt = $pdo->prepare("UPDATE studente SET token = NULL, token_expiry = NULL WHERE token_expiry < NOW()");
$stmt->execute();
?>
