/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

/**
 * An class that supports placing a shim over the client window, and optionally
 * just over IFrames.
 * <p/>
 * Used by Draggable, Resizable and SplitBar
 */
public class Shim {

  private static Shim instance;

  private Shim() {

  }

  private List<El> shims = new ArrayList<El>();

  /**
   * Creates and covers the area with a Shim. If shimIframes is true will only
   * covers IFrames.
   * 
   * @param shimIframes true if you want to cover only Iframes
   */
  public void cover(boolean shimIframes) {
    if (shimIframes) {
      NodeList<Element> elements = XDOM.getBodyEl().select("iframe:not(.x-noshim)");
      shim(elements);
      elements = XDOM.getBodyEl().select("object:not(.x-noshim)");
      shim(elements);
      elements = XDOM.getBodyEl().select("applet:not(.x-noshim)");
      shim(elements);
      elements = XDOM.getBodyEl().select("embed:not(.x-noshim)");
      shim(elements);
    } else {
      shims.add(createShim(null, 0, 0, Window.getClientWidth(), Window.getClientHeight()));
    }
  }

  public static Shim get() {
    if (instance == null) {
      instance = new Shim();
    }
    return instance;
  }

  public void setStyleAttribute(String attr, String value) {
    for (El shim : shims) {
      shim.setStyleAttribute(attr, value);
    }
  }

  /**
   * Uncovers and removes the shim.
   */
  public void uncover() {
    while (!shims.isEmpty()) {
      shims.get(0).remove();
      shims.remove(0);
    }
  }

  protected El createShim(Element element, int left, int top, int width, int height) {
    Layer shim = new Layer();
    shim.hide();
    shim.enableShim();
    shim.addStyleName("x-drag-overlay");
    shim.setSize(width, height);
    shim.setLeftTop(left, top);
    shim.update("&#160;");
    El parent = null;
    if (element != null) {
      parent = El.fly(element).getParent();
    }
    if (parent != null) {
      parent.appendChild(shim.dom);
    } else {
      XDOM.getBody().appendChild(shim.dom);
    }
    shim.show();
    if (element != null) {
      shim.setZIndex(El.fly(element).getZIndex() + 1);
    } else {
      shim.setZIndex(XDOM.getTopZIndex());
    }

    return shim;
  }

  protected void shim(NodeList<Element> elements) {
    for (int i = 0; i < elements.getLength(); i++) {
      Element e = elements.getItem(i);
      if ((e.getAttribute("src").length() > 0) || !e.getTagName().toLowerCase().equals("iframe")) {
        Rectangle bounds = El.fly(e).getBounds(true);
        if (bounds.height > 0 && bounds.width > 0 && El.fly(e).isVisible()) {
          shims.add(createShim(e, bounds.x, bounds.y, bounds.width, bounds.height));
        }
      }
    }
  }
}
