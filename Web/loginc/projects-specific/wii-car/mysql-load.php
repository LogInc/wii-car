<?php
$server = mysql_connect('localhost', 'wii_car_logger', 'chateau') or die(mysql_error($server));
mysql_select_db('wii_car_logger') or die (mysql_error($server));
?>