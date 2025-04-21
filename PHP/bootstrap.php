<?php

session_start();
require_once("Database/database.php");
$dbh = new DatabaseHelper("localhost", "kickify", "", "my_kickify", 3306);

?>