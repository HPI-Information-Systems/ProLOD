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
import com.extjs.gxt.ui.client.widget.InputSlider;
import com.extjs.gxt.ui.client.widget.form.NumberField;

public class InputSliderHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    return component.getData("gxt-input-slider") != null;
  }

  @Override
  public void onTab(Component component, PreviewEvent pe) {
    if (component instanceof NumberField) {
      pe.stopEvent();
      focusNextWidget(component.getParent());
    }
  }
  
  @Override
  public void onEscape(Component component, PreviewEvent pe) {
    if (!(component instanceof InputSlider)) {
      super.onEscape((Component)component.getParent(), pe);
    } else {
      super.onEscape(component, pe);
    }
   
  }

}
