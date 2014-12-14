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

public class FieldHandler extends FocusHandler {

//  @SuppressWarnings("unchecked")
  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    return false;//component instanceof Field;
  }

  @Override
  public void onTab(Component component, PreviewEvent pe) {

  }

}
