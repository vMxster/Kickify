<?php
require_once("bootstrap.php");
header('Content-Type: application/json');

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit();
}

$response = ["success" => false, "message" => ""];

try {
    $email = $_SESSION['user_email'] ?? null;
    if (!$email) {
        throw new Exception('User not logged in');
    }

    if (isset($_POST['action'])) {
        switch ($_POST['action']) {
            case 'getAvailableSizes':
                if (!isset($_POST['productId']) || !isset($_POST['color'])) {
                    throw new Exception('Missing required parameters');
                }
                $sizes = $dbh->getSizesByColor($_POST['productId'], $_POST['color']);
                echo json_encode($sizes);
                exit();

            case 'moveToWishlist':
                if (!isset($_POST['productId']) || !isset($_POST['color']) || !isset($_POST['size'])) {
                    throw new Exception('Missing required parameters');
                }
                
                $cart = $dbh->getCartByEmail($_SESSION["user_email"]);
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
                    echo json_encode($response);
                    exit();
                }
        }
    } else if (isset($_POST['removeItem'])) {
        $cart = $dbh->getCartByEmail($_SESSION["user_email"]);
        if ($dbh->removeFromCart($email, $_POST['removeItem'], $_POST['color'], $_POST['size'])) {
            $cartInfo = calculateCartInfo($dbh, $cart['ID_Carrello']);
            $dbh->modifyCartTotalValue($cart['ID_Carrello'], $cartInfo['total']);
            $response = [
                'success' => true,
                'message' => 'Item removed',
                'itemCount' => $cartInfo['itemCount'],
                'cartTotal' => $cartInfo['total']
            ];
        }
    } else if (isset($_POST['getQuantity'])) {
        $availableQty = $dbh->getProductMaxQuantity(
            $_POST['productId'],
            $_POST['color'],
            $_POST['size']
        );
        $response = [
            'success' => true,
            'quantity' => $availableQty
        ];
    } else if (isset($_POST['adjustQuantity'])) {
        $cart = $dbh->getCartByEmail($_SESSION["user_email"]);
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
    } else if (isset($_POST['updateBoth'])) {
        $cart = $dbh->getCartByEmail($_SESSION["user_email"]);
        if ($dbh->updateSizeAndColor(
            $cart['ID_Carrello'],
            $_POST['productId'],
            $_POST['oldColor'],
            $_POST['oldSize'],
            $_POST['newColor'],
            $_POST['newSize']
        )) {
            $dbh->adjustCartQuantity(
                $cart['ID_Carrello'],
                $_POST['productId'],
                $_POST['newColor'],
                $_POST['newSize'],
                1
            );
            $cartInfo = calculateCartInfo($dbh, $cart['ID_Carrello']);
            $dbh->modifyCartTotalValue($cart['ID_Carrello'], $cartInfo['total']);
            $response = [
                'success' => true,
                'message' => 'Cart updated successfully',
                'itemCount' => $cartInfo['itemCount'],
                'cartTotal' => $cartInfo['total']
            ];
        } else {
            $response = [
                'success' => false,
                'message' => 'Selected variant is not available'
            ];
        }
    } else if (isset($_POST['updateColor'])) {
        $cart = $dbh->getCartByEmail($_SESSION["user_email"]);
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
    } else if (isset($_POST['updateSize'])) {
        $cart = $dbh->getCartByEmail($_SESSION["user_email"]);
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