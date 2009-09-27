package org.metadon.client.widget;

import org.metadon.client.Metamap;

import com.google.gwt.user.client.ui.DockPanel;
import com.gwtext.client.widgets.Panel;


public class AppRootPanel extends Panel
{
//	private Metamap thisModule;
	
	
	public AppRootPanel()
   {
	   super();
//	   this.thisModule = thisModule;
   }
	
//	public Metamap getThisModule()
//   {
//   	return thisModule;
//   }

	public AppContainer setContentAt(Position _position)
	{
		AppContainer container = new AppContainer(this, _position);
		return container;
	}
	
	public Panel withPanel(String name)
	{
		return Metamap.uiRegistry.get(name);
	}
	
	
	

}
