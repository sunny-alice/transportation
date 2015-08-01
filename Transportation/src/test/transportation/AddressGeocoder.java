package test.transportation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class uses Google Geocoding API to get the coordinates for all existing
 * orders.
 * 
 */
public class AddressGeocoder {

	/**
	 * Base URL for geocoding requests.
	 */
	private final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/";

	/**
	 * Main method of the class AddressGeocoder. Takes a list with the address
	 * details, requests the coordinates for each address and modifies the given
	 * list.
	 * 
	 * @param orderList
	 *            List of Order objects with the filled address fields.
	 */
	public void getAllCoordinates(List<Order> orderList) {
		for (Order order : orderList) {
			order.setDepartureCoordinates(getCoordinates(order
					.getDepartureAddress()));
			order.setDestinationCoordinates(getCoordinates(order
					.getDestinationAddress()));
		}
	}

	/**
	 * Gets coordinates for given address using geocoding.
	 * 
	 * @param address
	 *            Map with the all address fields filled with some data.
	 * @return LatLng object with coordinates of the given address.
	 */
	private LatLng getCoordinates(Map<String, Object> address) {
		String request = formAddressRequest(address);
		JSONObject jsonCoordinates = getJSONAddress(request);
		return parseCoordinates(jsonCoordinates);
	}

	/**
	 * This method forms a request to geocoding service using the given address.
	 * Encoding is used for address part of the request to avoid problems with
	 * the special symbols.
	 * 
	 * @param address
	 *            Map filled with address details.
	 * @return String object with request ready to use for geocoding service.
	 */
	private String formAddressRequest(Map<String, Object> address) {
		String[] address_components = { Order.ADDRESS_HOUSE,
				Order.ADDRESS_STREET, Order.ADDRESS_CITY,
				Order.ADDRESS_ZIPCODE, Order.ADDRESS_COUNTRY_CODE_ALPHA_2 };

		StringBuilder builder = new StringBuilder();

		for (String component : address_components) {
			String componentValue = (String) address.get(component);
			if (!componentValue.equals("")) {
				builder.append(componentValue + ",");
			}
		}
		builder.deleteCharAt(builder.length() - 1);

		String addressString = "";
		try {

			addressString = URLEncoder.encode(builder.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return GEOCODE_URL + "json?address=" + addressString;
	}

	/**
	 * Connects the google geocoding api with the given request to get the
	 * coordinates of the current address.
	 * 
	 * @param request
	 *            String object with the prepared request for geocoding.
	 * @return Object with the result data of the executed request in JSON
	 *         format. Sometimes Google returns status message
	 *         "OVER_QUERY_LIMIT" so it is needed to make a little delay and
	 *         then repeat the last request execution.
	 */
	private JSONObject getJSONAddress(String request) {
		StringBuilder addressBuilder = new StringBuilder();
		URLConnection urlConnection = null;

		try {
			URL addressUrl = new URL(request);
			urlConnection = addressUrl.openConnection();

			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				addressBuilder.append(line);
			}
			reader.close();
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}

		JSONObject jsonAddress = null;
		try {
			jsonAddress = new JSONObject(addressBuilder.toString());
			if (jsonAddress.get("status").equals("OVER_QUERY_LIMIT")) {
				Thread.sleep(200);
				jsonAddress = getJSONAddress(request);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return jsonAddress;
	}

	/**
	 * Parses address object given in JSON format and gets coordinates for required place from it.
	 * @param jsonAddress Object in JSON format that contains result of the geocoding request.
	 * @return LatLng object with coordinates extracted from the given JSON object.
	 */
	private LatLng parseCoordinates(JSONObject jsonAddress) {
		try {
			JSONArray results = jsonAddress.getJSONArray("results");
			if (results.length() > 0) {
				JSONObject location = results.getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location");
				LatLng coordinates = new LatLng(location.getDouble("lat"),
						location.getDouble("lng"));
				return coordinates;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}