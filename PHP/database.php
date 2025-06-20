<?php

class DatabaseHelper {
    private $db;

    public function __construct($servername, $username, $password, $dbname, $port) {
        $this->db = new mysqli($servername, $username, $password, $dbname, $port);
        if ($this->db->connect_error) {
            die("Failed to connect to the database");
        }
    }

    /*******************
     * PRODUCT QUERIES *
     *******************/

    // Get all product brands
    public function getDistinctBrands(): array {
        $query = "SELECT DISTINCT Marca FROM PRODOTTO WHERE Marca IS NOT NULL ORDER BY Marca ASC";
        $result = $this->db->query($query);
        
        $brands = [];
        while ($row = $result->fetch_array(MYSQLI_NUM)) {
            $brands[] = $row[0];
        }
        
        return $brands;
    }

    // Get product Variants
    public function getProductVariants($productId) {
        $query = "SELECT * FROM VARIANTE WHERE ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $productId);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }
    
    // Get all products
    public function getProducts($lastAccess) {
        $query = "SELECT p.* 
                  FROM PRODOTTO p 
                  WHERE p.Data_Aggiunta > ? 
                  ORDER BY p.Data_Aggiunta DESC";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $lastAccess);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    // Get product images
    public function getProductsImages($productIds) {
        $placeholders = implode(',', array_fill(0, count($productIds), '?'));
        $query = "SELECT i.* 
                  FROM IMMAGINE i 
                  WHERE i.ID_Prodotto IN ($placeholders) 
                  ORDER BY i.ID_Prodotto, i.Numero";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param(str_repeat('i', count($productIds)), ...$productIds);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    public function getFilteredProducts(
        $brand = null, 
        $type = null, 
        $size = null, 
        $color = null, 
        $minPrice = null, 
        $maxPrice = null
    ) {
        // Costruiamo la query base con la sottoquery per la media dei punteggi
        $query = "
            SELECT 
                p.*, 
                v.Colore, 
                v.Quantita, 
                v.Taglia,
                CASE 
                    WHEN ps.Prezzo IS NOT NULL AND p.Prezzo < ps.Prezzo THEN 1
                    ELSE 0 
                END AS isDiscounted,
                (
                    SELECT COALESCE(AVG(r.Punteggio), 0)
                    FROM RECENSIONE r
                    WHERE r.ID_Prodotto = p.ID_Prodotto
                ) AS mediaRecensioni
            FROM PRODOTTO p
            LEFT JOIN VARIANTE v ON p.ID_Prodotto = v.ID_Prodotto
            LEFT JOIN (
                SELECT ID_Prodotto, Prezzo 
                FROM prodotto_storico 
                ORDER BY Data_Modifica DESC
            ) ps ON p.ID_Prodotto = ps.ID_Prodotto
            WHERE 1=1
              AND p.Sta_Tipo != 'Not Available'
        ";
    
        $params = [];
        $types = "";
    
        // Filtri opzionali
        if ($brand) {
            $query .= " AND p.Marca = ?";
            $params[] = $brand;
            $types .= "s";
        }
        if ($type) {
            $query .= " AND p.Tipo = ?";
            $params[] = $type;
            $types .= "s";
        }
        if ($size) {
            $query .= " AND v.Taglia = ?";
            $params[] = $size;
            // supponendo Taglia sia numerica usa 'd', 
            // se è VARCHAR allora usa 's'
            $types .= "s"; 
        }
        if ($color) {
            $query .= " AND v.Colore = ?";
            $params[] = $color;
            $types .= "s";
        }
        if ($minPrice) {
            $query .= " AND p.Prezzo >= ?";
            $params[] = $minPrice;
            $types .= "d";
        }
        if ($maxPrice) {
            $query .= " AND p.Prezzo <= ?";
            $params[] = $maxPrice;
            $types .= "d";
        }
    
        $stmt = $this->db->prepare($query);
        if (!empty($params)) {
            $stmt->bind_param($types, ...$params);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }


    // Admin: Add new product
    public function addProduct($productId, $name, $description, $brand, $type, $price) {
        $query = "INSERT INTO PRODOTTO (ID_Prodotto, Nome, Descrizione, Marca, Tipo, Prezzo, Data_Aggiunta, Sta_Tipo) 
                  VALUES (?, ?, ?, ?, ?, ?, NOW(), 'disponibile')";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("sssssd", $productId, $name, $description, $brand, $type, $price);
        return $stmt->execute();
    }

    // Admin: Update product price
    public function updateProductPrice($productId, $newPrice) {
        // First, store the old price in history
        $query = "INSERT INTO PRODOTTO_STORICO (ID_Prodotto, Prezzo, Data_Modifica) 
                  SELECT ID_Prodotto, Prezzo, NOW() FROM PRODOTTO WHERE ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $productId);
        $stmt->execute();

        // Then update the current price
        $query = "UPDATE PRODOTTO SET Prezzo = ? WHERE ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ds", $newPrice, $productId);
        return $stmt->execute();
    }

    public function isProductDiscounted($productId) {
        $query = "
            SELECT 
                CASE 
                    WHEN ps.Prezzo IS NOT NULL AND p.Prezzo < ps.Prezzo THEN 1 
                    ELSE 0 
                END AS Scontato
            FROM prodotto p
            LEFT JOIN (
                SELECT ID_Prodotto, Prezzo 
                FROM prodotto_storico 
                WHERE ID_Prodotto = ? 
                ORDER BY Data_Modifica DESC 
                LIMIT 1
            ) ps ON p.ID_Prodotto = ps.ID_Prodotto
            WHERE p.ID_Prodotto = ?
        ";
    
        try {
            $stmt = $this->db->prepare($query);
            if (!$stmt) {
                throw new Exception("Failed to prepare the statement: " . $this->db->error);
            }
    
            $stmt->bind_param("ii", $productId, $productId);
            $stmt->execute();
            $result = $stmt->get_result();
    
            if ($row = $result->fetch_assoc()) {
                return (bool)$row['Scontato'];
            }
    
            return false;
        } catch (Exception $e) {
            error_log("Error in isProductDiscounted: " . $e->getMessage());
            return false;
        }
    }

    public function getProductData($productId, $userEmail) {
        $query = "SELECT 
                p.*,
                v.Colore,
                v.Taglia,
                v.Quantita,
                r.Punteggio,
                r.Descrizione as RecensioneDescrizione,
                r.Data_Recensione,
                r.Email as ReviewerEmail,
                u.Nome as RecensioneNome,
                u.Cognome as RecensioneCognome,
                CASE WHEN w.Email IS NOT NULL THEN 1 ELSE 0 END as InWishlist,
                CASE WHEN cart.ID_Prodotto IS NOT NULL THEN 1 ELSE 0 END as InCart,
                cart.Quantita as CartQuantity
            FROM PRODOTTO p
            LEFT JOIN VARIANTE v ON p.ID_Prodotto = v.ID_Prodotto
            LEFT JOIN RECENSIONE r ON p.ID_Prodotto = r.ID_Prodotto 
            LEFT JOIN UTENTE u ON r.Email = u.Email
            LEFT JOIN (
                SELECT a.ID_Prodotto, w.Email 
                FROM WISHLIST w 
                JOIN aggiungere a ON w.Email = a.Email 
                WHERE w.Email = ?
            ) w ON p.ID_Prodotto = w.ID_Prodotto
            LEFT JOIN (
                SELECT comp.ID_Prodotto, comp.Quantita 
                FROM CARRELLO c 
                JOIN comprendere comp ON c.ID_Carrello = comp.ID_Carrello 
                WHERE c.Email = ?
            ) cart ON p.ID_Prodotto = cart.ID_Prodotto
            WHERE p.ID_Prodotto = ?";
    
        try {
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("ssi", $userEmail, $userEmail, $productId);
            $stmt->execute();
            $result = $stmt->get_result();
    
            $productData = [
                'product' => null,
                'variants' => [],
                'reviews' => [],
                'inWishlist' => false,
                'inCart' => false,
                'cartQuantity' => 0
            ];

            $uniqueReviews = [];
    
            while($row = $result->fetch_assoc()) {
                if(!$productData['product']) {
                    $productData['product'] = [
                        'ID_Prodotto' => $row['ID_Prodotto'],
                        'Nome' => $row['Nome'],
                        'Descrizione' => $row['Descrizione'],
                        'Marca' => $row['Marca'],
                        'Tipo' => $row['Tipo'],
                        'Genere' => $row['Genere'],
                        'Prezzo' => $row['Prezzo'],
                        'Data_Aggiunta' => $row['Data_Aggiunta'],
                        'Sta_Tipo' => $row['Sta_Tipo']
                    ];
                }
    
                if($row['Colore'] && $row['Taglia']) {
                    $variantKey = $row['Colore'] . '_' . $row['Taglia'];
                    if(!isset($productData['variants'][$variantKey])) {
                        $productData['variants'][$variantKey] = [
                            'Colore' => $row['Colore'],
                            'Taglia' => $row['Taglia'],
                            'Quantita' => $row['Quantita']
                        ];
                    }
                }
    
                
                // adding only unique reviews
                if ($row['Punteggio']) {
                    $reviewKey = $row['Punteggio'] . '_' . $row['ReviewerEmail'] . '_' . $row['Data_Recensione'];
                    if (!isset($uniqueReviews[$reviewKey])) {
                        $productData['reviews'][] = [
                            'Punteggio' => $row['Punteggio'],
                            'Descrizione' => $row['RecensioneDescrizione'],
                            'Data_Recensione' => $row['Data_Recensione'],
                            'Email' => $row['ReviewerEmail'],
                            'Nome' => $row['RecensioneNome'],
                            'Cognome' => $row['RecensioneCognome']
                        ];
                        $uniqueReviews[$reviewKey] = true;
                    }
                }
    
                $productData['inWishlist'] = (bool)$row['InWishlist'];
                $productData['inCart'] = (bool)$row['InCart'];
                $productData['cartQuantity'] = $row['CartQuantity'] ?? 0;
            }
            
    
            return $productData;
    
        } catch(Exception $e) {
            error_log("Error in getProductData: " . $e->getMessage());
            throw $e;
        }
    }

    // Get product history
    public function getProductsHistory($lastAccess) {
        $query = "SELECT * 
                  FROM PRODOTTO_STORICO 
                  WHERE Data_Modifica > ?";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $lastAccess);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    public function getVersions() {
        $stmt = $this->db->prepare("SELECT * FROM VARIANTE");
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    // Admin: Update product stock
    public function updateProduct($productId, $description, $price, $variants) {
        try {
            $this->db->begin_transaction();
    
            // Get current product price
            $query = "SELECT Prezzo, Sta_Tipo FROM PRODOTTO WHERE ID_Prodotto = ?";
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("i", $productId);
            $stmt->execute();
            $result = $stmt->get_result()->fetch_assoc();
            $currentPrice = $result['Prezzo'];
            $currentStatus = $result['Sta_Tipo'];
    
            // If price changed, add entry to PRODOTTO_STORICO
            if($currentPrice != $price) {
                $historicQuery = "INSERT INTO PRODOTTO_STORICO (ID_Prodotto, Prezzo, Data_Modifica) 
                                VALUES (?, ?, NOW())";
                $historicStmt = $this->db->prepare($historicQuery);
                $historicStmt->bind_param("id", $productId, $currentPrice);
                $historicStmt->execute();

                // Create sale notification if price decreased
                if($currentPrice > $price) {
                    $discountAmount = $currentPrice - $price;
                    $discountPercentage = round(($discountAmount / $currentPrice) * 100);
                    $this->createSaleNotification($productId, $discountPercentage);
                }
            }
    
            // Update product description and price
            $query = "UPDATE PRODOTTO SET Descrizione = ?, Prezzo = ?, Sta_Tipo = 'Available' WHERE ID_Prodotto = ?";
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("sdi", $description, $price, $productId);
            $stmt->execute();
    
            // Update variants
            $hasStock = false;
            foreach($variants as $size => $colors) {
                foreach($colors as $color => $quantity) {
                    $query = "UPDATE VARIANTE SET Quantita = ? 
                             WHERE ID_Prodotto = ? AND Taglia = ? AND Colore = ?";
                    $stmt = $this->db->prepare($query);
                    $stmt->bind_param("iids", $quantity, $productId, $size, $color);
                    $stmt->execute();

                    if ($quantity > 0) {
                        $hasStock = true;
                    }
                }
            }

            // Create stock notification if product was Not Available and now has stock
            if ($currentStatus === 'Not Available' && $hasStock) {
                $this->createStockNotification($productId);
            }
    
            $this->db->commit();
            return true;
        } catch (Exception $e) {
            $this->db->rollback();
            throw $e;
        }
    }

    // Admin: Delete product
    public function deleteProduct($productId) {
        try {
            $this->db->begin_transaction();
    
            // Delete all variants
            $query = "UPDATE VARIANTE SET Quantita = 0 WHERE ID_Prodotto = ?";
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("i", $productId);
            $stmt->execute();
    
            // Update product status to Not Available
            $query = "UPDATE PRODOTTO SET Sta_Tipo = 'Not Available' WHERE ID_Prodotto = ?";
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("i", $productId);
            $stmt->execute();
    
            $this->db->commit();
            return true;
    
        } catch (Exception $e) {
            $this->db->rollback();
            error_log("Error in deleteProduct: " . $e->getMessage());
            throw $e;
        }
    }

    // Admin: Add new product with variants
    public function addProductWithVariants($brand, $model, $genres, $category, $description, $price, $variants) {
        try {
            $this->db->begin_transaction();
            $productIds = [];
    
            foreach ($genres as $genre) {
                // Check if product exists
                $checkQuery = "SELECT ID_Prodotto, Prezzo 
                             FROM PRODOTTO 
                             WHERE Nome = ? AND Marca = ? AND Genere = ?";
                $checkStmt = $this->db->prepare($checkQuery);
                $checkStmt->bind_param("sss", $model, $brand, ucfirst($genre));
                $checkStmt->execute();
                $result = $checkStmt->get_result();
    
                if ($result->num_rows > 0) {
                    // Product exists - update if price different
                    $row = $result->fetch_assoc();
                    $productId = $row['ID_Prodotto'];
                    $currentPrice = $row['Prezzo'];
    
                    if ($currentPrice != $price) {
                        // Update price
                        $updateQuery = "UPDATE PRODOTTO 
                                      SET Prezzo = ?, Sta_Tipo = 'Available' 
                                      WHERE ID_Prodotto = ?";
                        $updateStmt = $this->db->prepare($updateQuery);
                        $updateStmt->bind_param("di", $price, $productId);
                        $updateStmt->execute();
    
                        // Log price history
                        $historyQuery = "INSERT INTO PRODOTTO_STORICO 
                                       (ID_Prodotto, Prezzo, Data_Modifica) 
                                       VALUES (?, ?, NOW())";
                        $historyStmt = $this->db->prepare($historyQuery);
                        $historyStmt->bind_param("id", $productId, $currentPrice);
                        $historyStmt->execute();
                    }
                    $this->createStockNotification($productId);
                } else {
                    // Create new product
                    $insertQuery = "INSERT INTO PRODOTTO 
                                  (Nome, Descrizione, Marca, Tipo, Genere, 
                                   Prezzo, Data_Aggiunta, Sta_Tipo) 
                                  VALUES (?, ?, ?, ?, ?, ?, NOW(), 'Available')";
                    $insertStmt = $this->db->prepare($insertQuery);
                    $insertStmt->bind_param("sssssd", 
                        $model, $description, $brand, $category, ucfirst($genre), $price
                    );
                    $insertStmt->execute();
                    $productId = $this->db->insert_id;
                }
    
                $productIds[] = $productId;
    
                // Add/Update variants
                $variantQuery = "INSERT INTO VARIANTE (ID_Prodotto, Colore, Taglia, Quantita) 
                               VALUES (?, ?, ?, ?) 
                               ON DUPLICATE KEY UPDATE Quantita = VALUES(Quantita)";
                $variantStmt = $this->db->prepare($variantQuery);
    
                foreach ($variants as $variant) {
                    $variantStmt->bind_param("isdi", 
                        $productId,
                        $variant['color'],
                        $variant['size'], 
                        $variant['quantity']
                    );
                    $variantStmt->execute();
                }
            }
    
            $this->db->commit();
            return $productIds;
    
        } catch (Exception $e) {
            $this->db->rollback();
            throw $e;
        }
    }

    /*******************
     * MESSAGE QUERIES *
     *******************/

    // User: Send message to admin
    public function sendMessage($email, $subject, $body) {
        $query = "INSERT INTO MESSAGGIO (Email, Oggetto, Corpo, Timestamp_Invio) 
                  VALUES (?, ?, ?, NOW())";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("sss", $email, $subject, $body);
        return $stmt->execute();
    }

    // Admin: Get all messages
    public function getAllMessages() {
        $query = "SELECT m.*, u.Nome, u.Cognome, u.Ruolo 
                  FROM MESSAGGIO m
                  JOIN UTENTE u ON m.Email = u.Email
                  ORDER BY m.Timestamp_Invio ASC";
        
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $result = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
        
        // Group messages by thread
        $threads = [];
        foreach ($result as $message) {
            $subject = $message['Oggetto'];
            if (strpos($subject, 'Re: ') === 0) {
                $originalSubject = substr($subject, 4);
                if (!isset($threads[$originalSubject])) {
                    $threads[$originalSubject] = ['original' => null, 'replies' => []];
                }
                $threads[$originalSubject]['replies'][] = $message;
            } else {
                if (!isset($threads[$subject])) {
                    $threads[$subject] = ['original' => null, 'replies' => []];
                }
                $threads[$subject]['original'] = $message;
            }
        }
        
        return $threads;
    }

    /*******************
     * TRACKING QUERIES *
     *******************/

    // Get order tracking information
    public function getOrderTracking($orderId) {
        try {
            $query = "SELECT 
                o.ID_Ordine,
                o.Tipo_Spedizione as shipping_type,
                o.Data_Ordine as order_date,
                ts.Posizione as location,
                ts.Stato as status,
                ts.Arrivo_Effettivo as actual_arrival,
                ts.Arrivo_Stimato as estimated_arrival,
                ts.Timestamp_Aggiornamento as timestamp
            FROM ORDINE o 
            LEFT JOIN Tracking_Spedizione ts ON o.ID_Ordine = ts.ID_Ordine 
            WHERE o.ID_Ordine = ? 
            ORDER BY ts.Timestamp_Aggiornamento DESC";
    
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("i", $orderId);
            $stmt->execute();
            $result = $stmt->get_result();
            
            $tracking = [
                'order_info' => null,
                'tracking_states' => []
            ];
    
            $states = ['Placed', 'In progress', 'Shipped', 'Delivered'];
            $tracking = [
                'order_info' => null,
                'tracking_states' => array_fill_keys($states, [
                    'status' => null,
                    'location' => null,
                    'timestamp' => null,
                    'estimated_arrival' => null,
                    'actual_arrival' => null
                ])
            ];

            while ($row = $result->fetch_assoc()) {
                // Set order info only once
                if (!$tracking['order_info']) {
                    $tracking['order_info'] = [
                        'order_id' => $row['ID_Ordine'],
                        'shipping_type' => $row['shipping_type'],
                        'order_date' => $row['order_date'],
                        'current_status' => $row['status'],
                        'current_location' => $row['location'],
                        'estimated_arrival' => $row['estimated_arrival']
                    ];
                }

                // Update tracking state if status exists
                if ($row['status'] && isset($tracking['tracking_states'][$row['status']])) {
                    $tracking['tracking_states'][$row['status']] = [
                        'status' => $row['status'],
                        'location' => $row['location'],
                        'timestamp' => $row['timestamp'],
                        'estimated_arrival' => $row['estimated_arrival'],
                        'actual_arrival' => $row['actual_arrival']
                    ];
                }
            }

            // Convert tracking states to indexed array maintaining order
            $tracking['tracking_states'] = array_values($tracking['tracking_states']);

            return $tracking;
        } catch (Exception $e) {
            error_log("Error in getOrderTracking: " . $e->getMessage());
            return null;
        }
    }

    // Update Order status
    public function updateOrderStatus($orderId, $newStatus) {
        $currentTimestamp = date('Y-m-d H:i:s');
        
        // Mark current status as completed
        $query = "UPDATE Tracking_Spedizione 
                    SET Arrivo_Effettivo = ?,
                        Timestamp_Aggiornamento = ?
                    WHERE ID_Ordine = ? AND Stato = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ssis", $currentTimestamp, $currentTimestamp, $orderId, $newStatus);
        $stmt->execute();

        // Create notification
        $this->createOrderNotification($orderId, strtolower($newStatus));
        if ($newStatus === 'Delivered') {
            $this->createReviewRequestNotification($orderId);
        }
        return true;
    }

    /*******************
     * DISCOUNT QUERIES *
     *******************/

    // Admin: Create discount code
    public function createDiscount($discountId, $description, $type, $value, $startDate, $endDate) {
        $query = "INSERT INTO SCONTO (ID_Sconto, Descrizione, TipoSconto, Valore, Data_Inizio, Data_Fine) 
                  VALUES (?, ?, ?, ?, ?, ?)";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("sssdss", $discountId, $description, $type, $value, $startDate, $endDate);
        return $stmt->execute();
    }

    // Check if discount is valid
    public function validateDiscount($discountId) {
        $query = "SELECT * FROM SCONTO 
                  WHERE ID_Sconto = ? 
                  AND Data_Inizio <= NOW() 
                  AND Data_Fine >= NOW()";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $discountId);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc();
    }

    /*******************
     * NOTIFICATION QUERIES *
     *******************/

    // Create notification
    public function createNotification($type, $message, $email) {
        $query = "INSERT INTO NOTIFICA (TipoNotifica, Messaggio, Timestamp_Invio, Tipo, Email) 
                  VALUES (?, ?, NOW(), 'Unread', ?)";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("sss", $type, $message, $email);
        return $stmt->execute();
    }

    // Get user notifications
    public function getUserNotifications($email) {
        $query = "SELECT DISTINCT n.*
                  FROM NOTIFICA n 
                  WHERE n.Email = ?
                  ORDER BY n.Timestamp_Invio DESC";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param('s', $email);
        $stmt->execute();
        $result = $stmt->get_result();
        
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    // Create a stock notification when a product is back in stock
    public function createStockNotification($productId) {
        $query = "SELECT p.Nome
                 FROM PRODOTTO p 
                 WHERE p.ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $productId);
        $stmt->execute();
        $product = $stmt->get_result()->fetch_assoc();
        
        // Get users who have this item in their wishlist
        $wishlistQuery = "SELECT DISTINCT a.Email 
                         FROM aggiungere a 
                         WHERE a.ID_Prodotto = ?";
        $wishlistStmt = $this->db->prepare($wishlistQuery);
        $wishlistStmt->bind_param("i", $productId);
        $wishlistStmt->execute();
        $users = $wishlistStmt->get_result()->fetch_all(MYSQLI_ASSOC);

        foreach ($users as $user) {
            $message = "Great news! The product {$product['Nome']} [$productId] is back in stock";
            $this->createNotification(
                'Stock Product',
                $message,
                $user['Email']
            );
        }
    }

    // Create an order status notification
    public function createOrderNotification($orderId, $status) {
        $query = "SELECT o.Email, o.ID_Ordine 
                 FROM ORDINE o 
                 WHERE o.ID_Ordine = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $orderId);
        $stmt->execute();
        $order = $stmt->get_result()->fetch_assoc();
    
        $messages = [
            'placed' => "Your payment has been successfully processed and we are starting to prepare your order [$orderId]",
            'in progress' => "Your order [$orderId] is being processed and prepared for shipping",
            'shipped' => "Your order [$orderId] was handed over to the SDA express courier",
            'delivered' => "Your order [$orderId] has been delivered"
        ];
    
        if (isset($messages[$status])) {
            $this->createNotification(
                'Order Status',
                $messages[$status],
                $order['Email']
            );
        }
    }

    // Create a sale notification for specific products
    public function createSaleNotification($productId, $discountPercentage) {
        $query = "SELECT p.Nome, p.Marca 
                 FROM PRODOTTO p 
                 WHERE p.ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $productId);
        $stmt->execute();
        $product = $stmt->get_result()->fetch_assoc();

        // Get users who have this item in their wishlist
        $wishlistQuery = "SELECT DISTINCT a.Email 
                         FROM aggiungere a 
                         WHERE a.ID_Prodotto = ?";
        $wishlistStmt = $this->db->prepare($wishlistQuery);
        $wishlistStmt->bind_param("i", $productId);
        $wishlistStmt->execute();
        $users = $wishlistStmt->get_result()->fetch_all(MYSQLI_ASSOC);

        foreach ($users as $user) {
            $message = "Your favorite product {$product['Nome']} [$productId] is now {$discountPercentage}% off for a short time";
            $this->createNotification(
                'Flash Sale',
                $message,
                $user['Email']
            );
        }
    }

