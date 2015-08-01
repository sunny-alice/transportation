package test.transportation;

import java.util.List;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * The main Activity class for the Transportation application. Has a map on it
 * and a ProgressBar object to get user know about the loading process. This
 * class also contains callback methods for orders data loader.
 * 
 */
public class MapActivity extends Activity implements OnMapReadyCallback,
		LoaderCallbacks<List<Order>> {

	/**
	 * Identifier for orders data loader.
	 */
	private final int LOADER_TRANSPORTATION_ID = 0;

	/**
	 * Default coordinates for the GoogleMap object.
	 */
	private final LatLng DEFAULT_FOCUS_POINT = new LatLng(51.165691, 10.451526);
	
	/**
	 * Default zoom level for the GoogleMap object.
	 */
	private final float DEFAULT_ZOOM_LEVEL = 5.5f;

	private GoogleMap map;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_map);

		getLoaderManager().initLoader(LOADER_TRANSPORTATION_ID, null, this)
				.forceLoad();

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap newMap) {
		map = newMap;
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_FOCUS_POINT,
				DEFAULT_ZOOM_LEVEL));
	}

	@Override
	public Loader<List<Order>> onCreateLoader(int id, Bundle args) {
		Loader<List<Order>> loader = null;
		if (id == LOADER_TRANSPORTATION_ID) {
			loader = new TransportationLoader(this);
		}

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		Toast.makeText(this, "Loading orders...", Toast.LENGTH_LONG).show();

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<Order>> loader, List<Order> orderList) {
		if (loader.getId() == LOADER_TRANSPORTATION_ID) {
			progressBar.setVisibility(View.GONE);

			drawAddressesOnMap(orderList);
		}
	}

	/**
	 * This method adds the departure and destination markers to the map and draws a line between them.
	 * @param orderList List of transportation orders. 
	 */
	private void drawAddressesOnMap(List<Order> orderList) {
		for (Order order : orderList) {
			if (order.hasCoordinates()) {

				LatLng departure = order.getDepartureCoordinates();
				map.addMarker(new MarkerOptions()
						.position(departure)
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
						.title(order.addressToString(Order.DEPARTURE_ID)));

				LatLng destination = order.getDestinationCoordinates();
				map.addMarker(new MarkerOptions()
						.position(destination)
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED))
						.title(order.addressToString(Order.DESTINATION_ID)));

				map.addPolyline(new PolylineOptions().add(departure)
						.add(destination).width(3));
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Order>> loader) {
	}
}
