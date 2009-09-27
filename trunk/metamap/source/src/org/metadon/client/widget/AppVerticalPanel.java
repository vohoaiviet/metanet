package org.metadon.client.widget;

import org.metadon.client.Metamap;

import com.gwtext.client.widgets.Panel;


/**
 * 
 * @author Hannes Weingartner
 *
 */
public class AppVerticalPanel extends Panel
{
	public void named(String identifier)
	{
		Metamap.uiRegistry.put(identifier, this);
	}

}
