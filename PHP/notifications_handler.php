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
            case "getUserNotifications":
                $email = $_POST["email"];
                $notifications = $dbh->getUserNotifications($email);
                $response = [
                    "success" => true,
                    "notifications" => $notifications
                ];
                break;
                
            case "createNotification":
                $type = $_POST["type"];
                $message = $_POST["message"];
                $email = $_POST["email"];
                
                if ($dbh->createNotification($type, $message, $email)) {
                    $response = [
                        "success" => true,
                        "message" => "Notifica creata con successo"
                    ];
                } else {
                    throw new Exception("Errore nella creazione della notifica");
                }
                break;
                
            case "markNotificationsAsRead":
                $email = $_POST["email"];
                $notificationIds = isset($_POST["notificationIds"]) ? json_decode($_POST["notificationIds"], true) : null;
                
                if ($dbh->markNotificationsAsRead($email, $notificationIds)) {
                    $response = [
                        "success" => true,
                        "message" => "Notifiche contrassegnate come lette"
                    ];
                } else {
                    throw new Exception("Errore nel contrassegnare le notifiche");
                }
                break;
                
            case "getUnreadNotificationsCount":
                $email = $_POST["email"];
                $count = $dbh->getUnreadNotificationsCount($email);
                $response = [
                    "success" => true,
                    "count" => $count
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