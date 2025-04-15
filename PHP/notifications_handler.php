<?php
require_once("bootstrap.php");

if (!isset($_SESSION["user_email"])) {
    die(json_encode(['success' => false, 'message' => 'Unauthorized']));
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $response = ['success' => false, 'message' => ''];
    
    try {
        if (isset($_POST['notificationIds']) && isset($_POST['action'])) {
            $notificationIds = json_decode($_POST['notificationIds']);
            $action = $_POST['action'];
            $email = $_SESSION["user_email"];

            if (!is_array($notificationIds)) {
                $notificationIds = [$notificationIds];
            }

            $success = $dbh->markNotificationsAsRead($email, $notificationIds);
            
            if ($success) {
                $response = [
                    'success' => true,
                    'message' => $action === 'markAllRead' ? 
                        'All notifications marked as read' : 
                        'Notification marked as read'
                ];
            }
        } else {
            $response = ['success' => false, 'message' => 'Missing required parameters'];
        }
    } catch (Exception $e) {
        $response = ['success' => false, 'message' => $e->getMessage()];
    }

    header('Content-Type: application/json');
    echo json_encode($response);
    exit();
}
?>