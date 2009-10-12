package org.metadon.client;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

public abstract class MainPanel extends Panel
{

	private String	               sourceIconCls	= "source-icon";
	private ToolbarButton	      loginButton;
	private Toolbar	            toolbar;

	protected Panel	            panel;

	protected MainPanel()
	{
		setTitle(getTitle());
		setClosable(true);
		setTopToolbar(new Toolbar());
		setPaddings(0);
		setLayout(new FitLayout());
		setBorder(false);
		setAutoScroll(true);
		addListener(new PanelListenerAdapter() {
			public void onActivate(Panel panel)
			{
				MainPanel.this.onActivate();
			}
		});
	}

	public abstract Panel getViewPanel();
	
	protected void onActivate()
	{
		Panel viewPanel = getViewPanel();
		if (viewPanel instanceof Window)
		{
			((Window) viewPanel).show();
		}
	}

	protected void afterRender()
	{

		ButtonListenerAdapter listener = new ButtonListenerAdapter() {
			public void onClick(Button button,
			                    EventObject e)
			{
				login();
			}
		};

		loginButton = new ToolbarButton("Login", listener);
		loginButton.setIconCls(sourceIconCls);

		toolbar = getTopToolbar();

		toolbar.addFill();
		toolbar.addButton(loginButton);

		addViewPanel();
	}

	private void addViewPanel()
	{
		Panel mainPanel = new Panel();
		mainPanel.setBorder(false);
		mainPanel.setLayout(new BorderLayout());

		Panel viewPanel = getViewPanel();
		if (viewPanel instanceof Window)
		{
			viewPanel.show();
			viewPanel = new Panel();
		}
		viewPanel.setAutoScroll(true);
		viewPanel.setBorder(false);

		BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
		centerLayoutData.setMargins(5, 5, 5, 5);
		mainPanel.add(viewPanel, centerLayoutData);
		
		add(mainPanel);
	}

	protected boolean showEvents()
	{
		return false;
	}

	private void login()
	{

		// TODO
	}



}
