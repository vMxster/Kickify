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
            case "registerUser":
                $email = $_POST["email"];
                $firstName = $_POST["firstName"];
                $lastName = $_POST["lastName"];
                $password = $_POST["password"];
                $newsletter = isset($_POST["newsletter"]) ? 'Y' : 'N';
                $phone = isset($_POST["phone"]) ? $_POST["phone"] : null;
                
                if ($dbh->registerUser($email, $firstName, $lastName, $password, $newsletter, $phone)) {
                    $response = [
                        "success" => true,
                        "message" => "Utente registrato con successo"
                    ];
                } else {
                    throw new Exception("Errore nella registrazione");
                }
                break;
                
            case "loginUser":
                $email = $_POST["email"];
                $password = $_POST["password"];
                $user = $dbh->loginUser($email, $password);
                
                if ($user) {
                    $response = [
                        "success" => true,
                        "user" => $user
                    ];
                } else {
                    throw new Exception("Email o password non validi");
                }
                break;
                
            case "changePassword":
                $email = $_POST["email"];
                $password = $_POST["password"];
                
                if ($dbh->changePassword($email, $password)) {
                    $response = [
                        "success" => true,
                        "message" => "Password cambiata con successo"
                    ];
                } else {
                    throw new Exception("Errore nel cambio password");
                }
                break;
                
            case "getUserProfile":
                $email = $_POST["email"];
                $profile = $dbh->getUserProfile($email);
                
                if ($profile) {
                    $response = [
                        "success" => true,
                        "profile" => $profile
                    ];
                } else {
                    throw new Exception("Profilo non trovato");
                }
                break;
                
            case "isUserRegistered":
                $email = $_POST["email"];
                $isRegistered = $dbh->isUserRegistered($email);
                $response = [
                    "success" => true,
                    "isRegistered" => $isRegistered
                ];
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