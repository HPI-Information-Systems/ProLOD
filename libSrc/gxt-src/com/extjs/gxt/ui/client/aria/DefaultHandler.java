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

public class DefaultHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    return true;
  }

  @Override
  public void onTab(Component component, PreviewEvent pe) {
    if (!isManaged()) return;

    if (component.isAriaIgnore()) {
      return;
    }
    pe.stopEvent();
    if (pe.isShiftKey()) {
      focusPreviousWidget(component);
    } else {
      focusNextWidget(component);
    }
  }

  @Override
  public void onEscape(Component component, PreviewEvent pe) {
    if (!isManaged()) return;
    stepOut(component);
  }

  @Override
  public void onEnter(Component component, PreviewEvent pe) {
    if (!isManaged()) return;
    stepInto(component, pe, true);
  }

}
