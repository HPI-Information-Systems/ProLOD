/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;

/**
 * <code>CenterLayout</code> centers a single widget within its container.
 */
public class CenterLayout extends AbsoluteLayout {

  public CenterLayout() {
    monitorResize = true;
  }

  @Override
  protected void onLayout(Container<?> container, El target) {
    super.onLayout(container, target);
    Component c = container.getItem(0);
    if (c != null) {
      callLayout(c, false);

      Point p = c.el().getAlignToXY(target.dom, "c-c", null);
      p = c.el().translatePoints(p);
      setPosition(c, p.x, p.y);
    }
  }
}
