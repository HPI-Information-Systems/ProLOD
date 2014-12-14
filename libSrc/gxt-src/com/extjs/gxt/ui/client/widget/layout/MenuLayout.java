/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.menu.AdapterMenuItem;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * Custom layout for <code>Menu</code>
 */
public class MenuLayout extends Layout {
  protected void cleanup(El target) {
    NodeList<Node> cn = target.dom.getChildNodes();
    for (int i = cn.getLength() - 1; i >= 0; i--) {
      if (!cn.getItem(i).hasChildNodes()) {
        target.dom.removeChild(cn.getItem(i));
      }
    }
  }

  @Override
  protected void onComponentHide(Component component) {
    super.onComponentHide(component);
    if (component.isRendered()) {
      component.el().getParent().addStyleName(component.getHideMode().value());
    }

  }

  @Override
  protected void onComponentShow(Component component) {
    super.onComponentShow(component);
    if (component.isRendered()) {
      component.el().getParent().removeStyleName(component.getHideMode().value());
    }
  }

  @Override
  protected void onLayout(Container<?> container, El target) {
    super.onLayout(container, target);
    cleanup(target);
  }

  @Override
  protected void renderComponent(Component c, int index, El target) {
    if (c != null && (!c.isRendered() || !isValidParent(c.getElement(), target.dom))) {
      Element div = DOM.createDiv();
      div.setId("x-menu-el-" + c.getId());
      div.setClassName("x-menu-list-item");

      if (GXT.isAriaEnabled()) {
        Accessibility.setRole(div, "presentation");
      }
      target.insertChild(div, index);
      boolean needsIndent = (c instanceof Field<?>) || (c instanceof ContentPanel)
          || (c instanceof AdapterMenuItem && ((AdapterMenuItem) c).isNeedsIndent());

      if (!c.isRendered()) {
        c.render(div);
      } else {
        c.el().insertInto(div);
        if (!c.isEnabled()) {
          c.disable();
        }
      }
      fly(div).setStyleName("x-menu-list-item-indent", needsIndent);

    }
  }
}
