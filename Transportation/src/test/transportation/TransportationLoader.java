package test.transportation;

import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * This loader class is used for long-time operations while modifying data about
 * orders. In the background thread it gets details about existing orders, uses
 * geocoder to get coordinates of each order addresses and gives this data back for
 * drawing it to the map.
 *   
 */
public class TransportationLoader extends AsyncTaskLoader<List<Order>> {

	OrderParser orderParser = null;
	AddressGeocoder geocoder = null;

	public TransportationLoader(Context context) {
		super(context);
	}

	@Override
	public List<Order> loadInBackground() {
		if (orderParser == null)
			orderParser = new OrderParser();
		List<Order> orderList = orderParser.parse();

		if (geocoder == null)
			geocoder = new AddressGeocoder();
		geocoder.getAllCoordinates(orderList);
		return orderList;
	}

}