    // Create a cart reminder notification
    public function createCartReminderNotification($email) {
        $query = "SELECT c.ID_Carrello, c.Valore_Totale, COUNT(co.ID_Prodotto) as item_count 
                 FROM CARRELLO c 
                 JOIN comprendere co ON c.ID_Carrello = co.ID_Carrello 
                 WHERE c.Email = ? AND c.Valore_Totale > 0
                 GROUP BY c.ID_Carrello";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result()->fetch_assoc();
    
        if ($result && $result['item_count'] > 0) {
            $message = "You have " . $result['item_count'] . " items waiting in your cart! Complete your purchase to avoid missing out.";
            return $this->createNotification(
                'Cart Reminder',
                $message,
                $email
            );
        }
        return false;
    }

    // Create a review request notification
    public function createReviewRequestNotification($orderId) {
        $query = "SELECT o.Email, p.Nome, p.ID_Prodotto 
                 FROM ORDINE o 
                 JOIN PRODOTTO_ORDINE po ON o.ID_Ordine = po.ID_Ordine 
                 JOIN PRODOTTO p ON po.ID_Prodotto = p.ID_Prodotto 
                 WHERE o.ID_Ordine = ? 
                 AND NOT EXISTS (
                     SELECT 1 FROM RECENSIONE r 
                     WHERE r.ID_Prodotto = po.ID_Prodotto 
                     AND r.Email = o.Email
                 )";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $orderId);
        $stmt->execute();
        $items = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);

