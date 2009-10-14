package org.metadon.client;

import org.metadon.client.admin.view.panel.WelcomePanel;

import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.HTMLPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

public abstract class MainView extends Panel
{

	protected Panel	contentPanel;
	
	protected WelcomePanel	welcomePanel;
	protected Panel	statusPanel;

	protected MainView()
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
				MainView.this.onActivate();
			}
		});

		contentPanel = new Panel();
		contentPanel.setLayout(new BorderLayout());
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
		appendWelcomePanel();
		appendStatusPanel();
		wrapViewPanel();
	}

	/**
	 * Wraps the view panel.
	 */
	protected void wrapViewPanel()
	{
		Panel wrapperPanel = new Panel();
		wrapperPanel.setBorder(false);
		wrapperPanel.setLayout(new BorderLayout());

		Panel viewPanel = getViewPanel();
		if (viewPanel instanceof Window)
		{
			viewPanel.show();
			viewPanel = new Panel();
		}
		viewPanel.setAutoScroll(true);
		viewPanel.setBorder(false);

		BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
		centerLayoutData.setMargins(0, 0, 0, 0);
		wrapperPanel.add(viewPanel, centerLayoutData);

		add(wrapperPanel);
	}

	/**
	 * Appends the welcome Panel at the top of the view.
	 * 
	 */
	private void appendWelcomePanel()
	{
		welcomePanel = new WelcomePanel();
		welcomePanel.setTitle("Welcome Guest!");

		BorderLayoutData welcomeData = new BorderLayoutData(RegionPosition.NORTH);
		welcomeData.setMinSize(100);
		welcomeData.setMaxSize(150);
		welcomeData.setMargins(new Margins(5, 5, 5, 5));
		welcomeData.setSplit(false);
		
		welcomePanel.setLoginView();
		
		contentPanel.add(welcomePanel, welcomeData);
	}

	/**
	 * Appends the status Panel at the bottom of the view.
	 * 
	 */
	private void appendStatusPanel()
	{
		statusPanel = new HTMLPanel("Show status info here.");
		statusPanel.setHeight(100);
		statusPanel.setCollapsible(true);
		statusPanel.setTitle("Status");

		BorderLayoutData southData = new BorderLayoutData(RegionPosition.SOUTH);
		southData.setMinSize(100);
		southData.setMaxSize(150);
		southData.setMargins(new Margins(0, 0, 0, 0));
		southData.setSplit(true);

		contentPanel.add(statusPanel, southData);
	}
	

	public WelcomePanel getWelcomePanel()
   {
   	return welcomePanel;
   }

	public Panel getStatusPanel()
   {
   	return statusPanel;
   }
	
	

}
