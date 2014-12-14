/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provides access to package protected methods of component and widget.
 */
public class ComponentHelper {

  public static void doAttach(Widget widget) {
    if (widget != null && !widget.isAttached()) {
      doAttachNative(widget);
    }
  }

  public static void doDetach(Widget widget) {
    if (widget != null && widget.isAttached()) {
      doDetachNative(widget);
    }
  }

  public static LayoutData getLayoutData(Component c) {
    return c.<LayoutData> getData("layoutData");
  }

  public static void setLayoutData(Component c, LayoutData data) {
    Widget parent = c.getParent();
    c.setData("layoutData", data);
    if (parent != null && parent instanceof Container<?>) {
      ((Container<?>) parent).setLayoutNeeded(true);
    }
  }

  public static void setModel(Component c, ModelData model) {
    c.setModel(model);
  }

  @SuppressWarnings("unchecked")
  public static void removeFromParent(Widget widget) {
    Widget parent = widget.getParent();

    if (parent instanceof ContentPanel) {
      ContentPanel cp = (ContentPanel) parent;
      if (cp.getTopComponent() == widget) {
        cp.setTopComponent(null);
        return;
      } else if (cp.getBottomComponent() == widget) {
        cp.setBottomComponent(null);
        return;
      }
    }
    if (parent instanceof Container) {
      ((Container) parent).remove((Component) widget);
      return;
    }
    if (parent instanceof WidgetComponent) {
      setParent(null, widget);
      return;
    }
    widget.removeFromParent();
  }

  static native void doAttachNative(Widget widget) /*-{
    widget.@com.google.gwt.user.client.ui.Widget::onAttach()();
  }-*/;

  static native void doDetachNative(Widget widget) /*-{
    widget.@com.google.gwt.user.client.ui.Widget::onDetach()();
  }-*/;
  
  private static native void setParent(Widget parent, Widget child) /*-{
  child.@com.google.gwt.user.client.ui.Widget::parent = parent;
}-*/;
}
