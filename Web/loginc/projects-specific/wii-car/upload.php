<?php
	if (!isset($_GET['LATITUDE']) || !isset($_GET['LONGITUDE']))
		die('Invalid upload request');

	require_once 'mysql-load.php';
	
	$event = 'UTC_TIMESTAMP()';
	$latitude = $_GET['LATITUDE'];
	$longitude = $_GET['LONGITUDE'];
	
	$query = 'SELECT MAX(tracking_session) FROM tracking_data';
	$result = mysql_query($query, $server) or die(mysql_error());
	
	$session = mysql_result($result, 0);
	if (isset($_GET['FIRST'])) {
		$session++;
	}
	
	$query = "INSERT INTO tracking_data VALUES ($event, '$session', '$longitude', '$latitude');";
	mysql_query($query, $server) or die(mysql_error());
?>