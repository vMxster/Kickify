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
    if (isset($_POST["check_email_only"]) && $_POST["check_email_only"]==="true") {
        $email = filter_var($_POST["email-login"], FILTER_SANITIZE_EMAIL);
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            throw new Exception("Invalid email format");
        }
        // Check if user already exists
        if (!$dbh->isUserRegistered($email)) {
            throw new Exception("User with this email is not registered");
        }

        echo json_encode([
            "success" => true, 
            "message" => "Email is registered"
        ]);
        exit();
    } else if (isset($_POST["password-login"])) {
        $user = $dbh->loginUser($_POST['email-login'], $_POST["password-login"]);
        if ($user) {
            $_POST['user_email'] = $_POST['email-login'];
            $_POST['role'] = $user["Ruolo"];
            $response = [
                "success" => true, 
                "message" => "Login successful",
                "role" => $user["Ruolo"]
            ];
        } else {
            throw new Exception("Invalid password");
        }
    } else {
        throw new Exception("Invalid operation");
    }
} catch (Exception $e) {
    $response = ["success" => false, "message" => $e->getMessage()];
}

echo json_encode($response);
exit();
?>