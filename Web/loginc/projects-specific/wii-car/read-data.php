<?php
require_once 'mysql-load.php';


// Convert the date given in $_REQUEST to a string of appropriate format.
function convertDateToString($date)
{
	$return = new DateTime($date);
	// H is for 24-Hour clock format.
	return $return->format("Y-m-d H:i:s");
}


if (isset($_REQUEST["QUERY"]))
{
	$request = $_REQUEST["QUERY"];
	
	if ($request == "UPDATED_SESSIONS")
	{
		try
		{
			$date = convertDateToString($_REQUEST["ARG0"]);
		}
		catch (Exception $e)
		{
			die("error");
		}
	
		
		$query = "	SELECT DISTINCT tracking_session
					FROM tracking_data
					WHERE tracking_event>'$date';";
		
		$result = mysql_query($query, $server) or die(mysql_error());
		
		if (mysql_num_rows($result))
		{
			$i = 0;
			while ($row = mysql_fetch_assoc($result))
			{
				$data[$i] = $row["tracking_session"];
				$i++;
			}
			echo json_encode($data);
		}
		else
		{
			echo "";
		}
	}
	else if ($request == "NEW_SESSION_DATA")
	{
		$session = $_REQUEST["ARG0"];
		try
		{
			$date = convertDateToString($_REQUEST["ARG1"]);
		}
		catch (Exception $e)
		{
			die("error");
		}
		
		$query = "	SELECT tracking_event, tracking_longitude, tracking_latitude
					FROM tracking_data
					WHERE tracking_session='$session' AND tracking_event>'$date';";
		
		$result = mysql_query($query, $server) or die(mysql_error());
		if (mysql_num_rows($result))
		{
			$i = 0;
			$return[0] = $session;
			while ($row = mysql_fetch_assoc($result))
			{
				$data[$i] = $row;
				$i++;
			}
			$return[1] = $data;
			echo json_encode($return);
		}
		else
		{
			echo "";
		}
	}
	else
	{
		echo "invalid query";
	}
}
?>