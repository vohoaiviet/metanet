package org.metadon.client;

import java.util.HashMap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.WindowMgr;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * Module entry point - defines <code>onModuleLoad()</code>.
 * 
 * @author Hannes Weingartner
 */
public class Metamap implements EntryPoint, HistoryListener
{

	public static HashMap<String, Panel>	uiRegistry	= new HashMap<String, Panel>();

	private ScreenManager	             screenManager;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		//create the main panel and assign it a BorderLayout
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new BorderLayout());

		BorderLayoutData northLayoutData = new BorderLayoutData(RegionPosition.NORTH);
		northLayoutData.setSplit(false);

		BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
		centerLayoutData.setMargins(new Margins(5, 0, 5, 5));

		Panel centerPanelWrapper = new Panel();
		centerPanelWrapper.setLayout(new FitLayout());
		centerPanelWrapper.setBorder(false);
		centerPanelWrapper.setBodyBorder(false);

		TabPanel applicationPanel = getApplicationTabPanel();
		screenManager = new ScreenManager(applicationPanel);
		
		centerPanelWrapper.add(applicationPanel);
      mainPanel.add(centerPanelWrapper, centerLayoutData);

		final String initToken = History.getToken();
		if (initToken.length() != 0)
		{
			mainPanel.addListener(new PanelListenerAdapter() {
				public void onRender(Component component)
				{
					onHistoryChanged(initToken);
				}
			});
		}
		new Viewport(mainPanel);
//		new Viewport(new MetamapMainPanel());
		
	// Add history listener
      History.addHistoryListener(this);
      
      Log.debug("Module loaded.");
	}

	/**
	* This method is called whenever the application's history changes.
	*
	* @param historyToken the histrory token
	*/
	public void onHistoryChanged(String historyToken)
	{
		screenManager.showScreen(historyToken);
	}
	
	public TabPanel getApplicationTabPanel()
	{

		TabPanel appTabPanel = new TabPanel();
		appTabPanel.setBodyBorder(false);
		appTabPanel.setEnableTabScroll(true);
		appTabPanel.setAutoScroll(true);
		appTabPanel.setAutoDestroy(false);
		appTabPanel.setActiveTab(0);

		//hide the panel when the tab is changed
		appTabPanel.addListener(new TabPanelListenerAdapter() {
			public boolean doBeforeTabChange(TabPanel source,
			                                 Panel newPanel,
			                                 Panel oldPanel)
			{
				WindowMgr.hideAll();
				return true;
			}
			
//			 public void onActivate(TabPanel source) {
//             MainPanel.this.onActivate();
//         }
		});

		appTabPanel.setLayoutOnTabChange(true);
		appTabPanel.setTitle("Main Content");

		Store mainPanelStore = ScreenManager.getStore();

		Record[] records = mainPanelStore.getRecords();
		for (int i = 0; i < records.length; i++)
		{
			Record record = records[i];

			final String id = record.getAsString("id");
			final String title = record.getAsString("title");
			final String iconCls = record.getAsString("iconCls");
			final Panel panel = (Panel) record.getAsObject("screen");
			
			record.set("screen", panel);
			
			Panel tab = new Panel();
			tab.setAutoScroll(true);
			tab.setTitle(title);
			tab.setIconCls(iconCls);
			tab.setClosable(false);
			tab.add(panel);

			appTabPanel.add(tab);
		}

		return appTabPanel;
	}

}
