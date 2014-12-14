package com.extjs.gxt.ui.client.aria;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.ui.Widget;

public interface NavigationHandler {

  public boolean canHandleTabKey(Component comp);
  
  public List<Widget> getOrderedWidgets(Widget widget);
  
}
