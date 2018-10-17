package org.artorg.tools.phantomData.client.controllers;

import javafx.event.Event;
import javafx.event.EventType;

public class MenuItemEvent2 extends Event {
	private static final long serialVersionUID = -888480782928761029L;

	public static final EventType<MenuItemEvent2> ANY =
        new EventType<MenuItemEvent2>(Event.ANY, "MENU_ITEM");
	
	public static final EventType<MenuItemEvent2> UPDATE =
        new EventType<MenuItemEvent2>(MenuItemEvent2.ANY, "UPDATE");
	
	public MenuItemEvent2(EventType<? extends MenuItemEvent2> eventType) {
		super(eventType);
		// TODO Auto-generated constructor stub
	}
	
}