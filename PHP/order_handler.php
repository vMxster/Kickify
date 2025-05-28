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
                $street = isset($_POST["street"]) ? $_POST["street"] : null;
                $city = isset($_POST["city"]) ? $_POST["city"] : null;
                $civic = isset($_POST["civic"]) ? $_POST["civic"] : null;
                $cap = isset($_POST["cap"]) ? $_POST["cap"] : null;
                
                $orderId = $dbh->placeOrder($email, $total, $paymentMethod, 
                    $shippingType, $isGift, $giftFirstName, $giftLastName, 
                    $street, $city, $civic, $cap);

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
                if (!isset($_POST["email"]) || !isset($_POST["last_access"])) {
                    throw new Exception("Email non specificata");
                }
                $email = $_POST["email"];
                $lastAccess = $_POST["last_access"];
                $orders = $dbh->getOrders($email, $lastAccess);
                $response = [
                    "success" => true,
                    "orders" => $orders
                ];
                break;

            case "getOrderDetails":
                if (!isset($_POST["orderId"])) {
                    throw new Exception("ID ordine non specificato");
                }
                $orderId = $_POST["orderId"];
                $orderDetails = $dbh->getOrderDetails($orderId);
                if ($orderDetails) {
                    $response = [
                        "success" => true,
                        "orderDetails" => $orderDetails
                    ];
                } else {
                    throw new Exception("Ordine non trovato");
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