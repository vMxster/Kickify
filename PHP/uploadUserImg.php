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
    if (!isset($_POST["email"]) || empty($_POST["email"])) {
        throw new Exception("Email empty or missing.");
    }
    
    $email = $_POST["email"];
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        throw new Exception("Invalid email");
    }
    
    if (!isset($_FILES["img"]) || $_FILES["img"]["error"] !== UPLOAD_ERR_OK) {
        throw new Exception("Error uploading image or image missing");
    }
    
    $imgFile = $_FILES["img"];
    $allowedTypes = ["image/jpeg", "image/jpg", "image/png"];
    $maxFileSize = 5 * 1024 * 1024; // 5 MB
    
    if (!in_array($imgFile["type"], $allowedTypes)) {
        throw new Exception("Only PNG and JPG images allowed");
    }
    
    if ($imgFile["size"] > $maxFileSize) {
        throw new Exception("Image too big. Max size allowed is 5MB.");
    }
    
    $imageExtension = strtolower(pathinfo($imgFile["name"], PATHINFO_EXTENSION));
    
    $uniqueFilename = uniqid("user_") . "." . $imageExtension;
    $uploadDirectory = "/userImg/";
    $targetPath = __DIR__ . $uploadDirectory . $uniqueFilename;
    
    if (!move_uploaded_file($imgFile["tmp_name"], $targetPath)) {
        throw new Exception("Couldn't save user image.");
    }
    
    // img link to be saved in DB
    $userimgPath = $uploadDirectory . $uniqueFilename;

    if ($dbh->updateUserImage($email, $userimgPath)) {
        $response = [
            "success" => true,
            "message" => "Immagine utente aggiornata con successo.",
            "user_email" => $email,
            "image_path" => $userimgPath
        ];
    } else {
        throw new Exception("Errore nel cambio password");
    }
    
} catch (Exception $e) {
    $response = ["success" => false, "message" => $e->getMessage()];
}

echo json_encode($response);
exit();
?>
