package test.transportation;

import java.util.Map;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class contains all information about transportation order. It has two
 * Map objects, one for departure address details and the other for destination
 * address details. Each of them contains a set of fields named by the constants
 * ADDRESS_<X>, listed below. Each address map contains two fields for
 * coordinates as well.
 * 
 */
public class Order {

	static final String ADDRESS_COUNTRY = "country";
	static final String ADDRESS_ZIPCODE = "zipCode";
	static final String ADDRESS_CITY = "city";
	static final String ADDRESS_COUNTRY_CODE = "countryCode";
	static final String ADDRESS_COUNTRY_CODE_ALPHA_2 = "twoLetterCountryCode";
	static final String ADDRESS_STREET = "street";
	static final String ADDRESS_HOUSE = "houseNumber";

	private final String KEY_LATITUDE = "latitude";
	private final String KEY_LONGITUDE = "longitude";

	/**
	 * Identifiers to distinguish departure and destination addresses.
	 */
	static final int DEPARTURE_ID = 0, DESTINATION_ID = 1;

	/**
	 * Fields for storing departure and destination addresses of the order. Each
	 * map contains set of keys that refers to ADDRESS_<X> constants and has all
	 * information about address, which means two-letter and three-letter
	 * country code, city, zipcode, street name, house number and also latitude
	 * and longitude of that place. Some of fields could have empty values.
	 */
	private Map<String, Object> departureAddress, destinationAddress;

	/**
	 * Constructor for class Order.
	 * 
	 * @param departureAddress
	 *            A map with address details for departure point of the current
	 *            order.
	 * @param destinationAddress
	 *            A map with address details for destination point of the
	 *            current order.
	 */
	public Order(Map<String, Object> departureAddress,
			Map<String, Object> destinationAddress) {
		this.departureAddress = departureAddress;
		this.destinationAddress = destinationAddress;
	}

	/**
	 * Getter method for departure address details.
	 * 
	 * @return A map with the details of the departure point of the current
	 *         order.
	 */
	public Map<String, Object> getDepartureAddress() {
		return departureAddress;
	}

	/**
	 * Getter method for destination address details.
	 * 
	 * @return A map with the details of the destination point of the current
	 *         order.
	 */
	public Map<String, Object> getDestinationAddress() {
		return destinationAddress;
	}

	/**
	 * Setter method for departure coordinates. Puts latitude and longitude to
	 * the Map field with departure details.
	 * 
	 * @param coordinates
	 *            The LatLng object with contains two values of the type double
	 *            with the latitude and longitude coordinates of the departure
	 *            point.
	 * @return A map with the details of the departure point of the current
	 *         order.
	 */

	public void setDepartureCoordinates(LatLng coordinates) {
		if (coordinates != null) {
			departureAddress.put(KEY_LATITUDE, coordinates.latitude);
			departureAddress.put(KEY_LONGITUDE, coordinates.longitude);
		}
	}

	/**
	 * Setter method for destination coordinates. Puts latitude and longitude to
	 * the Map field with destination details.
	 * 
	 * @param coordinates
	 *            The LatLng object with contains two values of the type double
	 *            with the latitude and longitude coordinates of the destination
	 *            point.
	 * @return A map with the details of the destination point of the current
	 *         order.
	 */
	public void setDestinationCoordinates(LatLng coordinates) {
		if (coordinates != null) {
			destinationAddress.put(KEY_LATITUDE, coordinates.latitude);
			destinationAddress.put(KEY_LONGITUDE, coordinates.longitude);
		}
	}

	/**
	 * Getter method for departure coordinates. Gets latitude and longitude as
	 * the one LatLng object from the Map field with departure details.
	 * 
	 * @return A LatLng object with the two double coordinates of the current
	 *         order's departure place.
	 */
	public LatLng getDepartureCoordinates() {
		return new LatLng((Double) departureAddress.get(KEY_LATITUDE),
				(Double) departureAddress.get(KEY_LONGITUDE));
	}

	/**
	 * Getter method for destination coordinates. Gets latitude and longitude as
	 * the one LatLng object from the Map field with destination details.
	 * 
	 * @return A LatLng object with the two double coordinates of the current
	 *         order's destination place.
	 */
	public LatLng getDestinationCoordinates() {
		return new LatLng((Double) destinationAddress.get(KEY_LATITUDE),
				(Double) destinationAddress.get(KEY_LONGITUDE));
	}

	/**
	 * Checks if the order has already got the latitude and longitude fields for
	 * its departure and destination address.
	 * 
	 * @return true if coordinates exist, false otherwise.
	 */
	public boolean hasCoordinates() {
		if (departureAddress.containsKey(KEY_LATITUDE)
				&& destinationAddress.containsKey(KEY_LATITUDE)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates a string representation for current order which can be used when
	 * displaying address point on the map. The same for departure and
	 * destination address so has to get a flag to distinguish those.
	 * 
	 * @param placeType
	 *            A flag to distinguish departure and destination addresses, can
	 *            be DEPARTURE_ID or DESTINATION_ID.
	 * @return String object with the full address of the required point.
	 */
	public String addressToString(int placeType) {
		Map<String, Object> address = (placeType == DEPARTURE_ID) ? departureAddress
				: destinationAddress;

		StringBuilder builder = new StringBuilder();
		builder.append(address.get(Order.ADDRESS_HOUSE) + " ");
		builder.append(address.get(Order.ADDRESS_STREET) + " ");
		builder.append(address.get(Order.ADDRESS_CITY) + " ");
		builder.append(address.get(Order.ADDRESS_ZIPCODE) + " ");
		builder.append(address.get(Order.ADDRESS_COUNTRY_CODE));
		return builder.toString();
	}
}