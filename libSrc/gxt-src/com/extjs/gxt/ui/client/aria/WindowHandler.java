package com.extjs.gxt.ui.client.aria;

import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.widget.Component;

public class WindowHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    // TODO Auto-generated method stub
    return false;//component instanceof Window;
  }

}
