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
            case "addReview":
                $email = $_POST["email"];
                $productId = $_POST["productId"];
                $rating = $_POST["rating"];
                $comment = $_POST["comment"];
                
                if ($dbh->addReview($email, $productId, $rating, $comment)) {
                    $response = [
                        "success" => true,
                        "message" => "Recensione aggiunta con successo"
                    ];
                } else {
                    throw new Exception("Errore nell'aggiunta della recensione");
                }
                break;
                
            case "deleteReview":
                $email = $_POST["email"];
                $productId = $_POST["productId"];
                
                if ($dbh->deleteReview($email, $productId)) {
                    $response = [
                        "success" => true,
                        "message" => "Recensione eliminata con successo"
                    ];
                } else {
                    throw new Exception("Errore nell'eliminazione della recensione");
                }
                break;
                
            case "getProductRating":
                $productId = $_POST["productId"];
                $rating = $dbh->getProductRating($productId);
                $response = [
                    "success" => true,
                    "rating" => $rating
                ];
                break;
                
            case "canUserReview":
                $email = $_POST["email"];
                $productId = $_POST["productId"];
                $canReview = $dbh->canUserReview($email, $productId);
                $response = [
                    "success" => true,
                    "canReview" => $canReview
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