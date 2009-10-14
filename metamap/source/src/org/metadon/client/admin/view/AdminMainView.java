/*
 * GWT-Ext Widget Library
 * Copyright 2007 - 2008, GWT-Ext LLC., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.metadon.client.admin.view;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.metadon.client.MainView;
import org.metadon.client.MapView;

import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.grid.PropertyGridPanel;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

public class AdminMainView extends MainView
{

	public Panel getViewPanel()
	{
		/**
		 * menu panel
		 */
		final AccordionLayout accordion = new AccordionLayout(true);

		Panel westPanel = new Panel();
		westPanel.setTitle("Menu");
		westPanel.setCollapsible(true);
		westPanel.setWidth(200);
		westPanel.setLayout(accordion);

		Panel navPanel = new Panel();
		navPanel.setHtml("<p>Hi. I'm the west panel.</p>");
		navPanel.setTitle("Navigation");
		navPanel.setBorder(false);
		navPanel.setIconCls("forlder-icon");
		westPanel.add(navPanel);

		Panel settingsPanel = new Panel();
		settingsPanel.setHtml("<p>Some settings in here.</p>");
		settingsPanel.setTitle("Settings");
		settingsPanel.setBorder(false);
		settingsPanel.setIconCls("settings-icon");
		westPanel.add(settingsPanel);

		BorderLayoutData westData = new BorderLayoutData(RegionPosition.WEST);
		westData.setSplit(true);
		westData.setMinSize(175);
		westData.setMaxSize(400);
		westData.setMargins(new Margins(5, 5, 0, 0));

		contentPanel.add(westPanel, westData);
		
		
		/**
		 * map panel
		 */
		final MapView mapView = new MapView();
		BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);
		centerData.setMargins(new Margins(5, 0, 0, 0));

		contentPanel.add(mapView, centerData);
		
		
		/**
		 * map properties panel
		 */
		Panel eastPanel = new Panel();
		eastPanel.setTitle("Map Properties");
		eastPanel.setCollapsible(true);
		eastPanel.setWidth(225);
		eastPanel.setLayout(new FitLayout());

		BorderLayoutData eastData = new BorderLayoutData(RegionPosition.EAST);
		eastData.setSplit(true);
		eastData.setMinSize(175);
		eastData.setMaxSize(400);
		eastData.setMargins(new Margins(5, 0, 5, 0));

		contentPanel.add(eastPanel, eastData);

		TabPanel tabPanel = new TabPanel();
		tabPanel.setBorder(false);
		tabPanel.setActiveTab(1);

		Panel tabOne = new Panel();
		tabOne.setHtml("<p>A TabPanel component can be a region.</p>");
		tabOne.setTitle("A Tab");
		tabOne.setAutoScroll(true);
		tabPanel.add(tabOne);

		PropertyGridPanel propertyGrid = new PropertyGridPanel();
		propertyGrid.setTitle("Property Grid");

		Map source = new HashMap();
		source.put("(name)", "Properties Grid");
		source.put("grouping", Boolean.FALSE);
		source.put("autoFitColumns", Boolean.TRUE);
		source.put("productionQuality", Boolean.FALSE);
		source.put("created", new Date());
		source.put("tested", Boolean.FALSE);
		source.put("version", new Float(0.1f));
		source.put("borderWidth", new Integer(1));

		propertyGrid.setSource(source);

		tabPanel.add(propertyGrid);
		eastPanel.add(tabPanel);
		

		return contentPanel;
	}

}