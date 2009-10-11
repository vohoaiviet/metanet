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

package org.metadon.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.History;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;

public class ScreenManager
{

	private static Store	      mainPanelStore;
	private TabPanel	         appTabPanel;
	private static ArrayReader	reader;
	private static MemoryProxy	proxy;
	
	public ScreenManager(TabPanel tabPanel)
	{
		this.appTabPanel = tabPanel;
	}

	public static Store getStore()
	{
		if (mainPanelStore == null)
		{
			proxy = new MemoryProxy(getData());

			RecordDef recordDef = new RecordDef(new FieldDef[] { new StringFieldDef("id"),
			      new StringFieldDef("title"), new StringFieldDef("iconCls"), new ObjectFieldDef("screen") });

			reader = new ArrayReader(0, recordDef);
			mainPanelStore = new Store(proxy, reader);
			mainPanelStore.load();
		}
		return mainPanelStore;
	}

	public static ArrayReader getReader()
	{
		getStore();
		return reader;
	}

	public static MemoryProxy getProxy()
	{
		getStore();
		return proxy;
	}

	public void showScreen(String historyToken)
	{
		Log.debug("History token: "+ historyToken);
		
		if (historyToken == null || historyToken.equals(""))
		{
			appTabPanel.activate(0);
		}
		else
		{
			Record record = mainPanelStore.getById(historyToken);
			if (record != null)
			{
				String id = record.getAsString("id");
//				String title = record.getAsString("title");
//				String iconCls = record.getAsString("iconCls");
				Panel panel = (Panel) record.getAsObject("screen");
				showScreen(panel, historyToken);
			}
		}
	}

	public void showScreen(Panel panel,
	                       String screenName)
	{

//		appTabPanel.add(panel);
		appTabPanel.activate(panel.getId());
		History.newItem(screenName);
	}

	private static Object[][] getData()
	{
		return new Object[][] {

		new Object[] { "metamapApp", "Metamap", "map-icon", new MetamapMainPanel() },
		      new Object[] { "metaspaceApp", "Metaspace", "map-icon", new MetamapMainPanel() } };
	}
}
