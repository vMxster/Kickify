<?php
require_once("bootstrap.php");
require_once("PHPMailer/src/PHPMailer.php");
require_once("PHPMailer/src/SMTP.php");
require_once("PHPMailer/src/Exception.php");

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;
header('Content-Type: application/json');

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit();
}

// Initialize response array
$response = ["success" => false, "message" => ""];

try {
    // Email verification check
    if (isset($_POST["check_email_only"]) && $_POST["check_email_only"] === "true") {
        $email = filter_var($_POST["email-recovery"], FILTER_SANITIZE_EMAIL);
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            throw new Exception("Invalid email format");
        }
        if (!$dbh->isUserRegistered($email)) {
            throw new Exception("User with this email is not registered");
        }
        $response = ["success" => true, "message" => "Email verified"];
    } else if (isset($_POST["send_otp"]) && $_POST["send_otp"] === "true") {
        $email = filter_var($_POST["email-recovery"], FILTER_SANITIZE_EMAIL);
        
        // Generate 6-digit OTP
        $otp = sprintf("%06d", mt_rand(0, 999999));
        $_POST['recovery_otp'] = $otp;
        $_POST['recovery_email'] = $email;
        $_POST['recovery_expires'] = time() + 900; // 15 minutes

        // Create PHPMailer instance
        $mail = new PHPMailer(true);

        // Server settings
        $mail->isSMTP();
        $mail->Host = 'smtp.gmail.com';
        $mail->SMTPAuth = true;
        $mail->Username = 'urbankicks77@gmail.com';
        $mail->Password = 'xhztmkdkigqlumwo';
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS;
        $mail->Port = 465;
        $mail->CharSet = 'UTF-8';

        // Recipients
        $mail->setFrom('urbankicks77@gmail.com', 'UrbanKicks');
        $mail->addAddress($email);

        // Content
        $mail->isHTML(true);
        $mail->Subject = 'Password Recovery OTP - Urban Kicks';
        $mail->Body = "Your OTP for password recovery is: <b>$otp</b><br>This code will expire in 15 minutes.";
        $mail->AltBody = "Your OTP for password recovery is: $otp\nThis code will expire in 15 minutes.";

        $mail->send();
        $response = ["success" => true, "message" => "OTP sent successfully"];
    } else if (isset($_POST["verify_otp"]) && $_POST["verify_otp"] === "true") {
        $email = filter_var($_POST["email-recovery"], FILTER_SANITIZE_EMAIL);
        $otp = $_POST["otp"];
        
        if (!isset($_POST['recovery_otp']) || 
            !isset($_POST['recovery_email']) || 
            $_POST['recovery_email'] !== $email) {
            throw new Exception("Invalid recovery session");
        }
        
        if ($_POST['recovery_expires'] <= time()) {
            throw new Exception("OTP has expired");
        }
        
        if ($_POST['recovery_otp'] !== $otp) {
            throw new Exception("Invalid OTP");
        }
        
        $response = ["success" => true, "message" => "OTP verified"];
    } else if (isset($_POST["update_password"]) && $_POST["update_password"] === "true") {
        $email = filter_var($_POST["email"], FILTER_SANITIZE_EMAIL);
        $password = $_POST["password"];
        
        if (!isset($_POST['recovery_email']) || $_POST['recovery_email'] !== $email) {
            throw new Exception("Invalid recovery session");
        }
        
        // Update password in database
        if (!$dbh->changePassword($email, $password)) {
            throw new Exception("Failed to update password");
        }
        
        $response = ["success" => true, "message" => "Password updated successfully"];
    } else {
        throw new Exception("Invalid operation");
    }
} catch (Exception $e) {
    $response = ["success" => false, "message" => $e->getMessage()];
}

echo json_encode($response);
exit();