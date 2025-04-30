<?php
require_once("bootstrap.php");
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit;
}

try {
    $data = json_decode(file_get_contents('php://input'), true);
    $action = isset($_POST["code"]) ? "promo" : "payment";

    switch($action) {
        case "promo":
            $promoCode = trim($_POST["code"]);
            if (empty($promoCode)) {
                echo json_encode(['valid' => false]);
                exit;
            }

            $discount = $dbh->checkPromoCode($promoCode);
            if ($discount) {
                $_POST['discount'] = $discount;
                echo json_encode([
                    'valid' => true,
                    'value' => $discount['Valore'],
                    'type' => $discount['TipoSconto']
                ]);
            } else {
                echo json_encode(['valid' => false]);
            }
            break;

        case "payment":
            $orderId = $dbh->placeOrder(
                $_POST["user_email"],
                $data['total'],
                $data['paymentMethod'],
                $data['shippingType'],
                isset($_POST['gift_wrap']) ? true : false,
                $data['firstName'],
                $data['lastName']
            );
            
            echo json_encode([
                "success" => true,
                "orderId" => $orderId
            ]);
            break;
    }

} catch (Exception $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error processing request: " . $e->getMessage()
    ]);
}
?>