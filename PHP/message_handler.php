<?php
require_once("bootstrap.php");
header("Content-Type: application/json");

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Metodo di richiesta non valido"]);
    exit();
}

$response = ["success" => false, "message" => ""];

try {
    if (isset($_POST["action"])) {
        switch ($_POST["action"]) {
            case "sendMessage":
                $email = $_POST["email"];
                $subject = $_POST["subject"];
                $body = $_POST["body"];
                
                if ($dbh->sendMessage($email, $subject, $body)) {
                    $response = [
                        "success" => true,
                        "message" => "Messaggio inviato con successo"
                    ];
                } else {
                    throw new Exception("Errore nell'invio del messaggio");
                }
                break;
                
            default:
                throw new Exception("Azione non valida");
        }
    } else {
        throw new Exception("Azione non specificata");
    }
} catch (Exception $e) {
    $response = ["success" => false, "message" => $e->getMessage()];
}

echo json_encode($response);
exit();
?>