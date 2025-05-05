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
            case "placeOrder":
                $email = $_POST["email"];
                $total = $_POST["total"];
                $paymentMethod = $_POST["paymentMethod"];
                $shippingType = $_POST["shippingType"];
                $isGift = isset($_POST["isGift"]) ? $_POST["isGift"] == "true" : false;
                $giftFirstName = isset($_POST["giftFirstName"]) ? $_POST["giftFirstName"] : null;
                $giftLastName = isset($_POST["giftLastName"]) ? $_POST["giftLastName"] : null;
                
                $orderId = $dbh->placeOrder($email, $total, $paymentMethod, $shippingType, $isGift, $giftFirstName, $giftLastName);
                if ($orderId) {
                    $response = [
                        "success" => true,
                        "orderId" => $orderId,
                        "message" => "Ordine effettuato con successo"
                    ];
                } else {
                    throw new Exception("Errore nella creazione dell'ordine");
                }
                break;
                
            case "getOrders":
                $email = $_POST["email"];
                $orders = $dbh->getOrders($email);
                $response = [
                    "success" => true,
                    "orders" => $orders
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