        foreach ($items as $item) {
            $message = "How do you like your {$item['Nome']} [{$item['ID_Prodotto']}]? Share your experience!";
            $this->createNotification(
                'Review Request',
                $message,
                $item['Email']
            );
        }
    }

    public function createAdminMessageNotification($email, $message) {
        $this->createNotification(
            'Admin Message',
            $message,
            $email
        );
    }

    // Mark notifications as read
    public function markNotificationsAsRead($email, $notificationIds = null) {
        $query = "UPDATE NOTIFICA SET Tipo = 'Read' WHERE Email = ?";
        $params = [$email];
        $types = "s";

        if ($notificationIds) {
            $query .= " AND ID_Notifica IN (" . str_repeat("?,", count($notificationIds) - 1) . "?)";
            $params = array_merge($params, $notificationIds);
            $types .= str_repeat("i", count($notificationIds));
        }

        $stmt = $this->db->prepare($query);
        $stmt->bind_param($types, ...$params);
        return $stmt->execute();
    }

    // Get unread notifications count
    public function getUnreadNotificationsCount($email) {
        $query = "SELECT COUNT(*) as count FROM NOTIFICA WHERE Email = ? AND Tipo = 'Unread'";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc()['count'];
    }

    /*******************
     * STATISTICS QUERIES *
     *******************/

    // Admin: Get revenue statistics
    public function getRevenueStats($startDate, $endDate) {
        $query = "SELECT DATE(Data_Ordine) as date, SUM(Costo_Totale) as revenue, COUNT(*) as orders 
                  FROM ORDINE 
                  WHERE Data_Ordine BETWEEN ? AND ? 
                  GROUP BY DATE(Data_Ordine) 
                  ORDER BY date";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $startDate, $endDate);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    // Admin: Get product statistics
    public function getProductStats() {
        $query = "SELECT p.ID_Prodotto, p.Nome, p.Marca, 
                         COUNT(DISTINCT po.ID_Ordine) as total_orders,
                         SUM(po.Quantita) as total_quantity,
                         AVG(r.Punteggio) as avg_rating
                  FROM PRODOTTO p
                  LEFT JOIN PRODOTTO_ORDINE po ON p.ID_Prodotto = po.ID_Prodotto
                  LEFT JOIN RECENSIONE r ON p.ID_Prodotto = r.ID_Prodotto
                  GROUP BY p.ID_Prodotto
                  ORDER BY total_orders DESC";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    /*******************
     * NEWSLETTER QUERIES *
     *******************/

    public function updateNewsletterPreference($email, $preference) {
        $query = "UPDATE UTENTE SET Preferenze_Newsletter = ? WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $preference, $email);
        return $stmt->execute();
    }

    public function getNewsletterSubscribers() {
        $query = "SELECT Email, Nome, Cognome FROM UTENTE WHERE Preferenze_Newsletter = 'Y'";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    /********************
     * AUTH QUERIES *
     ********************/

    // Check if user exists
    public function isUserRegistered($email) {
        $query = "SELECT Email FROM UTENTE WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        return $stmt->get_result()->num_rows > 0;
    }

    // User registration
    public function registerUser($email, $firstName, $lastName, $password, $newsletter, $phone = null) {
        try {
            $this->db->begin_transaction();
            
            /// Hash password
            $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
            
            // Insert new user
            $userQuery = "INSERT INTO UTENTE (Email, Nome, Cognome, Password, Telefono, Data_Registrazione, Preferenze_Newsletter, Ruolo) 
                        VALUES (?, ?, ?, ?, ?, NOW(), ?, 'Customer')";
            $userStmt = $this->db->prepare($userQuery);
            $userStmt->bind_param("sssssi", $email, $firstName, $lastName, $hashedPassword, $phone, $newsletter);
            $userStmt->execute();

            // Create cart for new user
            $cartQuery = "INSERT INTO CARRELLO (Email, Data_Modifica, Valore_Totale) 
                        VALUES (?, NOW(), 0)";
            $cartStmt = $this->db->prepare($cartQuery);
            $cartStmt->bind_param("s", $email);
            $cartStmt->execute();

            // Create wishlist for new user
            $wishlistQuery = "INSERT INTO WISHLIST (Email, Data_Modifica) VALUES (?, NOW())";
            $wishlistStmt = $this->db->prepare($wishlistQuery);
            $wishlistStmt->bind_param("s", $email);
            $wishlistStmt->execute();

            $this->db->commit();
            return true;
        } catch (Exception $e) {
            $this->db->rollback();
            throw $e;
        }
    }

    // User login
    public function loginUser($email, $password) {
        $query = "SELECT * FROM UTENTE WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows === 1) {
            $user = $result->fetch_assoc();
            if (password_verify($password, $user['Password'])) {
                return $user;
            }
        }
        return false;
    }

    // User login with Google
    public function loginWithGoogle($email, $firstName, $lastName, $googleId) {
        try {
            $this->db->begin_transaction();
            
            // Check if user exists
            $isRegistered = $this->isUserRegistered($email);
            
            if (!$isRegistered) {
                // User does not exist, register first
                // Generate a random password since it won't be used (auth is via Google)
                $randomPassword = bin2hex(random_bytes(8));
                // Set newsletter preference to No by default
                $newsletter = 'N';
                
                // Register the user
                $userQuery = "INSERT INTO UTENTE (Email, Nome, Cognome, Password, Data_Registrazione, Preferenze_Newsletter, Ruolo) 
                            VALUES (?, ?, ?, ?, NOW(), ?, 'Customer')";
                $userStmt = $this->db->prepare($userQuery);
                $hashedPassword = password_hash($randomPassword, PASSWORD_DEFAULT);
                $userStmt->bind_param("sssss", $email, $firstName, $lastName, $hashedPassword, $newsletter);
                $userStmt->execute();

                // Inserisci nella tabella UTENTE_OAUTH
                $oauthQuery = "INSERT INTO UTENTE_OAUTH (Provider, Provider_UserID, Data_Link, Email) 
                            VALUES ('Google', ?, NOW(), ?)";
                $oauthStmt = $this->db->prepare($oauthQuery);
                $oauthStmt->bind_param("ss", $googleId, $email);
                $oauthStmt->execute();

                // Create cart for new user
                $cartQuery = "INSERT INTO CARRELLO (Email, Data_Modifica, Valore_Totale) 
                            VALUES (?, NOW(), 0)";
                $cartStmt = $this->db->prepare($cartQuery);
                $cartStmt->bind_param("s", $email);
                $cartStmt->execute();

                // Create wishlist for new user
                $wishlistQuery = "INSERT INTO WISHLIST (Email, Data_Modifica) VALUES (?, NOW())";
                $wishlistStmt = $this->db->prepare($wishlistQuery);
                $wishlistStmt->bind_param("s", $email);
                $wishlistStmt->execute();
            } else {
                // Verifica se esiste già un record OAuth per questo utente
                $checkOauthQuery = "SELECT 1 FROM UTENTE_OAUTH WHERE Email = ? AND Provider = 'Google'";
                $checkOauthStmt = $this->db->prepare($checkOauthQuery);
                $checkOauthStmt->bind_param("s", $email);
                $checkOauthStmt->execute();
                $oauthExists = $checkOauthStmt->get_result()->num_rows > 0;
                
                if ($oauthExists) {
                    // Aggiorna il Provider_UserID se necessario
                    $updateOauthQuery = "UPDATE UTENTE_OAUTH SET Provider_UserID = ? WHERE Email = ? AND Provider = 'Google'";
                    $updateOauthStmt = $this->db->prepare($updateOauthQuery);
                    $updateOauthStmt->bind_param("ss", $googleId, $email);
                    $updateOauthStmt->execute();
                } else {
                    // Inserisci nuovo record OAuth
                    $insertOauthQuery = "INSERT INTO UTENTE_OAUTH (Provider, Provider_UserID, Data_Link, Email) 
                                    VALUES ('Google', ?, NOW(), ?)";
                    $insertOauthStmt = $this->db->prepare($insertOauthQuery);
                    $insertOauthStmt->bind_param("ss", $googleId, $email);
                    $insertOauthStmt->execute();
                }
            }
            
            // Get user data for session
            $query = "SELECT * FROM UTENTE WHERE Email = ?";
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $result = $stmt->get_result();
            
            $this->db->commit();
            
            if ($result->num_rows === 1) {
                return $result->fetch_assoc();
            }
            
            return false;
            
        } catch (Exception $e) {
            $this->db->rollback();
            error_log("Error in loginWithGoogle: " . $e->getMessage());
            throw $e;
        }
    }

    // Change password
    public function changePassword($email, $password) {
        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
        $query = "UPDATE UTENTE SET Password = ? WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $hashedPassword, $email);
        return $stmt->execute();
    }

    // Get user profile
    public function getUserProfile($email) {
        $query = "SELECT Email, Nome, Cognome, Telefono, Data_Registrazione, Preferenze_Newsletter, Ruolo 
                FROM UTENTE 
                WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc();
    }

     // Set user image
    public function updateUserImage($email, $imgUrl) {
        $query = "UPDATE UTENTE SET URL_Foto = ? WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $imgUrl, $email);
        return $stmt->execute();
    }

    // Get user address
    public function getUserAddress($email){
        $query = "SELECT * FROM INDIRIZZO WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }
    
    // Add new user address
    public function addUserAddress($email, $via, $civico, $cap, $citta, $provincia, $nazione, $predefinito){
        try{
            $this->db->begin_transaction();

            // when adding a new default address, check if there is already another default address
            if($predefinito == 1){ 
                $queryAlreadyDefaultAddress = "SELECT * FROM INDIRIZZO WHERE Predefinito = 1 and Email=?";
                $stmtCheck = $this->db->prepare($queryAlreadyDefaultAddress);
                $stmtCheck->bind_param("s", $email);
                $stmtCheck->execute();

                if ($stmtCheck->get_result()->num_rows > 0) { // if there is another default address, set it as not default
                    $queryRemoveOtherDefaultAddress = "UPDATE INDIRIZZO SET Predefinito=0 WHERE Email=?";
                    $stmtUpdate = $this->db->prepare($queryRemoveOtherDefaultAddress);
                    $stmtUpdate->bind_param("s", $email);
                    $stmtUpdate->execute();
                }
            }

            // when adding a new non default address, previous check is not required
            $query = "INSERT INTO INDIRIZZO (Email, Via, NumeroCivico, CAP, Citta, Provincia, Nazione, Predefinito) VALUES (?,?,?,?,?,?,?,?)";
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("sssssssi", $email, $via, $civico, $cap, $citta, $provincia, $nazione, $predefinito);
            $stmt->execute();

            $this->db->commit();
            return true;
        } catch (Exception $e) {
            $this->db->rollback();
            return false;
        }
    }
    
    // Remove user address
    public function removeUserAddress($email, $via, $civico, $cap, $citta){
        $query = "DELETE FROM INDIRIZZO WHERE Email = ? AND Via= ? AND NumeroCivico= ? AND CAP= ? AND Citta= ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ssiis", $email, $via, $civico, $cap, $citta);
        return $stmt->execute();
    }

     // Get user payment methods
    public function getUserPayMethods($email){
        $query = "SELECT * FROM METODO_PAGAMENTO WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }
    
    // Add new user payment method
    public function addUserPayMethod($userEmail, $paypalEmail, $creditCardBrand, $creditCardLast4, $creditCardExpMonth, $creditCardExpYear){
        $query = "INSERT INTO METODO_PAGAMENTO(Email, CreditCard_brand, CreditCard_Last4, CreditCard_ExpMonth, CreditCard_ExpYear, PayPal_Email) VALUES (?,?,?,?,?,?)";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("sssiis", $userEmail, $creditCardBrand, $creditCardLast4, $creditCardExpMonth, $creditCardExpYear, $paypalEmail);
        return $stmt->execute();
    }
    
    // Remove user payment method
    public function removeUserPayMethod($id){
        $query = "DELETE FROM METODO_PAGAMENTO WHERE Id = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }

    /*******************
     * WISHLIST QUERIES *
     *******************/

    // Get user's wishlist items
    public function getWishlistItems($email) {
        $query = "SELECT p.* FROM PRODOTTO p
                  JOIN aggiungere a ON p.ID_Prodotto = a.ID_Prodotto
                  WHERE a.Email = ? ORDER BY p.Nome";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    // Add item to wishlist
    public function addToWishlist($email, $productId) {
        // Check if item already exists in wishlist
        $checkQuery = "SELECT 1 FROM aggiungere 
                      WHERE Email = ? AND ID_Prodotto = ?";
        $checkStmt = $this->db->prepare($checkQuery);
        $checkStmt->bind_param("ss", $email, $productId);
        $checkStmt->execute();
        if ($checkStmt->get_result()->num_rows > 0) {
            return true; // Item already in wishlist
        }

        // Add item to wishlist
        $query = "INSERT INTO aggiungere (Email, ID_Prodotto) 
                  VALUES (?, ?)";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $email, $productId);
        return $stmt->execute();
    }

    // Remove item from wishlist
    public function removeFromWishlist($email, $productId) {
        $query = "DELETE FROM aggiungere 
                  WHERE Email = ? AND ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $email, $productId);
        return $stmt->execute();
    }

    // Clear entire wishlist
    public function clearWishlist($email) {
        $query = "DELETE FROM aggiungere WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        return $stmt->execute();
    }

    // Get wishlist count
    public function getWishlistCount($email) {
        $query = "SELECT COUNT(*) as count FROM aggiungere WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc()['count'];
    }

    // Check if item is in wishlist
    public function isInWishlist($email, $productId) {
        $query = "SELECT 1 FROM aggiungere 
                  WHERE Email = ? AND ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $email, $productId);
        $stmt->execute();
        return $stmt->get_result()->num_rows > 0;
    }

    /*******************
     * CART QUERIES *
     *******************/

    // Add product to cart
    public function addToCart($email, $productId, $color, $size, $quantity = 1) {
        try {
            $this->db->begin_transaction();
            
            // First get the cart ID
            $cartInfo = $this->getCartByEmail($email);
            if (!$cartInfo) {
                return false;
            }
            $cartId = $cartInfo['ID_Carrello'];
            
            // Check if item already exists in cart
            $checkQuery = "SELECT Quantita FROM comprendere 
                        WHERE ID_Carrello = ? AND ID_Prodotto = ? 
                        AND Colore = ? AND Taglia = ?";
            $checkStmt = $this->db->prepare($checkQuery);
            $checkStmt->bind_param("iisd", $cartId, $productId, $color, $size);
            $checkStmt->execute();
            $existingItem = $checkStmt->get_result()->fetch_assoc();
            
            if ($existingItem) {
                // Update quantity if item exists
                $newQuantity = $existingItem['Quantita'] + $quantity;
                $updateQuery = "UPDATE comprendere 
                            SET Quantita = ? 
                            WHERE ID_Carrello = ? AND ID_Prodotto = ? 
                            AND Colore = ? AND Taglia = ?";
                $updateStmt = $this->db->prepare($updateQuery);
                $updateStmt->bind_param("iiisd", $newQuantity, $cartId, $productId, $color, $size);
                $updateStmt->execute();
            } else {
                // Add new item if it doesn't exist
                $insertQuery = "INSERT INTO comprendere (ID_Carrello, ID_Prodotto, Colore, Taglia, Quantita) 
                            VALUES (?, ?, ?, ?, ?)";
                $insertStmt = $this->db->prepare($insertQuery);
                $insertStmt->bind_param("iisdi", $cartId, $productId, $color, $size, $quantity);
                $insertStmt->execute();
            }
    
            // Update cart total value
            $updateTotalQuery = "UPDATE CARRELLO c 
                               SET c.Valore_Totale = (
                                   SELECT SUM(comp.Quantita * p.Prezzo)
                                   FROM comprendere comp
                                   JOIN PRODOTTO p ON comp.ID_Prodotto = p.ID_Prodotto
                                   WHERE comp.ID_Carrello = c.ID_Carrello
                               )
                               WHERE c.ID_Carrello = ?";
            $updateTotalStmt = $this->db->prepare($updateTotalQuery);
            $updateTotalStmt->bind_param("i", $cartId);
            $updateTotalStmt->execute();
    
            $this->db->commit();
            return true;
        } catch (Exception $e) {
            $this->db->rollback();
            return false;
        }
    }
    
     // Remove item from cart
    public function removeFromCart($email, $productId, $color, $size) {
        // First get the cart ID
        $cartInfo = $this->getCartByEmail($email);
        if (!$cartInfo) {
            return false;
        }
        $cartId = $cartInfo['ID_Carrello'];
        
        $query = "DELETE FROM comprendere 
                  WHERE ID_Carrello = ? AND ID_Prodotto = ? 
                  AND Colore = ? AND Taglia = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("iisd", $cartId, $productId, $color, $size);
        return $stmt->execute();
    }

    // Adjust item quantity in cart
    public function adjustCartQuantity($cartId, $productId, $color, $size, $quantity) {
        $query = "UPDATE comprendere 
                  SET Quantita = ? 
                  WHERE ID_Carrello = ? AND ID_Prodotto = ? 
                  AND Colore = ? AND Taglia = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("iiisd", $quantity, $cartId, $productId, $color, $size);
        return $stmt->execute();
    }

    // Update product size in cart
    public function updateSizeOnly($cartId, $productId, $color, $oldSize, $newSize) {
        $stmt = $this->db->prepare(
            "UPDATE comprendere 
                SET Taglia = ? 
                WHERE ID_Carrello = ? AND ID_Prodotto = ? 
                AND Colore = ? AND Taglia = ?"
        );
        $stmt->bind_param("diisd", $newSize, $cartId, $productId, $color, $oldSize);
        return $stmt->execute();
    }
    
    // Update product color and size in cart
    public function updateSizeAndColor($cartId, $productId, $oldColor, $oldSize, $newColor, $newSize) {
        $stmt = $this->db->prepare(
            "UPDATE comprendere 
                SET Colore = ?, Taglia = ? 
                WHERE ID_Carrello = ? AND ID_Prodotto = ? 
                AND Colore = ? AND Taglia = ?"
        );
        $stmt->bind_param("sdiisd", $newColor, $newSize, $cartId, $productId, $oldColor, $oldSize);
        return $stmt->execute();
    }

    // Update item color in cart
    public function updateCartItemColor($cartId, $productId, $oldColor, $newColor, $size) {
        $query = "UPDATE comprendere 
                  SET Colore = ? 
                  WHERE ID_Carrello = ? AND ID_Prodotto = ? 
                  AND Taglia = ? AND Colore = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("siids", $newColor, $cartId, $productId, $size, $oldColor);
        return $stmt->execute();
    }

    // Get all Colors of a product size
    public function getColorsBySize($productId, $size) {
        $query = "SELECT DISTINCT Colore 
                  FROM VARIANTE 
                  WHERE ID_Prodotto = ? 
                  AND Taglia = ? 
                  AND Quantita > 0 
                  ORDER BY Colore";
                  
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $productId, $size);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    // Get all Sizes of a product color
    public function getSizesByColor($productId, $color) {
        $query = "SELECT DISTINCT v.Taglia, v.Quantita 
                FROM VARIANTE v 
                WHERE v.ID_Prodotto = ? 
                AND v.Colore = ? 
                AND v.Quantita > 0 
                ORDER BY v.Taglia";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("is", $productId, $color);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    // Get quantity of a product variant
    public function getQuantity($productId, $color, $size) {
        $query = "SELECT Quantita FROM VARIANTE 
                  WHERE ID_Prodotto = ? AND Colore = ? AND Taglia = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param('iss', $productId, $color, $size);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc()['Quantita'] ?? 0;
    }

    // Get all Colors of a product
    public function getProductColors($productId) {
        $query = "SELECT DISTINCT v.Colore 
                FROM VARIANTE v 
                WHERE v.ID_Prodotto = ? 
                AND v.Quantita > 0 
                ORDER BY v.Colore";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $productId);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    // Get all Sizes of a product
    public function getProductSizes($productId) {
        $query = "SELECT DISTINCT v.Taglia 
                FROM VARIANTE v 
                WHERE v.ID_Prodotto = ? 
                AND v.Quantita > 0 
                ORDER BY v.Taglia";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $productId);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    // Get max product quantity
    public function getProductMaxQuantity($productId, $color, $size) {
        $query = "SELECT Quantita FROM VARIANTE 
                  WHERE ID_Prodotto = ? AND Colore = ? AND Taglia = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("isd", $productId, $color, $size);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc()['Quantita'];
    }

    // Get all items in cart
    // Get all items in cart
    public function getCartItems($cartId) {
        $query = "SELECT c.*, p.Prezzo 
                    FROM comprendere c LEFT JOIN PRODOTTO p ON c.ID_Prodotto = p.ID_Prodotto
                    WHERE c.ID_Carrello = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $cartId);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    // Get cart by user email
    public function getCartByEmail($email) {
        $query = "SELECT * FROM CARRELLO WHERE Email = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
    }

    // Modify cart total value
    public function modifyCartTotalValue($cartId, $newTotal) {
        $query = "UPDATE CARRELLO 
            SET Valore_Totale = ? WHERE ID_Carrello = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ii", $newTotal, $cartId);
        return $stmt->execute();
    }

    // Check promo code is valid
    public function checkPromoCode($code) {
        $query = "SELECT ID_Sconto, Valore, TipoSconto 
                  FROM SCONTO 
                  WHERE ID_Sconto = ? 
                  AND CURRENT_DATE BETWEEN Data_Inizio AND Data_Fine";
        
        $stmt = $this->db->prepare($query);
        $stmt->bind_param('s', $code);
        $stmt->execute();
        $result = $stmt->get_result();
        
        return $result->fetch_assoc();
    }

    // Get Abandoned Carts
    public function getAbandonedCarts($threshold) {
        $query = "SELECT DISTINCT c.Email, c.Data_Modifica 
                 FROM CARRELLO c 
                 JOIN comprendere co ON c.ID_Carrello = co.ID_Carrello 
                 WHERE c.Valore_Totale > 0 
                 AND TIMESTAMPDIFF(HOUR, c.Data_Modifica, NOW()) >= ?";
    
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $threshold);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    /*********************
     * REVIEW FUNCTIONS *
     *********************/

    // Add new review
    public function addReview($email, $productId, $rating, $comment) {
        // Check if user has already reviewed this product
        $checkQuery = "SELECT 1 FROM RECENSIONE 
                    WHERE Email = ? AND ID_Prodotto = ?";
        $checkStmt = $this->db->prepare($checkQuery);
        $checkStmt->bind_param("ss", $email, $productId);
        $checkStmt->execute();
        
        if ($checkStmt->get_result()->num_rows > 0) {
            // Update existing review
            $query = "UPDATE RECENSIONE 
                    SET Punteggio = ?, Descrizione = ?, Data_Recensione = NOW() 
                    WHERE Email = ? AND ID_Prodotto = ?";
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("isss", $rating, $comment, $email, $productId);
        } else {
            // Add new review
            $query = "INSERT INTO RECENSIONE (Email, ID_Prodotto, Punteggio, Descrizione, Data_Recensione) 
                    VALUES (?, ?, ?, ?, NOW())";
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("ssis", $email, $productId, $rating, $comment);
        }
        
        return $stmt->execute();
    }

    // Get product reviews
    public function getProductReviews($productId) {
        $query = "SELECT r.*, u.Nome, u.Cognome 
                FROM RECENSIONE r 
                JOIN UTENTE u ON r.Email = u.Email 
                WHERE r.ID_Prodotto = ? 
                ORDER BY r.Data_Recensione DESC";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $productId);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    // Delete review
    public function deleteReview($email, $productId) {
        $query = "DELETE FROM RECENSIONE 
                WHERE Email = ? AND ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $email, $productId);
        return $stmt->execute();
    }

    // Get average product rating
    public function getProductRating($productId) {
        $query = "SELECT AVG(Punteggio) as avg_rating, COUNT(*) as review_count 
                FROM RECENSIONE 
                WHERE ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("s", $productId);
        $stmt->execute();
        $result = $stmt->get_result()->fetch_assoc();
        return (float) $result['avg_rating'];
    }

    // Check if user can review (has purchased the product)
    public function canUserReview($email, $productId) {
        $query = "SELECT 1 
                FROM ORDINE o 
                JOIN PRODOTTO_ORDINE po ON o.ID_Ordine = po.ID_Ordine
                WHERE o.Email = ? AND po.ID_Prodotto = ?";
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $email, $productId);
        $stmt->execute();
        return $stmt->get_result()->num_rows > 0;
    }

    public function getPopularProducts() {
        $query = "SELECT p.* FROM PRODOTTO p 
            JOIN PRODOTTO_ORDINE po ON p.ID_Prodotto = po.ID_Prodotto 
            GROUP BY p.ID_Prodotto 
            ORDER BY SUM(po.Quantita) DESC";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    public function getDiscountedProducts() {
        $query = "SELECT DISTINCT p.* 
                FROM PRODOTTO p 
                JOIN (
                    SELECT ID_Prodotto, Prezzo 
                    FROM PRODOTTO_STORICO ps1
                    WHERE Data_Modifica = (
                        SELECT MAX(Data_Modifica) 
                        FROM PRODOTTO_STORICO ps2 
                        WHERE ps2.ID_Prodotto = ps1.ID_Prodotto
                    )
                ) latest_historic ON p.ID_Prodotto = latest_historic.ID_Prodotto
                WHERE p.Prezzo < latest_historic.Prezzo 
                ORDER BY p.Data_Aggiunta DESC";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    /*********************
     * ORDER FUNCTIONS *
     *********************/
    
    // Get all orders for a user
    public function getOrders($email, $lastAccess) {
        $query = "SELECT o.* 
                FROM ORDINE o 
                WHERE o.Email = ? AND o.Data_Ordine > ? 
                ORDER BY o.Data_Ordine DESC";
    
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("ss", $email, $lastAccess);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    // Retrieve order details by order ID
    public function getOrderDetails($orderId) {
        $query = "SELECT 
                    o.ID_Ordine,
                    o.Data_Ordine,
                    o.Costo_Totale,
                    o.Metodo_Pagamento,
                    o.Regalo,
                    o.Tipo_Spedizione AS Tipo,
                    
                    p.ID_Prodotto,
    
                    po.Prezzo_Acquisto,
                    po.Quantita,
                    po.Taglia,
                    po.Colore,
    
                    -- Subquery che determina se l'ordine è Delivered
                    (
                      SELECT COUNT(*) 
                      FROM Tracking_Spedizione ts
                      WHERE ts.ID_Ordine = o.ID_Ordine 
                        AND ts.Stato = 'Delivered'
                        AND ts.Arrivo_Effettivo IS NOT NULL
                    ) > 0 AS delivered_flag
    
                FROM ORDINE o 
                JOIN PRODOTTO_ORDINE po 
                      ON o.ID_Ordine = po.ID_Ordine 
                JOIN PRODOTTO p 
                      ON po.ID_Prodotto = p.ID_Prodotto 
                WHERE o.ID_Ordine = ? 
                ORDER BY o.Data_Ordine DESC, p.ID_Prodotto ASC";
    
        $stmt = $this->db->prepare($query);
        $stmt->bind_param("i", $orderId);
        $stmt->execute();
        $result = $stmt->get_result();

        $orders = [];
    
        while ($row = $result->fetch_assoc()) {
            $orderId = $row['ID_Ordine'];
    
            // Se l'ordine non è ancora in $orders, inizializzalo
            if (!isset($orders[$orderId])) {
                $orders[$orderId] = [
                    'ID_Ordine'       => $orderId,
                    'Data_Ordine'     => $row['Data_Ordine'],
                    'Costo_Totale'    => $row['Costo_Totale'],
                    'Metodo_Pagamento'=> $row['Metodo_Pagamento'],
                    'Regalo'          => $row['Regalo'],
                    'Tipo'            => $row['Tipo'],
                    // Salviamo il valore booleano una sola volta per ogni ordine:
                    'tracking_delivered' => (bool) $row['delivered_flag'],
                    'products'        => []
                ];
            }
    
            // Aggiungi le informazioni del prodotto
            $orders[$orderId]['products'][] = [
                'ID_Prodotto' => $row['ID_Prodotto'],
                'ID_Ordine'  => $orderId,
                'Prezzo'      => $row['Prezzo_Acquisto'],
                'Quantita'    => $row['Quantita'],
                'Taglia'      => $row['Taglia'],
                'Colore'      => $row['Colore']
            ];
        }
    
        return array_values($orders);
    }
    
    // Add new function to DatabaseHelper class
    public function placeOrder($email, $total, $paymentMethod, 
        $shippingType, $isGift = false, $giftFirstName = null, $giftLastName = null,
        $street, $city, $civic, $cap) {
        try {
            $this->db->begin_transaction();
            
            // Get cart info
            $cart = $this->getCartByEmail($email);
            
            // Create new order
            $query = "INSERT INTO ORDINE (Data_Ordine, Costo_Totale, Metodo_Pagamento, 
                Tipo_Spedizione, Regalo, NomeDestinatario, CognomeDestinatario, 
                Email, Spe_Email, Spe_Via, Spe_NumeroCivico, Spe_CAP, Spe_Citta) 
                VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            $stmt = $this->db->prepare($query);
            $stmt->bind_param("dssisssssiis", 
                $total, 
                $paymentMethod,
                $shippingType,
                $isGift,
                $giftFirstName,
                $giftLastName,
                $email,
                $email,
                $street,
                $civic,
                $cap,
                $city
            );
            $stmt->execute();
            $orderId = $this->db->insert_id;
            
            // Move items from cart to order
            $cartItems = $this->getCartItems($cart['ID_Carrello']);
            foreach ($cartItems as $item) {
                $query = "INSERT INTO PRODOTTO_ORDINE (ID_Prodotto, Colore, Taglia, Quantita, ID_Ordine, Prezzo_Acquisto) 
                        VALUES (?, ?, ?, ?, ?, ?)";
                $stmt = $this->db->prepare($query);
                $stmt->bind_param(
                    "isdiid",
                    $item['ID_Prodotto'],
                    $item['Colore'],
                    $item['Taglia'],
                    $item['Quantita'],
                    $orderId,
                    $item['Prezzo']
                );
                $stmt->execute();
            }
            
            // Create tracking entries
            $orderDate = new DateTimeImmutable();
            $isExpress = stripos($shippingType, 'express') !== false;
            $expectedDates = $this->calculateExpectedDates($orderDate, $isExpress);
            error_log(print_r($expectedDates, true));

            $trackingQuery = "INSERT INTO Tracking_Spedizione 
                (ID_Ordine, Posizione, Stato, Arrivo_Effettivo, Arrivo_Stimato, Timestamp_Aggiornamento) 
                VALUES (?, ?, ?, ?, ?, NOW())";

            // Store dates in variables for proper binding
            $currentTime = $orderDate->format('Y-m-d H:i:s');
            $inProgressDate = $expectedDates['in_progress']->format('Y-m-d H:i:s');
            $shippedDate = $expectedDates['shipped']->format('Y-m-d H:i:s');
            $deliveredDate = $expectedDates['delivered']->format('Y-m-d H:i:s');
            $nullDate = null;
           
            // Placed status
            $destination = $this->getUniversityLocation();
            $source = $this->getWarehouseLocation();
            $status = "Placed";
            $stmt = $this->db->prepare($trackingQuery);
            $stmt->bind_param(
                "issss",
                $orderId,
                $source,
                $status,
                $currentTime,
                $currentTime
            );
            $stmt->execute();

            // In Progress status
            $status = "In progress";
            $stmt = $this->db->prepare($trackingQuery);
            $stmt->bind_param(
                "issss",
                $orderId,
                $source,
                $status,
                $nullDate,
                $inProgressDate
            );
            $stmt->execute();

            // Shipped status
            $status = "Shipped";
            $stmt = $this->db->prepare($trackingQuery);
            $stmt->bind_param(
                "issss",
                $orderId,
                $source,
                $status,
                $nullDate,
                $shippedDate
            );
            $stmt->execute();

            // Delivered status
            $status = "Delivered";
            $stmt = $this->db->prepare($trackingQuery);
            $stmt->bind_param(
                "issss",
                $orderId,
                $destination,
                $status,
                $nullDate,
                $deliveredDate
            );
            $stmt->execute();
            
            // Clear cart
            $this->db->query("DELETE FROM comprendere WHERE ID_Carrello = " . $cart['ID_Carrello']);
            $this->db->query("UPDATE CARRELLO SET Valore_Totale = 0 WHERE ID_Carrello = " . $cart['ID_Carrello']);

            // Create Order Notification
            $this->createOrderNotification($orderId, 'placed');
            
            $this->db->commit();
            return $orderId;
            
        } catch (Exception $e) {
            $this->db->rollback();
            throw $e;
        }
    }

    // Add this helper function inside DatabaseHelper class
    private function calculateExpectedDates(DateTimeImmutable $orderDate, bool $isExpress): array 
    {
        $inProgress = $orderDate->modify('+1 hour');
        $shipped = $inProgress->modify('+1 weekday');
        $delivered = $shipped->modify($isExpress ? '+2 weekday' : '+5 weekday');

        return [
            'in_progress' => $inProgress,
            'shipped' => $shipped,
            'delivered' => $delivered
        ];
    }

    // Get University Location
    private function getUniversityLocation() {
        return "44.147613, 12.235779";
    }

    // Get Warehouse Location
    private function getWarehouseLocation() {
        return "39.082520, -94.582306";
    }

    /*********************
     * ADMIN FUNCTIONS *
     *********************/

    public function getCompletedOrders() {
        $query = "SELECT COUNT(DISTINCT o.ID_Ordine) as count 
                FROM ORDINE o 
                JOIN Tracking_Spedizione ts ON o.ID_Ordine = ts.ID_Ordine 
                WHERE ts.Stato = 'Delivered' 
                AND ts.Arrivo_Effettivo IS NOT NULL";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        return number_format($row['count']);
    }
    
    public function getPendingOrders() {
        $query = "SELECT COUNT(DISTINCT o.ID_Ordine) as count 
                FROM ORDINE o 
                JOIN Tracking_Spedizione ts ON o.ID_Ordine = ts.ID_Ordine 
                WHERE ts.Stato = 'Delivered' 
                AND ts.Arrivo_Effettivo IS NULL";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        return number_format($row['count']);
    }
    
    public function getTotalUsers() {
        $query = "SELECT COUNT(*) as count 
                FROM UTENTE 
                WHERE Ruolo = 'Customer'";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        return number_format($row['count']);
    }
    
    public function getBestSeller() {
        $query = "SELECT p.Nome as product_name, SUM(po.Quantita) as total_sold 
                FROM PRODOTTO p 
                JOIN PRODOTTO_ORDINE po ON p.ID_Prodotto = po.ID_Prodotto 
                JOIN ORDINE o ON po.ID_Ordine = o.ID_Ordine 
                JOIN Tracking_Spedizione ts ON o.ID_Ordine = ts.ID_Ordine 
                WHERE ts.Stato = 'Delivered'
                GROUP BY p.ID_Prodotto, p.Nome 
                ORDER BY total_sold DESC 
                LIMIT 1";
        $stmt = $this->db->prepare($query);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        return $row['product_name'] ?? 'No sales yet';
    }

    // Get all orders by status
    public function getOrdersByStatus($isDelivered = false) {
        $query = "SELECT DISTINCT
            o.ID_Ordine,
            o.Data_Ordine,
            o.Costo_Totale,
            o.Metodo_Pagamento,
            o.NomeDestinatario,
            o.CognomeDestinatario,
            p.ID_Prodotto,
            p.Nome,
            p.Genere,
            p.Prezzo,
            po.Prezzo_Acquisto,
            po.Colore,
            po.Taglia,
            po.Quantita,
            (
                SELECT ts2.Stato 
                FROM Tracking_Spedizione ts2 
                WHERE ts2.ID_Ordine = o.ID_Ordine 
                AND ts2.Arrivo_Effettivo IS NOT NULL 
                ORDER BY ts2.Timestamp_Aggiornamento DESC 
                LIMIT 1
            ) as CurrentStatus,
            EXISTS (
                SELECT 1 
                FROM Tracking_Spedizione ts3 
                WHERE ts3.ID_Ordine = o.ID_Ordine 
                AND ts3.Stato = 'Delivered' 
                AND ts3.Arrivo_Effettivo IS NOT NULL
            ) as IsDelivered
        FROM ORDINE o
        JOIN PRODOTTO_ORDINE po ON o.ID_Ordine = po.ID_Ordine
        JOIN PRODOTTO p ON po.ID_Prodotto = p.ID_Prodotto
        JOIN Tracking_Spedizione ts ON o.ID_Ordine = ts.ID_Ordine
        WHERE EXISTS (
            SELECT 1 
            FROM Tracking_Spedizione ts4 
            WHERE ts4.ID_Ordine = o.ID_Ordine 
            AND ts4.Arrivo_Effettivo IS NOT NULL
        )
        AND (? = 1 AND EXISTS (
                SELECT 1 
                FROM Tracking_Spedizione ts5 
                WHERE ts5.ID_Ordine = o.ID_Ordine 
                AND ts5.Stato = 'Delivered' 
                AND ts5.Arrivo_Effettivo IS NOT NULL
            ) 
            OR 
            ? = 0 AND NOT EXISTS (
                SELECT 1 
                FROM Tracking_Spedizione ts6 
                WHERE ts6.ID_Ordine = o.ID_Ordine 
                AND ts6.Stato = 'Delivered' 
                AND ts6.Arrivo_Effettivo IS NOT NULL
            )
        )
        ORDER BY o.Data_Ordine DESC";
    
        $stmt = $this->db->prepare($query);
        $delivered = $isDelivered ? 1 : 0;
        $stmt->bind_param("ii", $delivered, $delivered);
        $stmt->execute();
        $result = $stmt->get_result();
    
        $orders = [];
        while ($row = $result->fetch_assoc()) {
            if (!isset($orders[$row['ID_Ordine']])) {
                $orders[$row['ID_Ordine']] = [
                    'ID_Ordine' => $row['ID_Ordine'],
                    'Data_Ordine' => $row['Data_Ordine'],
                    'Status' => $row['CurrentStatus'],
                    'Costo_Totale' => $row['Costo_Totale'],
                    'Metodo_Pagamento' => $row['Metodo_Pagamento'],
                    'NomeDestinatario' => $row['NomeDestinatario'],
                    'CognomeDestinatario' => $row['CognomeDestinatario'],
                    'products' => []
                ];
            }
    
            $orders[$row['ID_Ordine']]['products'][] = [
                'ID_Prodotto' => $row['ID_Prodotto'],
                'Nome' => $row['Nome'],
                'Genere' => $row['Genere'],
                'Prezzo_Attuale' => $row['Prezzo'],
                'Prezzo_Acquisto' => $row['Prezzo_Acquisto'],
                'Colore' => $row['Colore'],
                'Taglia' => $row['Taglia'],
                'Quantita' => $row['Quantita']
            ];
        }
    
        return array_values($orders);
    }

    /*********************
     * USER INFO FUNCTIONS *
     *********************/

    // TBD
}

?>