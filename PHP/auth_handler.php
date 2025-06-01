<?php
require_once("bootstrap.php");
require_once("PHPMailer/src/PHPMailer.php");
require_once("PHPMailer/src/SMTP.php");
require_once("PHPMailer/src/Exception.php");

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;
header("Content-Type: application/json");

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Metodo di richiesta non valido"]);
    exit();
}

$response = ["success" => false, "message" => ""];

try {
    if (isset($_POST["action"])) {
        switch ($_POST["action"]) {
            case "registerUser":
                $email = $_POST["email"];
                $firstName = $_POST["firstName"];
                $lastName = $_POST["lastName"];
                $password = $_POST["password"];
                $newsletter = isset($_POST["newsletter"]) ? 'Y' : 'N';
                $phone = isset($_POST["phone"]) ? $_POST["phone"] : null;
                
                if ($dbh->registerUser($email, $firstName, $lastName, $password, $newsletter, $phone)) {
                    $response = [
                        "success" => true,
                        "message" => "Utente registrato con successo"
                    ];
                } else {
                    throw new Exception("Errore nella registrazione");
                }
                break;
                
            case "loginUser":
                $email = $_POST["email"];
                $password = $_POST["password"];
                $user = $dbh->loginUser($email, $password);
                
                if ($user) {
                    $response = [
                        "success" => true,
                        "user" => $user
                    ];
                } else {
                    throw new Exception("Email o password non validi");
                }
                break;
                
            case "changePassword":
                $email = $_POST["email"];
                $password = $_POST["password"];
                
                if ($dbh->changePassword($email, $password)) {
                    $response = [
                        "success" => true,
                        "message" => "Password cambiata con successo"
                    ];
                } else {
                    throw new Exception("Errore nel cambio password");
                }
                break;
                
            case "getUserProfile":
                $email = $_POST["email"];
                $profile = $dbh->getUserProfile($email);
                
                if ($profile) {
                    $response = [
                        "success" => true,
                        "profile" => $profile
                    ];
                } else {
                    throw new Exception("Profilo non trovato");
                }
                break;
                
            case "isUserRegistered":
                $email = $_POST["email"];

                if ($dbh->isUserRegistered($email)) {
                    $response = [
                        "success" => true,
                        "message" => "Utente registrato"
                    ];
                } else {
                    throw new Exception("Utente non registrato");
                }
                break;

            case "sendOTP":
                $email = $_POST["email"];
        
                $otp = sprintf("%06d", mt_rand(0, 999999));
                $_SESSION['recovery_otp'] = $otp;
                $_SESSION['recovery_email'] = $email;
                $_SESSION['recovery_expires'] = time() + 900; // 15 minutes

                $mail = new PHPMailer(true);

                $mail->isSMTP();
                $mail->Host = 'smtp.gmail.com';
                $mail->SMTPAuth = true;
                $mail->Username = 'urbankicks77@gmail.com';
                $mail->Password = 'xhztmkdkigqlumwo';
                $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS;
                $mail->Port = 465;
                $mail->CharSet = 'UTF-8';

                $mail->setFrom('urbankicks77@gmail.com', 'Kickify');
                $mail->addAddress($email);

                $mail->isHTML(true);
                $mail->Subject = 'Password Recovery OTP - Kickify';
                $mail->Body = "Your OTP for password recovery is: <b>$otp</b><br>This code will expire in 15 minutes.";
                $mail->AltBody = "Your OTP for password recovery is: $otp\nThis code will expire in 15 minutes.";

                $mail->send();
                $response = [
                    "success" => true, 
                    "message" => "OTP sent successfully"
                ];
                break;

            case "verifyOTP":
                $email = $_POST["email"];
                $otp = $_POST["otp"];
                
                if (!isset($_SESSION['recovery_otp']) || 
                    !isset($_SESSION['recovery_email']) || 
                    $_SESSION['recovery_email'] !== $email) {
                    throw new Exception("Invalid recovery session");
                }
                
                if ($_SESSION['recovery_expires'] <= time()) {
                    throw new Exception("OTP has expired");
                }
                
                if ($_SESSION['recovery_otp'] !== $otp) {
                    throw new Exception("Invalid OTP");
                }
                
                $response = [
                    "success" => true, 
                    "message" => "OTP verified"
                ];
                break;
                
            default:
                throw new Exception("Azione non valida");
        }
    } else {
        throw new Exception("Azione non specificata");
    }
} catch (Exception $e) {
    $response = ["success" => false, "message" => $e->getMessage()];
}

echo json_encode($response);
exit();
?>