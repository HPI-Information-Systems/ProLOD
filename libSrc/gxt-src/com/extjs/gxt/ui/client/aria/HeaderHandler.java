/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Header;

public class HeaderHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    return false;//component.getParent().getParent() instanceof Header;
  }

  @Override
  public void onTab(Component component, PreviewEvent pe) {
    if (!isManaged()) return;
    pe.stopEvent();
    Header head = (Header)component.getParent().getParent();
    int index = head.indexOf(component);
    if (index == head.getToolCount() - 1) {
      stepInto(head.getParent(), pe, true);
      return;
    }
    super.onTab(component, pe);
  }
}
