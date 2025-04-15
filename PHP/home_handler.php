<?php
require_once("bootstrap.php");
header("Content-Type: application/json");

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit();
}

// Initialize response array
$response = ["success" => false, "message" => ""];

try {
    if (isset($_POST["action"])) {

        if ($_POST["action"] === "getWishlistItems") {
            
            // Se l’utente non è loggato...
            if (!isset($_SESSION["user_email"])) {
                $response = [
                    "success" => false,
                    "message" => "User not logged in"
                ];
                echo json_encode($response);
                exit;
            }

            $email = $_SESSION["user_email"];
            // Recupera i prodotti in wishlist dal DB
            $wishlistItems = $dbh->getWishlistItems($email);

            // Rispondi con la lista in formato JSON
            $response = [
                "success" => true,
                "wishlistItems" => $wishlistItems
            ];
            echo json_encode($response);
            exit;
        }

        if ($_POST["action"] === "toggleWishlist") {
            
            if (!isset($_SESSION["user_email"])) {
                $response["message"] = "Please login first";
                echo json_encode($response);
                exit();
            }

            $email = $_SESSION["user_email"];
            $productId = $_POST["productId"];

            if ($_POST["isAdd"] === "true") {
                if ($dbh->addToWishlist($email, $productId)) {
                    $response["success"] = true;
                    $response["message"] = "Added to wishlist";
                } else {
                    throw new Exception("Product already in wishlist");
                }
            } else {
                if ($dbh->removeFromWishlist($email, $productId)) {
                    $response["success"] = true;
                    $response["message"] = "Removed from wishlist";
                } else {
                    throw new Exception("Product not in wishlist");
                }
            }
        }
    }
} catch (Exception $e) {
    $response = ["success" => false, "message" => $e->getMessage()];
}

echo json_encode($response);
exit();
?>