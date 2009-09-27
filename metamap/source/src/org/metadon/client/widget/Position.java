package org.metadon.client.widget;

import com.google.gwt.user.client.ui.DockPanel;

public enum Position
{
	TOP(DockPanel.NORTH),
	BOTTOM(DockPanel.SOUTH),
	RIGHT(DockPanel.EAST),
	LEFT(DockPanel.WEST),
	CENTER(DockPanel.CENTER);
	
	private DockPanel.DockLayoutConstant direction;
	
	Position(DockPanel.DockLayoutConstant _direction)
	{
		direction = _direction;
	}

	public DockPanel.DockLayoutConstant get()
   {
   	return direction;
   }
	

}
