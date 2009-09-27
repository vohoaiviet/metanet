package org.metadon.client;

import java.util.HashMap;

import org.metadon.client.widget.AppMapContentPanel;
import org.metadon.client.widget.AppRootPanel;
import org.metadon.client.widget.Position;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Viewport;

/**
 * Module entry point - defines <code>onModuleLoad()</code>.
 * 
 * @author Hannes Weingartner
 */
public class Metamap implements EntryPoint
{
	
	public static HashMap<String, Panel> uiRegistry = new HashMap<String, Panel>();
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		new Viewport(createRootPanel());
	}
	
	private Panel createRootPanel()
	{
		AppRootPanel root = new AppRootPanel();
		
		root.setContentAt(Position.CENTER).withHorizontalPanel().named("login");
		root.setContentAt(Position.TOP).withMapContentPanel().named("map");
		root.setContentAt(Position.BOTTOM).withHorizontalPanel().named("status");
		
		root.withPanel("login").add(new Label("login panel"));
		root.withPanel("map").add(new Label("map"));
		root.withPanel("status").add(new Label("status panel"));
		
		return root;
		
	}
	
	
	
}
