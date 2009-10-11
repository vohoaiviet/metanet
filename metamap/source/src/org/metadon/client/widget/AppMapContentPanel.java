package org.metadon.client.widget;

import org.metadon.client.Metamap;

import com.google.gwt.core.client.JavaScriptObject;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.util.JavaScriptObjectHelper;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.map.LatLonPoint;
import com.gwtext.client.widgets.map.MapPanel;
import com.gwtext.client.widgets.map.Marker;
import com.gwtext.client.widgets.map.OpenLayersMap;

public class AppMapContentPanel extends Panel
{
	
	private MapPanel mapPanel;

	
	public AppMapContentPanel()
   {
		createMapPanel();
		addMapControls();
//		new Viewport(mapPanel);
		this.add(mapPanel);
		updateMap("belgrade", JavaScriptObjectHelper.createObject(), this);
		
   }
	
	public void named(String identifier)
	{
		Metamap.uiRegistry.put(identifier, mapPanel);
	}

	private void createMapPanel()
	{
		mapPanel = new OpenLayersMap();
//		mapPanel.setTitle("Test");
		mapPanel.setHeight(600);
		mapPanel.setWidth(1200);
		mapPanel.addLargeControls();
	}
	
	private void addMapControls()
	{
		final AppMapContentPanel _this = this;
	 
		final TextField addressField = new TextField();
		addressField.setValue("belgrade");
		ToolbarButton refreshMapButton = new ToolbarButton("Refresh map", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject  e) {
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
	 
		mapPanel.setTopToolbar(toolbar);
	}
	
	public native void updateMap(String locationAddress, JavaScriptObject llp, AppMapContentPanel _this) /*-{
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
 
		    		_this.@org.metadon.client.widget.AppMapContentPanel::renderMap(Lcom/google/gwt/core/client/JavaScriptObject;)(llp);
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
