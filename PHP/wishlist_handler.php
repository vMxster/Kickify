<?php
require_once("bootstrap.php");
header("Content-Type: application/json");

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit();
}

// Initialize response array
$response = ["success" => false, "message" => ""];

    if ($_POST["action"] === "toggleWishlist") {
        $email = $_POST["user_email"];
        $productId = $_POST["productId"];

        if ($dbh->removeFromWishlist($email, $productId)) {
                $response["success"] = true;
                $response["message"] = "Removed from wishlist";
            } else {
                throw new Exception("Product not in wishlist");
            }
        }

echo json_encode($response);
exit();
?>