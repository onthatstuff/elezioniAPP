<?php
require 'db_connection.php';
require 'vendor/autoload.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// Leggi e valida l'email dal POST
$email = filter_var(trim($_POST['email'] ?? ''), FILTER_SANITIZE_EMAIL);

if (!empty($email) && filter_var($email, FILTER_VALIDATE_EMAIL)) {
    // Confronto case-insensitive
    $stmt = $pdo->prepare("SELECT * FROM studente WHERE LOWER(TRIM(Mail)) = :email");
    $stmt->execute(['email' => strtolower($email)]);
    $user = $stmt->fetch();

    if ($user) {
        $token = generateToken();

        $mail = new PHPMailer(true);

        try {
            // Configurazione SMTP
            $mail->isSMTP();
            $mail->Host = 'smtp.gmail.com';
            $mail->SMTPAuth = true;
            $mail->Username = 'elezionitassinari@gmail.com';
            $mail->Password = 'ktnl utse ccii qvuy';
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
            $mail->Port = 587;

            // Mittente e destinatario
            $mail->setFrom('elezionitassinari@gmail.com', 'TOKEN ELEZIONI');
            $mail->addAddress($email);

            // Email HTML + Plain Text fallback
            $mail->isHTML(true);
            $mail->Subject = 'Il tuo token di accesso alle elezioni';

            $mail->Body = "
                <html>
                    <head>
                        <style>
                            .container {
                                max-width: 600px;
                                margin: 0 auto;
                                padding: 20px;
                                font-family: Arial, sans-serif;
                                background-color: #f9f9f9;
                                border: 1px solid #ddd;
                                border-radius: 8px;
                            }
                            .header {
                                text-align: center;
                                font-size: 22px;
                                font-weight: bold;
                                color: #333;
                            }
                            .token-box {
                                margin: 20px auto;
                                padding: 15px;
                                background-color: #e6f4ea;
                                border-left: 5px solid #34a853;
                                font-size: 18px;
                                text-align: center;
                                color: #222;
                                font-weight: bold;
                                border-radius: 5px;
                            }
                            .footer {
                                margin-top: 30px;
                                font-size: 12px;
                                color: #999;
                                text-align: center;
                            }
                        </style>
                    </head>
                    <body>
                        <div class='container'>
                            <div class='header'>Il tuo token di voto è pronto!</div>
                            <p>Ciao,</p>
                            <p>Hai richiesto un token per accedere al sistema di votazione. Usa il codice qui sotto per procedere:</p>
                            <div class='token-box'>$token</div>
                            <p>Per qualsiasi problema, contatta il responsabile delle elezioni.</p>
                            <div class='footer'>© ".date('Y')." Elezioni Tassinari. Tutti i diritti riservati.</div>
                        </div>
                    </body>
                </html>";

            $mail->AltBody = "Ciao,\n\nEcco il tuo token: $token\n\nGrazie.\n\n- Elezioni Tassinari";

            $mail->send();
        } catch (Exception $e) {
            echo "EMAIL_ERROR: {$mail->ErrorInfo}";
        }
    } else {
        echo "USER_NOT_FOUND";
    }
} else {
    echo "INVALID_EMAIL";
}

// Funzione per generare token sicuro
function generateToken($length = 16) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';     
    $token = '';
    $maxIndex = strlen($characters) - 1;
    for ($i = 0; $i < $length; $i++) {
        $token .= $characters[random_int(0, $maxIndex)];
    }
    return $token;
}
?>
