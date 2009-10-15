
package org.metadon.client.admin.view;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

public class AboutWindow
{
	
	private String	               informationIconCls	= "info-icon";

	public Button getButton()
	{

		//center panel
		TabPanel tabPanel = new TabPanel();
		tabPanel.setActiveTab(0);

		Panel tab1 = new Panel();
		tab1.setTitle("Bogus Tab");
		tab1.setHtml("<h3>Test</h3>");
		tab1.setAutoScroll(true);

		Panel tab2 = new Panel();
		tab2.setTitle("Another Tab");
		tab2.setHtml("<h3>Test</h3>");
		tab2.setAutoScroll(true);

		Panel tab3 = new Panel();
		tab3.setTitle("Closable Tab");
		tab3.setHtml("<h3>Test</h3>");
		tab3.setAutoScroll(true);
		tab3.setClosable(true);

		tabPanel.add(tab1);
		tabPanel.add(tab2);
		tabPanel.add(tab3);

		//west panel
		Panel navPanel = new Panel();
		navPanel.setTitle("Navigation");
		navPanel.setWidth(200);
		navPanel.setCollapsible(true);

		BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);
		centerData.setMargins(3, 0, 3, 3);

		BorderLayoutData westData = new BorderLayoutData(RegionPosition.WEST);
		westData.setSplit(true);
		westData.setMargins(3, 3, 0, 3);
		westData.setCMargins(3, 3, 3, 3);

		final Window window = new Window();
		window.setTitle("Layout Window");
		window.setClosable(true);
		window.setWidth(800);
		window.setHeight(550);
		window.setPlain(true);
		window.setLayout(new BorderLayout());
		window.add(tabPanel, centerData);
		window.add(navPanel, westData);
		window.setCloseAction(Window.HIDE);

		Button button = new Button("About");
		button.setIconCls(informationIconCls);
		button.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button,
			                    EventObject e)
			{
				window.show(button.getId());
			}
		});

		return button;
	}
}