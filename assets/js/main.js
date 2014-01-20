var directionsService = new google.maps.DirectionsService();
var start= "Powell St, SF";
var end = "Union Square, SF";

function findDirections() {

	start = Goldilocks.getStartAddress();
	end = Goldilocks.getEndAddress();
		
	var request = {
		origin: start,
		destination: end,
		provideRouteAlternatives: true,
		travelMode: google.maps.DirectionsTravelMode.WALKING
	};
	
	directionsService.route(request, function(response, status) {
		if (status == google.maps.DirectionsStatus.OK) {
			alert(JSON.stringify(response));
		} else {
			console.log("Unable to get directionService information");
		}
	});
};


