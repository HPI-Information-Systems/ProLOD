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
import com.extjs.gxt.ui.client.widget.ContentPanel;

public class ContentPanelHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    return false;//component instanceof ContentPanel;
  }

  @Override
  public void onEnter(Component component, PreviewEvent pe) {
    if (!isManaged()) return;
    pe.stopEvent();

    ContentPanel panel = (ContentPanel) component;
    if (panel.getHeader().getToolCount() > 0) {
      focusWidget(panel.getHeader().getTool(0));
      return;
    }
    if (panel.getTopComponent() != null) {
      focusWidget(panel.getTopComponent());
    } else {
      stepInto(panel, pe, true);
    }

  }

  @Override
  public void onTab(Component component, PreviewEvent pe) {
    if (!isManaged()) {
      return;
    }
    pe.stopEvent();
    if (component instanceof ContentPanel) {
      focusNextWidget(component);
    } else {
      ContentPanel panel = (ContentPanel) component.getParent();

      if (!pe.isShiftKey() && panel.indexOf(component) == panel.getItemCount() - 1) {
        if (panel.getBottomComponent() != null) {
          focusWidget(panel.getBottomComponent());
        } else if (panel.getHeader().getToolCount() > 0) {
          focusWidget(panel.getHeader().getTool(0));
        }
        return;
      }
      if (pe.isShiftKey() && panel.indexOf(component) == 0) {
        if (panel.getTopComponent() != null) {
          focusWidget(panel.getTopComponent());
        }
      }
    }
  }

}
