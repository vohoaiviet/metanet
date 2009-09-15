package org.metadon.client.widget;

import org.metadon.client.openlayers.OpenLayersMapHandler;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 *
 * @author Edwin Commandeur - Atlis EJS
 *
 * TODO make this a composite
 */
public class ContentPanel extends TabPanel
{

	private DockPanel	  metamapPanel	  = new DockPanel();
	private HTMLPanel	  metaspacePanel	= new HTMLPanel("metaspace here");
	private OpenLayersMapHandler	mapHandler;

	public ContentPanel()
	{
		add(metamapPanel, "metamap");
		add(metaspacePanel, "metaspace");
		this.selectTab(0);
	}

	public void setMapHandler(OpenLayersMapHandler _mapHandler)
	{
		if (mapHandler != null)
		{
			mapHandler.destroy();
			metamapPanel.remove(mapHandler);
		}
		metamapPanel.add(_mapHandler, DockPanel.CENTER);
		mapHandler = _mapHandler;
	}

	public Panel getMetamapPanel()
	{
		return this.metamapPanel;
	}

	public Panel getMetaspacePanel()
	{
		return this.metaspacePanel;
	}

}
