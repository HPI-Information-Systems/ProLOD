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
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;

public class ButtonBarHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    if (component.getParent() instanceof ToolBar) {
      return true;
    }
    return false;
  }

  @Override
  public void onTab(Component component, PreviewEvent pe) {
    if (!isManaged()) return;
    pe.preventDefault();
    Widget parent = component.getParent();
    if (pe.isShiftKey()) {
      focusPreviousWidget(parent);
    } else {
      focusNextWidget(parent);
    }
    
  }

  @Override
  public void onRight(Component component, PreviewEvent pe) {
    focusNextWidget(component);
  }

  @Override
  public void onLeft(Component component, PreviewEvent pe) {
    focusPreviousWidget(component);
  }

  @Override
  public void onEscape(Component component, PreviewEvent pe) {
    stepOut(component.getParent());
  }

}
