package org.metadon.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.util.JavaScriptObjectHelper;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.map.LatLonPoint;
import com.gwtext.client.widgets.map.MapPanel;
import com.gwtext.client.widgets.map.Marker;
import com.gwtext.client.widgets.map.OpenLayersMap;

/**
 * Module entry point - defines <code>onModuleLoad()</code>.
 * 
 * @author Hannes Weingartner
 */
public class Metamap implements EntryPoint
{
//	private DockPanel mainPanel = new DockPanel();
//	private ContentPanel contentPanel = new ContentPanel();
	
	private MapPanel mapPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		/** gwtext library **/
		createMapPanel();
		addMapControls();
		new Viewport(mapPanel);
		updateMap("belgrade", JavaScriptObjectHelper.createObject(), this);

		
		
		/** openlayers library **/
//		// add a specific map type here
//		contentPanel.setTitle("Meta-D.O.N");
//		BasicWMS basicWms = new BasicWMS();
//		contentPanel.setMapHandler(basicWms.getMapHandler());
//
//		// TODO add login and status panel
//		
//		mainPanel.add(contentPanel, DockPanel.CENTER);
//		RootPanel.get().add(mainPanel);
	}
	
	private void createMapPanel()
	{
		mapPanel = new OpenLayersMap();
		mapPanel.setTitle("Test");
		mapPanel.setHeight(400);
		mapPanel.setWidth(400);
		mapPanel.addLargeControls();
	}
	
	private void addMapControls()
	{
		final Metamap thisModule = this;
	 
		final TextField addressField = new TextField();
		addressField.setValue("belgrade");
		ToolbarButton refreshMapButton = new ToolbarButton("Refresh map", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject  e) {
				String address = addressField.getText();
				if (!address.trim().equals(""))
					updateMap(address, JavaScriptObjectHelper.createObject(), thisModule);
			}
		});
	 
		Toolbar toolbar = new Toolbar();
		toolbar.addText("Enter an address: ");
		toolbar.addField(addressField);
		toolbar.addSpacer();
		toolbar.addButton(refreshMapButton);
	 
		mapPanel.setTopToolbar(toolbar);
	}
	
	public native void updateMap(String locationAddress, JavaScriptObject llp, Metamap thisModule) /*-{
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
 
		    		thisModule.@org.metadon.client.Metamap::renderMap(Lcom/google/gwt/core/client/JavaScriptObject;)(llp);
	      		}
      		}
      	);
	}-*/;
 
 
	public void renderMap(JavaScriptObject jsObj)
	{
		double lat = Double.parseDouble(JavaScriptObjectHelper.getAttribute(jsObj, "lat"));
		double lon = Double.parseDouble(JavaScriptObjectHelper.getAttribute(jsObj, "lon"));
	 
		LatLonPoint latLonPoint = new LatLonPoint(lat, lon);
		mapPanel.setCenterAndZoom(latLonPoint, 14);
		mapPanel.addMarker(new Marker(latLonPoint));
	}
	
}
