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
            $email = $_POST["user_email"];
            $wishlistItems = $dbh->getWishlistItems($email);
            
            $response = [
                "success" => true,
                "wishlistItems" => $wishlistItems
            ];
            echo json_encode($response);
            exit;
        }

        if ($_POST["action"] === "toggleWishlist") {
            $email = $_POST["user_email"];
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