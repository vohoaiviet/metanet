package org.metadon.client.openlayers.raster;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.NavToolBar;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.metadon.client.openlayers.OpenLayersConstants;
import org.metadon.client.openlayers.OpenLayersMapHandler;
import org.metadon.client.openlayers.OpenLayersMap;

public class BasicWMS implements OpenLayersMap
{

	private OpenLayersMapHandler	mapHandler;

	private WMS	        wmsLayer;

	public BasicWMS()
	{

		mapHandler = new OpenLayersMapHandler();

		//Defining a WMSLayer and adding it to a Map
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("basic");
		wmsParams.setStyles("");

		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setUntiled();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);

		wmsLayer = new WMS("Basic WMS", OpenLayersConstants.METACARTA_WMS_URL, wmsParams, wmsLayerParams);

		mapHandler.getMap().addLayers(new Layer[] { wmsLayer });

		//Adding controls to the Map
		mapHandler.getMap().addControl(new PanZoomBar());
		//use NavToolbar instead of deprecated MouseToolbar
		mapHandler.getMap().addControl(new NavToolBar());
		mapHandler.getMap().addControl(new MousePosition());
		mapHandler.getMap().addControl(new LayerSwitcher());

		//Center and Zoom
		double lon = 20.27;
		double lat = 44.49;
		int zoom = 8;
		mapHandler.getMap().setCenter(new LonLat(lon, lat), zoom);
	}

	public OpenLayersMapHandler getMapHandler()
	{
		return this.mapHandler;
	}

}
