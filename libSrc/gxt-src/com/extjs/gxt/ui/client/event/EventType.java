/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import java.io.Serializable;

import com.google.gwt.user.client.Event;

/**
 * Base class for all events types.
 */
public class EventType implements Serializable {

  private static int count = 0;

  // needed to use FastMap for much better speed
  final String id;

  private int eventCode = -1;

  /**
   * Creates a new event type.
   */
  public EventType() {
    id = String.valueOf(count++);
  }

  /**
   * Creates a new browser based event type.
   * 
   * @param eventCode additional information about the event
   */
  public EventType(int eventCode) {
    this();
    this.eventCode = eventCode;
  }

  /**
   * Returns the event code.
   * 
   * @return the event code
   * @see Event
   */
  public int getEventCode() {
    return eventCode;
  }

  /**
   * Returns true if the event type represents a browser event type (GWT event).
   * 
   * @return true for browser event types
   */
  public boolean isBrowserEvent() {
    return eventCode != -1;
  }
}
