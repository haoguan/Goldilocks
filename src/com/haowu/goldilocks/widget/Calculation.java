package com.haowu.goldilocks.widget;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.google.android.gms.maps.model.LatLng;

public class Calculation {

	/* Returns distance in meters */
	Document doc;
	GMapV2Direction gmap = new GMapV2Direction();
	ArrayList<LatLng> arr = new ArrayList<LatLng>();
	
	double halfDistance = 0;

	public Calculation(Document doc) {
		this.doc = doc;
		
	}

	public double calcDistance(double lat1, double lat2, double lon1,
			double lon2) {
		int R = 6371; // km
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		lon1 = Math.toRadians(lon1);
		lon2 = Math.toRadians(lon2);
		double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(lon2 - lon1))
				* R;
		return d * 1000;
	}

	public LatLng calcStep() {
		NodeList nl1, nl2, nl3, nl4;

		nl1 = doc.getElementsByTagName("distance");
		nl3 = doc.getElementsByTagName("step");
		Node nLastDistance = nl1.item(nl1.getLength() - 1);
		nl2 = nLastDistance.getChildNodes();
		Node ntotalDistance = nl2.item(getNodeIndex(nl2, "value"));
		double fullDistance = Double.parseDouble(ntotalDistance
				.getTextContent());

		halfDistance = fullDistance / 2;
		double totalDistance = 0;
		int criticalPath = 0;
		double distancePrime = 0;

		for (int i = 0; i < nl3.getLength(); i++) {
			nl4 = nl3.item(i).getChildNodes();
			NodeList nDistance = nl4.item(getNodeIndex(nl4, "distance"))
					.getChildNodes();
			Node nValue = nDistance.item(getNodeIndex(nDistance, "value"));
			distancePrime = Double.parseDouble(nValue.getTextContent());

			totalDistance += distancePrime;
			if (totalDistance >= halfDistance) {
				criticalPath = i;
				break;
			}
		}
		double distanceLeft = distancePrime - (totalDistance - halfDistance);
		return calcPath(distanceLeft, nl3.item(criticalPath).getChildNodes());
		// return nl1.item(criticalPath);
	}

	public LatLng calcPath(double distance_left,
			NodeList criticalStep) {
		double lat1, lon1, lat2, lon2;
		double totalDistance = 0;
		double distance = 0;
		int criticalPath = 0;
		NodeList nPolyline = criticalStep.item(
				getNodeIndex(criticalStep, "polyline")).getChildNodes();
		Node nPaths = nPolyline.item(getNodeIndex(nPolyline, "points"));
		
		ArrayList<LatLng> paths = gmap.decodePoly(nPaths.getTextContent());
		for (int i = 0; i < paths.size() - 1; i++) {
			lat1 = paths.get(i).latitude;
			lon1 = paths.get(i).longitude;
			lat2 = paths.get(i + 1).latitude;
			lon2 = paths.get(i + 1).longitude;
			arr.add(new LatLng(lat1, lon1));

			
			distance = calcDistance(lat1, lat2, lon1, lon2);
			totalDistance += distance;
			if (totalDistance >= distance_left) {
				criticalPath = i;
				break;
			}
		}
		double distanceToMp = distance - (totalDistance - distance_left);
		return calcMidPoint(paths.get(criticalPath),
				paths.get(criticalPath + 1), distance, distanceToMp);

	}

	public LatLng calcMidPoint(LatLng start, LatLng end,
			double distance, double distancePrime) {
		double startLat = start.latitude;
		double startLng = start.longitude;
		double endLat = end.latitude;
		double endLng = end.longitude;

		double y = endLng - startLng;
		double x = endLat - startLat;
		double hypo = Math.sqrt(y * y + x * x);
		double deg = Math.asin(y / hypo);

		double hypoPrime = (distancePrime * hypo) / distance;

		double yPrime = Math.sin(deg) * hypoPrime;
		double xPrime = Math.cos(deg) * hypoPrime;

		double finalX = startLat + xPrime;
		double finalY = startLng + yPrime;
		
		return new LatLng(finalX, finalY);
	}

	private int getNodeIndex(NodeList nl, String nodename) {
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().equals(nodename))
				return i;
		}
		return -1;
	}
	
	public double getHalfDistance() {
		return halfDistance;
	}
	public double metersToMiles(double meters){
		return meters * 0.000621371;
	}

}
