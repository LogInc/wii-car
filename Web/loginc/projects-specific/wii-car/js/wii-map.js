// wii-map.js
// This script file goes with wii-map.php. This file contains functionality
// to initialize Google Maps API and use AJAX to read tracking data from the server
// and plot it on the map.

// AJAX object.
var ajax = new XMLHttpRequest();
// AJAX receive callback function.
var ajax_callback;
// Stores tracking session that need to be updated.
var pendingSessionUpdates = [];

// Google Maps object.
var map;
// Array to store the poly lines for the tracking data. 1 for each session.
var polylines = new Array();
// Arrays to store the centre of tracking data for each session.
var centersLat = new Array();
var centersLng = new Array();
// Circle and arrow symbols to display on the line plots on the map.
var mapsCircleSymbol;
var mapsArrowSymbol;

// Stores the timestamp the data was last updated. When requesting for updates
// this time stamp is sent to the server so that it can only send new data.
var lastUpdate = new Date("5 oct 1000, 06:01:02 GMT");
// var lastUpdate = new Date("14 dec 2014, 13:58:02 GMT");

// Called after the document is loaded.
function documentLoad() {
	ajax.onreadystatechange = ajaxCallback;
	try {
		initializeMaps();
		setupMapsAnimation();
	} catch (e) {
		console.log("ERROR: Unable to initialize Google Maps API");
	}

	ajaxGetUpdatedSessions();
	window.setInterval(ajaxGetUpdatedSessions, 2000)
}

// Initializes the Google Maps API.
function initializeMaps() {
	var options = {
		center : new google.maps.LatLng(31.5784, 74.3589),
		mapTypeId : google.maps.MapTypeId.ROADMAP,
		disableDefaultUI : true,
		zoomControl : true,
		scrollwheel : false,
		overviewMapControl : true,
		zoom : 18,
		// minZoom : 15,
		maxZoom : 20
	};

	map = new google.maps.Map(document.getElementById('map'), options);
	mapsCircleSymbol = {
		path : google.maps.SymbolPath.CIRCLE,
		scale : 10,
		fillColor : 'red',
		strokeColor : 'red',
		strokeWeight : 3
	};
	mapsArrowSymbol = {
		path : google.maps.SymbolPath.FORWARD_CLOSED_ARROW
	}
}

function setupMapsAnimation() {
	var scale = 0;
	var offset = 0;

	window.setInterval(function() {
		scale = (scale + 1) % 20;
		offset = (offset + 1) % 100;
		mapsCircleSymbol.scale = scale;

		for (i in polylines) {
			var icons = polylines[i].get('icons');
			icons[0].offset = offset + '%';
			icons[1].scaledSize = scale + '%';
			polylines[i].set('icons', icons);
		}
	}, 50);
}

// Called when tracking data for a particular session is received from the
// server.
function updateMap(session, data) {
	console.log("Updating Map, Session: " + session);

	var centerX = 0.0;
	var centerY = 0.0;
	var i = 0;

	try {
		if (!polylines[session]) {
			polylines[session] = new google.maps.Polyline({
				map : map,
				geodesic : true,
				strokeColor : '#0000FF',
				strokeOpacity : 0.7,
				strokeWeight : 3,
				icons : [{
					icon : mapsArrowSymbol,
					offset : '0%',
					repeat : '50px'
				}, {
					icon : mapsCircleSymbol,
					offset : '100%',
					scaledSize : '10px'
				}]
			});
		}
	} finally {
	}

	for (coords in data) {
		var row = data[coords];
		var event = row["tracking_event"];
		var latitude = row["tracking_latitude"];
		var longitude = row["tracking_longitude"];

/* 		var html = "<tr>";
		html += "<td>" + session + "</td>";
		html += "<td>" + event + "</td>";
		html += "<td>" + latitude + "</td>";
		html += "<td>" + longitude + "</td>";
		html += "</tr>";
		$("#sessions").children().last().append(html);
*/

		centerX += parseFloat(latitude);
		centerY += parseFloat(longitude);
		i++;

		try {
			polylines[session].getPath().push(
					new google.maps.LatLng(latitude, longitude));
		} finally {
		}
	}

	// Calculate the center point of the session.
	centersLat[session] = centerX / i;
	centersLng[session] = centerY / i;
}

// Sends requests to the server if any session needs to be updated.
function performPendingUpdate() {
	if (pendingSessionUpdates.length) {
		console.log("Pending Session Updates: " + pendingSessionUpdates);
		ajaxGetNewSessionData(pendingSessionUpdates[0]);
	} else {
		// No updates are pending implies the tracking data is up to date.
		// We get here only if no session needs to be updated.
		lastUpdate = new Date();
	}
}

// AJAX asynchronous request for updated sessions since last update.
function ajaxGetUpdatedSessions() {
	var html = "read-data.php?QUERY=UPDATED_SESSIONS&ARG0="
			+ lastUpdate.toUTCString();
	ajaxRequest(html, ajaxUpdatedSessionsReceived);
}

// AJAX asynchronous request for updated session data since last update.
function ajaxGetNewSessionData(session) {
	console.log("Last Update: " + lastUpdate.toUTCString());
	var html = "read-data.php?QUERY=NEW_SESSION_DATA&ARG0=" + session;
	html += "&ARG1=" + lastUpdate.toUTCString();
	ajaxRequest(html, ajaxNewSessionDataReceived);
}

// Callback function for receving the list of sessions need to be updated.
function ajaxUpdatedSessionsReceived() {
	var updated_sessions = eval(ajax.responseText);

	for (i in updated_sessions) {
		pendingSessionUpdates.unshift(updated_sessions[i]);
	}

	pendingSessionUpdates.sort();
	performPendingUpdate();
}

// Callback function for receiving the new session data.
function ajaxNewSessionDataReceived() {
	var result = eval(ajax.responseText);
	if (!result)
		return;

	// Result returned from server contains 2 outer elements.
	// 1st is the session no. 2nd is the data.

	var session = result[0];
	// Remove this session from the pending list.
	pendingSessionUpdates.shift(session);
	updateMap(session, result[1]);

	console.log("Session Updated: " + session);

	// Continue updating the remaining sessions. Request is only sent for
	// 1 session at a time.
	performPendingUpdate();
}

function ajaxRequest(url, callback) {
	ajax_callback = callback;
	ajax.open("GET", url, true);
	ajax.send();
}

function ajaxCallback() {
	// 4: request finished and response ready.
	// 200: OK.
	if (ajax.readyState == 4 && ajax.status == 200) {
		ajax_callback();
	}
}