package com.extjs.gxt.ui.client.aria;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Header;
import com.google.gwt.user.client.ui.Widget;

public class ContentPanelNavigationHandler implements NavigationHandler {

  public List<Widget> getOrderedWidgets(Widget widget) {
    ContentPanel panel = (ContentPanel) widget;

    List<Widget> widgets = new ArrayList<Widget>();

    Header header = panel.getHeader();
    for (int i = 0, len = header.getTools().size(); i < len; i++) {
      widgets.add(header.getTool(i));
    }

    if (panel.getTopComponent() != null) {
      widgets.add(panel.getTopComponent());
    }

    for (int i = 0, len = panel.getItemCount(); i < len; i++) {
      widgets.add(panel.getItem(i));
    }

    if (panel.getBottomComponent() != null) {
      widgets.add(panel.getBottomComponent());
    }

    if (panel.getButtonBar() != null) {
      widgets.add(panel.getButtonBar());
    }
    return widgets;
  }

  public boolean canHandleTabKey(Component comp) {
    return comp instanceof ContentPanel;
  }

}
