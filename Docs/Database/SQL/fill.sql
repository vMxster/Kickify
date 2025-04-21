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

-- Aggiornamento dati nella tabella MESSAGGIO
INSERT INTO `messaggio` (`Email`, `Oggetto`, `Corpo`, `Timestamp_Invio`) VALUES ('tomassimartin@hotmail.com', 	'Risposta', 'Ciao, ho pensato a questo', NOW());

-- Aggiornamento dati nella tabella NOTIFICA
INSERT INTO `notifica` (`ID_Notifica`, `TipoNotifica`, `Messaggio`, `Timestamp_Invio`, `Tipo`, `Email`) VALUES 	('1', 'Flash Sale', 'Your favorite product Ultraboost [1] is now 6% off for a short time!!', NOW(), 	'Unread', 'tomassimartin@hotmail.com');

-- Inserimento dati nella tabella PRODOTTO
INSERT INTO PRODOTTO (Nome, Descrizione, Marca, Tipo, Genere, Prezzo, Data_Aggiunta, Sta_Tipo) VALUES
    ('Ultraboost', 'Scarpa da corsa ad alte prestazioni', 'Adidas', 'Sneakers', 'Man', 180.00, '2025-01-08', 'Available'),
    ('Cloudfoam Pure', 'Scarpa casual comoda', 'Adidas', 'Sneakers', 'Woman', 70.00, '2025-01-08', 'Available'),
    ('Adilette Aqua', 'Ciabatte comode e di tendenza', 'Adidas', 'Sliders', 'Kids', 40.00, '2025-01-08', 'Available'),
    ('Arishi V3', 'Scarpa da corsa leggera', 'New Balance', 'Sneakers', 'Man', 100.00, '2025-01-08', 'Available'),
    ('Fresh Foam 1080', 'Scarpa ammortizzata per lunghe corse', 'New Balance', 'Sneakers', 'Woman', 150.00, '2025-01-08', 'Available'),
    ('Slide', 'Ciabatte ortopediche', 'Hoka', 'Sliders', 'Woman', 80.00, '2025-01-08', 'Available'),
    ('Arizona Sandals', 'Sandali classici', 'Birkenstock', 'Sandals', 'Man', 100.00, '2025-01-08', 'Available'),
    ('Gizeh', 'Sandali eleganti', 'Birkenstock', 'Sandals', 'Woman', 90.00, '2025-01-08', 'Available'),
    ('Crocs', 'Sandali resistenti e leggeri', 'Crocs', 'Sandals', 'Kids', 40.00, '2025-01-08', 'Available'),
    ('Air Max', 'Scarpa sportiva iconica', 'Nike', 'Sneakers', 'Man', 200.00, '2025-01-08', 'Available'),
    ('Free RN', 'Scarpa da corsa flessibile', 'Nike', 'Sneakers', 'Woman', 120.00, '2025-01-08', 'Available'),
    ('Sunray Protect 2', 'Sandali per bambini leggeri', 'Nike', 'Sandals', 'Kids', 50.00, '2025-01-08', 'Available'),
    ('RS-X', 'Scarpa dal design futuristico', 'Puma', 'Sneakers', 'Man', 110.00, '2025-01-08', 'Available'),
    ('Cali', 'Scarpa casual retrò', 'Puma', 'Sneakers', 'Woman', 90.00, '2025-01-08', 'Available'),
    ('Suede XL', 'Scarpe comode e di tendenza', 'Puma', 'Sneakers', 'Kids', 35.00, '2025-01-08', 'Available'),
    ('Classic', 'Scarpa sportiva classica', 'Reebok', 'Sneakers', 'Man', 95.00, '2025-01-08', 'Available'),
    ('Club C', 'Scarpa casual di tendenza', 'Reebok', 'Sneakers', 'Woman', 85.00, '2025-01-08', 'Available'),
    ('Club C', 'Scarpe comode', 'Reebok', 'Sneakers', 'Kids', 50.00, '2025-01-08', 'Available'),
    ('All Star', 'Scarpa iconica in tela', 'Converse', 'Sneakers', 'Man', 75.00, '2025-01-08', 'Available'),
    ('Gel-Kayano', 'Scarpa da corsa stabile', 'Asics', 'Sneakers', 'Woman', 160.00, '2025-01-08', 'Available'),
    ('Lerond', 'Scarpa casual elegante', 'Lacoste', 'Sneakers', 'Man', 100.00, '2025-01-08', 'Available'),
    ('Cloud', 'Scarpa da corsa leggera', 'On', 'Sneakers', 'Woman', 130.00, '2025-01-08', 'Available'),
    ('Speedcross', 'Scarpa da trail running', 'Salomon', 'Sneakers', 'Man', 120.00, '2025-01-08', 'Available'),
    ('Fluff Yeah', 'Sandali soffici', 'UGG', 'Sandals', 'Woman', 100.00, '2025-01-08', 'Available'),
    ('Jazz Original', 'Scarpa retrò da corsa', 'Saucony', 'Sneakers', 'Man', 90.00, '2025-01-08', 'Available'),
    ('Old Skool', 'Scarpa da skate classica', 'Vans', 'Sneakers', 'Man', 60.00, '2025-01-08', 'Available'),
    ('Top', 'Sandali estivi', 'Havaianas', 'Sandals', 'Woman', 20.00, '2025-01-08', 'Available'),
    ('Banda', 'Scarpa sportiva casual', 'Kappa', 'Sneakers', 'Man', 50.00, '2025-01-08', 'Available'),
    ('HOVR', 'Scarpa da corsa ammortizzata', 'Under Armour', 'Sneakers', 'Woman', 110.00, '2025-01-08', 'Available'),
    ('N9000', 'Scarpa sportiva retrò', 'Diadora', 'Sneakers', 'Man', 95.00, '2025-01-08', 'Available');

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
