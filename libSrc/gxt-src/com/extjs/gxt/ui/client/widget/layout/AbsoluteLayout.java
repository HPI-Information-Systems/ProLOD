/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Container;

/**
 * Inherits the anchoring of {@link AnchorLayout} and adds the ability for left
 * / top positioning using the <code>AbsoluteData</code> left and top
 * properties.
 * 
 * <p />
 * Child Widgets are:
 * <ul>
 * <li><b>Sized</b> : Yes - relative to parent container.</li>
 * <li><b>Positioned</b> : Yes - using left and top.</li>
 * </ul>
 * 
 * @see AbsoluteData
 */
public class AbsoluteLayout extends AnchorLayout {

  public AbsoluteLayout() {
    componentStyleName = "x-abs-layout-item";
    targetStyleName = "x-abs-layout-container";
  }

  /**
   * Sets the component's position.
   * 
   * @param c the component
   * @param left the left value
   * @param top the top value
   */
  public void setPosition(Component c, int left, int top) {
    LayoutData data = ComponentHelper.getLayoutData(c);
    if (data != null && data instanceof AbsoluteData) {
      AbsoluteData ad = (AbsoluteData) data;
      ad.setLeft(left);
      ad.setTop(top);
    }

    if (container.isRendered() && c.isRendered()) {
      super.setPosition(c, left,top);
    }
  }

  @Override
  protected void onLayout(Container<?> container, El target) {
    super.onLayout(container, target);
    for (int i = 0; i < container.getItemCount(); i++) {
      Component c = container.getItem(i);
      LayoutData data = ComponentHelper.getLayoutData(c);
      if (data != null && data instanceof AbsoluteData) {
        AbsoluteData ad = (AbsoluteData) data;
        setPosition(c, ad.getLeft(), ad.getTop());
      }
    }
  }

}
