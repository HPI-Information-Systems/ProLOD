/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;

public class BorderLayoutEvent extends LayoutEvent {

  private ContentPanel panel;
  private LayoutRegion region;

  public BorderLayoutEvent(Container<?> container, Layout layout) {
    super(container, layout);
  }

  /**
   * Gets the panel that is associated with this event.
   * 
   * @return the panel associated with this event
   */
  public ContentPanel getPanel() {
    return panel;
  }

  /**
   * Gets the LayoutRegion that is associated with this event.
   * 
   * @return the LayoutRegion
   */
  public LayoutRegion getRegion() {
    return region;
  }

  /**
   * Sets the panel that is associated with this event.
   * 
   * @param panel the panel associated with this event
   */
  public void setPanel(ContentPanel panel) {
    this.panel = panel;
  }

  /**
   * Sets the LayoutRegion.
   * 
   * @param region the LayoutRegion to set
   */
  public void setRegion(LayoutRegion region) {
    this.region = region;
  }

}
