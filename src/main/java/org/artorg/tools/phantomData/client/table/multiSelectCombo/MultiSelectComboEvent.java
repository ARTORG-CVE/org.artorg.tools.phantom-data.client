package org.artorg.tools.phantomData.client.table.multiSelectCombo;

import javafx.event.Event;
import javafx.event.EventType;
 
public class MultiSelectComboEvent extends Event
{
    static final long serialVersionUID = 1L;
    public static EventType<MultiSelectComboEvent> EVENT_OK = new EventType<>(ANY, "EVENT_OK");
    public static EventType<MultiSelectComboEvent> EVENT_CANCEL = new EventType<>(ANY, "EVENT_CANCEL");
 
    private MultiSelectCombo cmb;
 
    public MultiSelectComboEvent(EventType<? extends Event> eventType)
    {
    super(eventType);
 
    }
 
    public MultiSelectComboEvent(MultiSelectCombo cmb, EventType<? extends Event> eventType)
    {
    super(eventType);
    this.cmb = cmb;
    }
 
}