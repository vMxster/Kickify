<?php
require_once("bootstrap.php");
header("Content-Type: application/json");

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit();
}

$response = ["success" => false, "message" => ""];

try {
    if (isset($_POST["action"])) {
        switch ($_POST["action"]) {
            case "removeFromWishlist":
                $email = $_POST["user_email"];
                $productId = $_POST["productId"];

                if ($dbh->removeFromWishlist($email, $productId)) {
                    $response["success"] = true;
                    $response["message"] = "Removed from wishlist";
                } else {
                    throw new Exception("Product not in wishlist");
                }
                break;

            case "getWishlistItems":
                $email = $_POST["user_email"];
                $wishlistItems = $dbh->getWishlistItems($email);

                if ($wishlistItems) {
                    $response = [
                        "success" => true,
                        "items" => $wishlistItems
                    ];
                } else {
                    throw new Exception("No items in wishlist");
                }
                break;

            case "addToWishlist":
                $email = $_POST["user_email"];
                $productId = $_POST["productId"];

                if ($dbh->addToWishlist($email, $productId)) {
                    $response["success"] = true;
                    $response["message"] = "Added to wishlist";
                } else {
                    throw new Exception("Error adding to wishlist");
                }
                break;

            case "clearWishlist":
                $email = $_POST["user_email"];

                if ($dbh->clearWishlist($email)) {
                    $response["success"] = true;
                    $response["message"] = "Wishlist cleared";
                } else {
                    throw new Exception("Error clearing wishlist");
                }
                break;

            case "isInWishlist":
                $email = $_POST["user_email"];
                $productId = $_POST["productId"];

                $isInWishlist = $dbh->isInWishlist($email, $productId);
                
                $response = [
                    "success" => true,
                    "isInWishlist" => $isInWishlist
                ];
                break;

            default:
                throw new Exception("Invalid action");
        }
    }
} catch (Exception $e) {
    $response = ["success" => false, "message" => $e->getMessage()];
}

echo json_encode($response);
exit();
?>