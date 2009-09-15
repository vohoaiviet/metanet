package org.metadon.client.openlayers;

import java.util.ArrayList;

import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.util.JObjectArray;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenLayersMapHandler extends DockPanel {

	private MapOptions defaultMapOptions;
	private Map map;
	private ArrayList<Widget> widgets = new ArrayList<Widget>();

	public OpenLayersMapHandler(){
		this.defaultMapOptions = new MapOptions();
		//In OL, the map gets PanZoom, Navigation, ArgParser, and Attribution Controls
		//by default. Do setControls below to start with a map without Controls.
		this.defaultMapOptions.setControls(new JObjectArray(new JSObject[] {}));
		this.defaultMapOptions.setNumZoomLevels(16);
		this.defaultMapOptions.setProjection("EPSG:4326");
		initMapWidget(this.defaultMapOptions);
	}

	public OpenLayersMapHandler(MapOptions options){
		initMapWidget(options);
	}

	private void initMapWidget(MapOptions options){
		MapWidget mapWidget = new MapWidget("900px","750px", defaultMapOptions);
		this.map = mapWidget.getMap();
		add(mapWidget, DockPanel.CENTER);
	}

	public Map getMap(){
		return this.map;
	}

	public void destroy() {
		this.map.destroy();
		for(int i = 0, max = widgets.size(); i < max; i++){
			this.remove(widgets.get(i));
		};

	}

	public void add(Widget w, DockLayoutConstant c){
		super.add(w,c);
		this.widgets.add(w);
	}
}
