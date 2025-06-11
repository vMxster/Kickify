<?php
require_once("bootstrap.php");
header('Content-Type: application/json');

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit();
}

$response = ["success" => false, "message" => ""];

try {
    $email = $_POST['user_email'] ?? null;

    if (isset($_POST['action'])) {
        switch ($_POST['action']) {
            case 'getCart':
                if (empty($email)) {
                    throw new Exception('Email is required');
                }
                $cart = $dbh->getCartByEmail($email);
                if ($cart) {
                    $cartInfo = calculateCartInfo($dbh, $cart['ID_Carrello']);
                    $response = [
                        'success' => true,
                        'cart' => $cart,
                        'itemCount' => $cartInfo['itemCount'],
                        'cartTotal' => $cartInfo['total']
                    ];
                } else {
                    $response = [
                        'success' => false,
                        'message' => 'Cart not found'
                    ];
                }
                break;

            case 'getCartItems':
                if (empty($email)) {
                    throw new Exception('Email is required');
                }
                $cart = $dbh->getCartByEmail($email);
                if ($cart) {
                    $items = $dbh->getCartItems($cart['ID_Carrello']);
                    $response = [
                        'success' => true,
                        'items' => $items
                    ];
                } else {
                    $response = [
                        'success' => false,
                        'message' => 'Cart not found'
                    ];
                }
                break;

            case 'getAvailableSizes':
                if (!isset($_POST['productId']) || !isset($_POST['color'])) {
                    throw new Exception('Missing required parameters');
                }
                $sizes = $dbh->getSizesByColor($_POST['productId'], $_POST['color']);
                $response = [
                    'success' => true,
                    'sizes' => $sizes
                ];
                break;

            case 'moveToWishlist':
                if (!isset($_POST['productId']) || !isset($_POST['color']) || !isset($_POST['size'])) {
                    throw new Exception('Missing required parameters');
                }
                
                $cart = $dbh->getCartByEmail($email);
                if ($dbh->addToWishlist($email, $_POST['productId'])) {
                    $dbh->removeFromCart($email, $_POST['productId'], $_POST['color'], $_POST['size']);
                    $cartInfo = calculateCartInfo($dbh, $cart['ID_Carrello']);
                    $dbh->modifyCartTotalValue($cart['ID_Carrello'], $cartInfo['total']);
                    $response = [
                        'success' => true,
                        'message' => 'Item moved to wishlist',
                        'itemCount' => $cartInfo['itemCount'],
                        'cartTotal' => $cartInfo['total']
                    ];
                }
                break;
            
            case 'removeItem':
                if (!isset($_POST['productId']) || !isset($_POST['color']) || !isset($_POST['size'])) {
                    throw new Exception('Missing required parameters');
                }

                $cart = $dbh->getCartByEmail($email);
                if ($dbh->removeFromCart($email, $_POST['productId'], $_POST['color'], $_POST['size'])) {
                    $cartInfo = calculateCartInfo($dbh, $cart['ID_Carrello']);
                    $dbh->modifyCartTotalValue($cart['ID_Carrello'], $cartInfo['total']);
                    $response = [
                        'success' => true,
                        'message' => 'Item removed',
                        'itemCount' => $cartInfo['itemCount'],
                        'cartTotal' => $cartInfo['total']
                    ];
                }
                break;

            case 'getQuantity':
                if (!isset($_POST['productId']) || !isset($_POST['color']) || !isset($_POST['size'])) {
                    throw new Exception('Missing required parameters');
                }

                $availableQty = $dbh->getProductMaxQuantity(
                    $_POST['productId'],
                    $_POST['color'],
                    $_POST['size']
                );
                $response = [
                    'success' => true,
                    'quantity' => $availableQty
                ];
                break;

            case 'adjustQuantity':
                if (!isset($_POST['productId']) || !isset($_POST['color']) 
                    || !isset($_POST['size']) || !isset($_POST['quantity'])) {
                    throw new Exception('Missing required parameters');
                }

                $cart = $dbh->getCartByEmail($email);
                if ($dbh->adjustCartQuantity(
                    $cart['ID_Carrello'],
                    $_POST['productId'],
                    $_POST['color'],
                    $_POST['size'],
                    $_POST['quantity']
                )) {
                    $cartInfo = calculateCartInfo($dbh, $cart['ID_Carrello']);
                    $dbh->modifyCartTotalValue($cart['ID_Carrello'], $cartInfo['total']);
                    $response = [
                        'success' => true,
                        'itemCount' => $cartInfo['itemCount'],
                        'cartTotal' => $cartInfo['total']
                    ];
                }
                break;

            case 'updateColor':
                if (!isset($_POST['productId']) || !isset($_POST['oldColor']) || !isset($_POST['newColor']) 
                    || !isset($_POST['size']) || !isset($_POST['quantity'])) {
                    throw new Exception('Missing required parameters');
                }

                $cart = $dbh->getCartByEmail($email);
                if ($dbh->updateCartItemColor(
                    $cart['ID_Carrello'], 
                    $_POST['productId'],
                    $_POST['oldColor'],
                    $_POST['newColor'],
                    $_POST['size']
                )) {
                    $dbh->adjustCartQuantity(
                        $cart['ID_Carrello'],
                        $_POST['productId'],
                        $_POST['newColor'],
                        $_POST['size'],
                        $_POST['quantity']
                    );
                    $cartInfo = calculateCartInfo($dbh, $cart['ID_Carrello']);
                    $response = [
                        'success' => true,
                        'message' => 'Color updated successfully',
                        'itemCount' => $cartInfo['itemCount'],
                        'cartTotal' => $cartInfo['total']
                    ];
                } else {
                    $response = [
                        'success' => false,
                        'message' => 'Failed to update color'
                    ];
                }
                break;

            case 'updateSize':
                if (!isset($_POST['productId']) || !isset($_POST['color']) || !isset($_POST['oldSize']) 
                    || !isset($_POST['newSize']) || !isset($_POST['quantity'])) {
                    throw new Exception('Missing required parameters');
                }
                
                $cart = $dbh->getCartByEmail($email);
                if ($dbh->updateSizeOnly(
                    $cart['ID_Carrello'],
                    $_POST['productId'],
                    $_POST['color'],
                    $_POST['oldSize'],
                    $_POST['newSize']
                )) {
                    $dbh->adjustCartQuantity(
                        $cart['ID_Carrello'],
                        $_POST['productId'],
                        $_POST['color'],
                        $_POST['newSize'],
                        $_POST['quantity']
                    );
                    $cartInfo = calculateCartInfo($dbh, $cart['ID_Carrello']);
                    $response = [
                        'success' => true,
                        'message' => 'Size updated successfully',
                        'itemCount' => $cartInfo['itemCount'],
                        'cartTotal' => $cartInfo['total']
                    ];
                } else {
                    $response = [
                        'success' => false,
                        'message' => 'Selected size is not available'
                    ];
                }
                break;
            
            default:
                throw new Exception('Invalid action');
        }
    }
} catch (Exception $e) {
    $response = [
        'success' => false,
        'message' => $e->getMessage()
    ];
}

echo json_encode($response);
exit();

function getCartId(DatabaseHelper $dbh, string $email): int {
    $cartInfo = $dbh->getCartByEmail($email);
    if (!$cartInfo) {
        throw new Exception('Cart not found');
    }
    return $cartInfo['ID_Carrello'];
}

function calculateCartInfo(DatabaseHelper $dbh, int $cartId): array {
    $items = $dbh->getCartItems($cartId);
    $total = 0.0;
    $itemCount = 0;
    
    foreach ($items as $item) {
        $total += $item['Prezzo'] * $item['Quantita'];
        $itemCount += $item['Quantita'];
    }
    
    return [
        'total' => round($total, 2),
        'itemCount' => $itemCount
    ];
}
?>