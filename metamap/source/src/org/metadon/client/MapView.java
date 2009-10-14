package org.metadon.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.util.JavaScriptObjectHelper;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.map.LatLonPoint;
import com.gwtext.client.widgets.map.MapPanel;
import com.gwtext.client.widgets.map.Marker;
import com.gwtext.client.widgets.map.OpenLayersMap;

public class MapView extends Panel
{

	private MapPanel	mapInstance;

	public MapView()
	{
		mapInstance = createMapInstance();
		mapInstance.addLargeControls();

		setBorder(false);
		addMapControls();
		add(mapInstance);
		updateMap("belgrade", JavaScriptObjectHelper.createObject(), this);

		// fit map size to panel size if panel size changes
		addListener(new PanelListenerAdapter() {
			public void onBodyResize(Panel panel,
			                         String newWidth,
			                         String newHeight)
			{
				getMapInstance().setHeight(Integer.valueOf(newHeight).intValue());
				getMapInstance().setWidth(Integer.valueOf(newWidth).intValue());
			}
		});
	}

	private MapPanel createMapInstance()
	{
		return new OpenLayersMap();
	}

	private void addMapControls()
	{
		final MapView _this = this;

		final TextField addressField = new TextField();
		addressField.setValue("belgrade");
		ToolbarButton refreshMapButton = new ToolbarButton("Refresh map", new ButtonListenerAdapter() {
			public void onClick(Button button,
			                    EventObject e)
			{
				String address = addressField.getText();
				if (!address.trim().equals(""))
					updateMap(address, JavaScriptObjectHelper.createObject(), _this);
			}
		});

		Toolbar toolbar = new Toolbar();
		toolbar.addText("Enter an address: ");
		toolbar.addField(addressField);
		toolbar.addSpacer();
		toolbar.addButton(refreshMapButton);

		mapInstance.setTopToolbar(toolbar);
	}

	public native void updateMap(String locationAddress,
	                             JavaScriptObject llp,
	                             MapView _this)
	/*-{
		var geo = new $wnd.GClientGeocoder();

		geo.getLocations(locationAddress, 
		function(response) 		// callback method to be executed when result arrives from server
		{
			if (!response || response.Status.code != 200) 
			{
					alert("Unable to geocode that address");
			} 
			else 
		   		{
		    		var place = response.Placemark[0];
		    		llp.lat = place.Point.coordinates[1];
		    		llp.lon = place.Point.coordinates[0];

		    		_this.@org.metadon.client.MapView::renderMap(Lcom/google/gwt/core/client/JavaScriptObject;)(llp);
		   		}
				}
			);
	}-*/;

	public void renderMap(JavaScriptObject jsObj)
	{
		double lat = Double.parseDouble(JavaScriptObjectHelper.getAttribute(jsObj, "lat"));
		double lon = Double.parseDouble(JavaScriptObjectHelper.getAttribute(jsObj, "lon"));

		LatLonPoint latLonPoint = new LatLonPoint(lat, lon);
		mapInstance.setCenterAndZoom(latLonPoint, 14);
		mapInstance.addMarker(new Marker(latLonPoint));
	}

	public MapPanel getMapInstance()
	{
		return mapInstance;
	}

}
