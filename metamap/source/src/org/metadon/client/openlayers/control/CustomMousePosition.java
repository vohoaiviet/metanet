package org.metadon.client.openlayers.control;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.MousePositionOptions;
import org.gwtopenmaps.openlayers.client.control.MousePositionOutput;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.metadon.client.openlayers.OpenLayersConstants;
import org.metadon.client.openlayers.OpenLayersMapHandler;
import org.metadon.client.openlayers.OpenLayersMap;

public class CustomMousePosition implements OpenLayersMap{

	private OpenLayersMapHandler example;

	private WMS wmsLayer;

	public CustomMousePosition(){

		example = new OpenLayersMapHandler();

		//Defining a WMSLayer and adding it to a Map
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers(OpenLayersConstants.METACARTA_WMS_BASIC_LAYER);
		wmsParams.setStyles("");

		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setUntiled();
		wmsLayerParams.setTransitionEffectResize();

		wmsLayer = new WMS(
				"Basic WMS",
				OpenLayersConstants.METACARTA_WMS_URL,
				wmsParams,
				wmsLayerParams);

		example.getMap().addLayers(new Layer[] {wmsLayer});

		//Adding controls to the Map
		example.getMap().addControl(new PanZoomBar());

		MousePositionOutput mpOut = new MousePositionOutput(){

			public String format(LonLat lonLat, Map map) {
				String out = "";
				out += "<b>This is the longitude </b> ";
				out += lonLat.lon();
				out += "<b>, and this the latitude</b> ";
				out += lonLat.lon();
				return out;
			}

		};

		MousePositionOptions mpOptions = new MousePositionOptions();
		mpOptions.setFormatOutput(mpOut); //rename to setFormatOutput

		example.getMap().addControl(new MousePosition(mpOptions));


		example.getMap().addControl(new LayerSwitcher());

		//Center and Zoom
		double lon = 4.0;
		double lat = 5.0;
		int zoom = 5;
		example.getMap().setCenter(new LonLat(lon, lat), zoom);
	}

	public OpenLayersMapHandler getMapHandler(){
		return this.example;
	}


}
