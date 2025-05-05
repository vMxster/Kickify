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
            case "getOrderTracking":
                $orderId = $_POST["orderId"];
                $trackingInfo = $dbh->getOrderTracking($orderId);
                
                if ($trackingInfo) {
                    $response = [
                        "success" => true,
                        "tracking" => $trackingInfo
                    ];
                } else {
                    throw new Exception("Informazioni di tracking non trovate");
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