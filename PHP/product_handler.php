<?php
require_once("bootstrap.php");
header('Content-Type: application/json');

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit();
}

// Initialize response array
$response = ["success" => false, "message" => ""];

try {
    if (!isset($_POST["action"])) {
        throw new Exception("No action specified");
    }

    $action = $_POST["action"];

    switch ($action) {
        case "get_disabled_sizes":
            if (!isset($_POST["product_id"], $_POST["color"])) {
                throw new Exception("Missing required fields");
            }
    
            $productId = $_POST["product_id"];
            $color = $_POST["color"];
    

            $availableSizes = $dbh->getSizesByColor($productId, $color);
    
            $allSizes = array_unique(array_column($dbh->getProductSizes($productId), 'Taglia')); 
            $disabledSizes = array_values(array_diff($allSizes, array_column($availableSizes, 'Taglia')));
    
            $response = [
                "success" => true,
                "disabledSizes" => $disabledSizes
            ];
            break;
    
        case "get_disabled_colors":

            if (!isset($_POST["product_id"], $_POST["size"])) {
                throw new Exception("Missing required fields");
            }
    
            $productId = $_POST["product_id"];
            $size = floatval($_POST["size"]);

            $availableColors = $dbh->getColorsBySize($productId, $size);
   
            $allColors = array_unique(array_column($dbh->getProductColors($productId), 'Colore')); 
            $disabledColors = array_values(array_diff($allColors, array_column($availableColors, 'Colore')));
    
            $response = [
                "success" => true,
                "disabledColors" => $disabledColors
            ];
            break;

        case "add_to_cart":
            // Validate required fields
            if (!isset($_POST["product_id"], $_POST["size"], $_POST["color"])) {
                throw new Exception("Missing required fields");
            }

            $email = $_POST['user_email'];
            $productId = $_POST["product_id"];
            $size = floatval($_POST["size"]);
            $color = $_POST["color"];
            $quantity = isset($_POST["quantity"]) ? intval($_POST["quantity"]) : 1;

            $success = $dbh->addToCart($email, $productId, $color, $size, $quantity);
            if (!$success) {
                throw new Exception("Failed to add item to cart");
            }
            $response = ["success" => true, "message" => "Product added to cart"];
            break;

        case "remove_from_cart":
            if (!isset($_POST["product_id"], $_POST["size"], $_POST["color"])) {
                throw new Exception("Missing required fields");
            }
            if (!isset($_POST["email"]) || empty($_POST["email"])) {
                throw new Exception("User must be logged in");
            }

            $email = filter_var($_POST["email"], FILTER_SANITIZE_EMAIL);
            $productId = $_POST["product_id"];
            $size = floatval($_POST["size"]);
            $color = $_POST["color"];

            $success = $dbh->removeFromCart($email, $productId, $color, $size);
            if (!$success) {
                throw new Exception("Failed to remove item from cart");
            }
            $response = ["success" => true, "message" => "Product removed from cart"];
            break;

        case "add_to_wishlist":
            $email = $_POST['user_email'];
            $productId = $_POST["product_id"];
            
            $success = $dbh->addToWishlist($email, $productId);
            if (!$success) {
                throw new Exception("Failed to add item to wishlist");
            }
            $response = ["success" => true, "message" => "Product added to wishlist"];
            break;

        case "remove_from_wishlist":
            if (!isset($_POST["product_id"])) {
                throw new Exception("Missing required fields");
            }
        
            $email = $_POST["user_email"];
            $productId = $_POST["product_id"];
        
            $success = $dbh->removeFromWishlist($email, $productId);
            if (!$success) {
                throw new Exception("Failed to remove item from wishlist");
            }
        
            $response = ["success" => true, "message" => "Product removed from wishlist"];
            break;

        case "add_review":
            if (!isset($_POST["product_id"], $_POST["rating"], $_POST["comment"])) {
                throw new Exception("Missing required fields");
            }

            $email = $_POST['user_email'];
            $productId = $_POST["product_id"];
            $rating = intval($_POST["rating"]);
            $comment = filter_var($_POST["comment"], FILTER_SANITIZE_STRING);

            // Validate rating range
            if ($rating < 1 || $rating > 5) {
                throw new Exception("Invalid rating value");
            }

            // Check if user can review (has purchased the product)
            if (!$dbh->canUserReview($email, $productId)) {
                throw new Exception("You must purchase this product before reviewing it");
            }

            $success = $dbh->addReview($email, $productId, $rating, $comment);
            if (!$success) {
                throw new Exception("Failed to add review");
            }
            $newReviewData = [
                'Punteggio'          => $rating,
                'Descrizione'        => $comment,
                'Data_Recensione'    => date('d/m/Y'), 
                'Email'              => $email,
                'PunteggioAVG'       => $dbh->getProductRating($productId)
            ];
        
            echo json_encode([
                'success'   => true,
                'newReview' => $newReviewData
            ]);
            
            exit();
            break;

        case "notify_availability":
            if (!isset($_POST["product_id"], $_POST["size"], $_POST["color"])) {
                throw new Exception("Missing required fields");
            }

            $email = filter_var($_POST["email"], FILTER_SANITIZE_EMAIL);
            $productId = $_POST["product_id"];
            $size = floatval($_POST["size"]);
            $color = $_POST["color"];

            // Add to wishlist to track notification
            $dbh->addToWishlist($email, $productId, $color, $size);
            
            $response = ["success" => true, "message" => "You will be notified when the product is available"];
            break;

        case "getProducts":
            $filters = isset($_POST["filters"]) ? $_POST["filters"] : [];
            $products = $dbh->getProducts($filters);
            $response = [
                "success" => true,
                "products" => $products
            ];
            break;
            
        case "getProductData":
            $productId = $_POST["productId"];
            $userEmail = isset($_POST["userEmail"]) ? $_POST["userEmail"] : null;
            $productData = $dbh->getProductData($productId, $userEmail);
            $response = [
                "success" => true,
                "productData" => $productData
            ];
            break;

        default:
            throw new Exception("Invalid action");
    }
} catch (Exception $e) {
    $response = ["success" => false, "message" => $e->getMessage()];
}

echo json_encode($response);
exit();
?>