package test.transportation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used for parsing the web page which contains the information
 * about existing orders.
 * 
 */
public class OrderParser {

	/**
	 * Link to the web page with the information about existing orders.
	 */
	private final String ordersPath = "http://mobapply.com/tests/orders/";

	/**
	 * The field used for country codes mapping. It has three-letter ISO country
	 * code as the key and two-letter code as the value.
	 */
	private Map<String, String> localeMap;

	/**
	 * The main parsing method for the class OrderParser. Combines getting data
	 * from given site and parsing that data to the convenient format.
	 * 
	 * @return List of Order objects with the orders information.
	 */
	public List<Order> parse() {
		initCountryCodeMapping();

		JSONArray orders = getJSONOrders();
		return parseJSONOrders(orders);
	}

	/**
	 * Gets information about orders from given url.
	 * 
	 * @return JSONArray with the result from reading the web page.
	 */
	private JSONArray getJSONOrders() {
		StringBuilder ordersBuilder = new StringBuilder();

		try {
			URL ordersUrl = new URL(ordersPath);
			URLConnection urlConnection = ordersUrl.openConnection();

			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				ordersBuilder.append(line);
			}
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}

		JSONArray jsonOrders = null;
		try {
			jsonOrders = new JSONArray(ordersBuilder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonOrders;
	}

	/**
	 * Parses given JSONArray with the information about orders and converts it
	 * to readable format.
	 * 
	 * @param jsonOrders
	 *            JSONArray with the orders data.
	 * @return List of Order objects with the details for each order.
	 */
	private List<Order> parseJSONOrders(JSONArray jsonOrders) {
		List<Order> ordersList = new ArrayList<>();

		for (int i = 0; i < jsonOrders.length(); i++) {
			Order order = null;

			try {
				JSONObject jsonOrder = jsonOrders.getJSONObject(i);

				JSONObject jsonDeparture = jsonOrder
						.getJSONObject("departureAddress");
				Map<String, Object> departureAddress = getAddressDetails(jsonDeparture);

				JSONObject jsonDestination = jsonOrder
						.getJSONObject("destinationAddress");
				Map<String, Object> destinationAddress = getAddressDetails(jsonDestination);

				order = new Order(departureAddress, destinationAddress);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (order != null) {
				ordersList.add(order);
			}
		}
		return ordersList;
	}

	/**
	 * Creates a Map object with the address details read from address JSON
	 * object.
	 * 
	 * @param addressJSON
	 *            Object with the address details in JSON format.
	 * @return Map object with the address details.
	 */
	private Map<String, Object> getAddressDetails(JSONObject addressJSON) {
		Map<String, Object> addressDetails = new HashMap<>();

		try {
			String[] components = { Order.ADDRESS_COUNTRY,
					Order.ADDRESS_ZIPCODE, Order.ADDRESS_CITY,
					Order.ADDRESS_COUNTRY_CODE, Order.ADDRESS_STREET,
					Order.ADDRESS_HOUSE };
			for (String component : components) {
				addressDetails.put(component, addressJSON.getString(component));
			}
			addressDetails.put(Order.ADDRESS_COUNTRY_CODE_ALPHA_2,
					getTwoLetterCountryCode((String) addressDetails
							.get(Order.ADDRESS_COUNTRY_CODE)));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return addressDetails;
	}

	/**
	 * Creates a map with the country code mapping. Map uses ISO 3-letter
	 * country code as its key and 2-letter ISO country code as its value.
	 */
	private void initCountryCodeMapping() {
		String[] twoLetterCountries = Locale.getISOCountries();
		localeMap = new HashMap<String, String>(twoLetterCountries.length);
		for (String twoLetterCode : twoLetterCountries) {
			Locale locale = new Locale("", twoLetterCode);
			localeMap.put(locale.getISO3Country().toUpperCase(locale),
					locale.getCountry());
		}
	}

	/**
	 * Converts 3-letter ISO country code to 2-letter code.
	 * 
	 * @param threeLetterCountryCode
	 *            3-letter ISO country code.
	 * @return 2-letter ISO country code.
	 */
	private String getTwoLetterCountryCode(String threeLetterCountryCode) {
		return localeMap.get(threeLetterCountryCode);
	}
}