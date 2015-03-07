<!DOCTYPE html>

<html lang="en">
<head>
<title>Wii-Car Tracking Data</title>

<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCJWr6Ky8Pr5HP3q1iu9VVucAM6TDXw60I"></script>
<script type="text/javascript" src="/projects-specific/wii-car/js/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="/projects-specific/wii-car/js/wii-map.js"></script>


<script type="text/javascript">
window.addEventListener("load", documentLoad);
</script>

<style>
#map {
	height: 500px;
	width: 95%;
	padding: 10px;
	margin-right: auto;
	margin-left: auto;
}

table, thead, tr, td {
	width: 700px;
	border: solid 2px;
	border-collapse: collapse;
	margin-left: auto;
	margin-right: auto;
	text-align: center;
}

thead {
	font-weight: bold;
	background-color: aqua;
	font-size: 120%;
}
</style>
</head>

<body>
	<div>
	<!--
		<table id="sessions">
			<thead>
				<tr>
					<td>Session</td>
					<td>Time Stamp</td>
					<td>Latitude</td>
					<td>Longitude</td>
				</tr>
			</thead>

			<tbody>
			</tbody>
		</table>
		-->
	</div>

	<div id="map"></div>
</body>

</html>