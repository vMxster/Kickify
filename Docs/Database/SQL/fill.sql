-- Inserimento Utente ADMIN
INSERT INTO UTENTE (Email, Nome, Cognome, Password, Data_Registrazione, Preferenze_Newsletter, Ruolo) VALUES ("tomassimartin@hotmail.com", "Kickify", "Admin", "$2y$10$l4Llx3ynDp0HbOYyADATEOBmFUNaELDU0LI4VYIncpajnTYJCfdFa", NOW(), 0, "Admin");

-- Inserimento Stato Notifica
INSERT INTO STATO_NOTIFICA (Tipo, Descrizione) VALUES
    	('Read', 'The notification has been read!'),
    	('Unread', 'The notification was not read!');

-- Aggiornamento dati nella tabella STATO_PRODOTTO
INSERT INTO STATO_PRODOTTO (Tipo, Descrizione) VALUES
    	('Available', 'Il prodotto è attualmente disponibile in magazzino'),
    	('Not Available', 'Il prodotto non è attualmente disponibile in magazzino'),
    	('Coming', 'Il prodotto è previsto in arrivo prossimamente');

-- Aggiornamento dati nella tabella NOTIFICA
INSERT INTO NOTIFICA (ID_Notifica, TipoNotifica, Messaggio, Timestamp_Invio, Tipo, Email) VALUES 	('1', 'Flash Sale', 'Your favorite product Ultraboost [1] is now 6% off for a short time!!', NOW(), 	'Unread', 'tomassimartin@hotmail.com');

-- Inserimento dati nella tabella PRODOTTO
INSERT INTO PRODOTTO (Nome, Descrizione, Marca, Tipo, Genere, Prezzo, Data_Aggiunta, Sta_Tipo) VALUES
    ('Ultraboost', 'Scarpa da corsa ad alte prestazioni', 'Adidas', 'Sneakers', 'Man', 180.00, '2025-01-08', 'Available'),
    ('Gazelle', 'Scarpa casual comoda', 'Adidas', 'Sneakers', 'Woman', 120.00, '2025-01-08', 'Available'),
    ('Adilette Aqua', 'Ciabatte comode e di tendenza', 'Adidas', 'Sliders', 'Kids', 40.00, '2025-01-08', 'Available'),
    ('Air Max 97', 'Scarpa sportiva iconica', 'Nike', 'Sneakers', 'Man', 200.00, '2025-01-08', 'Available'),
    ('Free RN', 'Scarpa da corsa flessibile', 'Nike', 'Sneakers', 'Woman', 120.00, '2025-01-08', 'Available'),
    ('Sunray Protect 2', 'Sandali per bambini leggeri', 'Nike', 'Sandals', 'Kids', 50.00, '2025-01-08', 'Available'),
    ('RS-X', 'Scarpa dal design futuristico', 'Puma', 'Sneakers', 'Man', 110.00, '2025-01-08', 'Available'),
    ('Cali', 'Scarpa casual retrò', 'Puma', 'Sneakers', 'Woman', 90.00, '2025-01-08', 'Available'),
    ('Suede XL', 'Scarpe comode e di tendenza', 'Puma', 'Sneakers', 'Kids', 35.00, '2025-01-08', 'Available'),
    ('Samba', 'Scarpe comode e di tendenza', 'Adidas', 'Sneakers', 'Man', 120.00, '2025-04-14', 'Available'),
    ('Cortez', 'Scarpe comode e di tendenza', 'Nike', 'Sneakers', 'Woman', 100.00, '2025-04-16', 'Available');

-- Aggiornamento dati nella tabella RECENSIONE
INSERT INTO RECENSIONE (`ID_Prodotto`, `Email`, `Punteggio`, `Descrizione`, `Data_Recensione`) VALUES ('1', 	'tomassimartin@hotmail.com', '4', 'Ottima Scarpa', NOW());

-- Inserimento dati nella tabella VARIANTE per UOMO
INSERT INTO VARIANTE (ID_Prodotto, Colore, Taglia, Quantita)
SELECT 
    p.ID_Prodotto,
    c.Colore,
    t.Taglia,
    30 as Quantita
FROM PRODOTTO p
CROSS JOIN (
    SELECT 'Blue' as Colore UNION
    SELECT 'Purple' UNION
    SELECT 'Red' UNION
    SELECT 'Green' UNION
    SELECT 'White' UNION
    SELECT 'Black'
) c
CROSS JOIN (
    SELECT 37 as Taglia UNION
    SELECT 38 UNION
    SELECT 39 UNION
    SELECT 40 UNION
    SELECT 41 UNION
    SELECT 42 UNION
    SELECT 43 UNION
    SELECT 44 UNION
    SELECT 45
) t
WHERE p.Genere = 'Man';

-- Inserimento dati nella tabella VARIANTE per DONNA
INSERT INTO VARIANTE (ID_Prodotto, Colore, Taglia, Quantita)
SELECT 
    p.ID_Prodotto,
    c.Colore,
    t.Taglia,
    30 as Quantita
FROM PRODOTTO p
CROSS JOIN (
    SELECT 'Blue' as Colore UNION
    SELECT 'Purple' UNION
    SELECT 'Red' UNION
    SELECT 'Green' UNION
    SELECT 'White' UNION
    SELECT 'Black'
) c
CROSS JOIN (
    SELECT 37 as Taglia UNION
    SELECT 38 UNION
    SELECT 39 UNION
    SELECT 40 UNION
    SELECT 41 UNION
    SELECT 42 UNION
    SELECT 43 UNION
    SELECT 44 UNION
    SELECT 45
) t
WHERE p.Genere = 'Woman';

-- Inserimento dati nella tabella VARIANTE per BAMBINO
INSERT INTO VARIANTE (ID_Prodotto, Colore, Taglia, Quantita)
SELECT 
    p.ID_Prodotto,
    c.Colore,
    t.Taglia,
    30 as Quantita
FROM PRODOTTO p
CROSS JOIN (
    SELECT 'Blue' as Colore UNION
    SELECT 'Purple' UNION
    SELECT 'Red' UNION
    SELECT 'Green' UNION
    SELECT 'White' UNION
    SELECT 'Black'
) c
CROSS JOIN (
    SELECT 28 as Taglia UNION
    SELECT 29 UNION
    SELECT 30 UNION
    SELECT 31 UNION
    SELECT 32 UNION
    SELECT 33 UNION
    SELECT 34 UNION
    SELECT 35 UNION
    SELECT 36
) t
WHERE p.Genere = 'Kids';
