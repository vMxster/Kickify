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
    // Email-only check (existing code)
    if (isset($_POST["check_email_only"]) && $_POST["check_email_only"]==="true") {
        $email = filter_var($_POST["emailinsert"], FILTER_SANITIZE_EMAIL);
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            throw new Exception("Invalid email format");
        }
        // Check if user already exists
        if ($dbh->isUserRegistered($email)) {
            throw new Exception("User with this email is already registered");
        }

        echo json_encode([
            "success" => true, 
            "message" => "Email is available"
        ]);
        exit();
    } else if (isset($_POST["email-register"]) && isset($_POST["password-register"])) {
        $email = $_POST["email-register"];
        $password = $_POST["password-register"];
        $firstName = $_POST["firstname-register"];
        $lastName = $_POST["lastname-register"];
        $phone = isset($_POST["phone-register"]) ? $_POST["phone-register"] : "";
        $newsletter = isset($_POST["newsletter-register"]) ? 1 : 0;

        // Register user
        if ($dbh->registerUser($email, $firstName, $lastName, $password, $newsletter, $phone)) {
            echo json_encode([
                "success" => true,
                "message" => "Registration successful"
            ]);
        } else {
            throw new Exception("Registration failed");
        }
    } else {
        throw new Exception("Missing required fields");
    }

} catch (Exception $e) {
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
?>