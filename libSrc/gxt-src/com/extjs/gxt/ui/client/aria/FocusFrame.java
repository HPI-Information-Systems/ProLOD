/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Adds a frame around a given component to indicate a component has focus.
 */
public class FocusFrame extends BaseObservable {
  private static FocusFrame instance;

  public static FocusFrame get() {
    if (instance == null) {
      instance = new FocusFrame();
    }
    return instance;
  }

  private boolean initialized;
  private Component curComponent;
  private Element curElement;
  private El focusFrameContainer;
  private List<El> sides = new ArrayList<El>();

  private FocusFrame() {

  }

  /**
   * Frames the given component.
   * 
   * @param newComponent the component to be framed
   */
  public void frame(Component newComponent) {
    frame(newComponent, null);
  }

  /**
   * Frames the given component.
   * 
   * @param newComponent the component to be framed
   * @param newElement a child element of the component where the frame should
   *          be applied
   */
  public void frame(Component newComponent, Element newElement) {
    if (!GXT.isAriaEnabled()) {
      return;
    }

    if (!initialized) {
      init();
    }

    if (!GXT.isAriaEnabled()) {
      return;
    }
    if (!initialized) {
      init();
    }

    if (curComponent != newComponent) {
      if (newComponent.isRendered()) {
        curComponent = newComponent;
        curElement = newElement != null ? newElement : curComponent.getElement();
        El e = El.fly(curElement);
        e.getParent().makePositionable();
        e.getParent().dom.appendChild(focusFrameContainer.dom);
        focusFrameContainer.show();
        sync(curComponent, newElement);
      }
    }

  }

  /**
   * Hides the frame.
   * 
   * @param c the framed component
   */
  public void hide(Component c) {
    if (initialized && c == curComponent) {
      focusFrameContainer.setVisible(false);
    }
  }

  /**
   * Shows the frame on a component that has been previously framed.
   * 
   * @param c the component
   */
  public void show(Component c) {
    if (initialized && c == curComponent) {
      focusFrameContainer.setVisible(true);
      sync(c);
    }
  }

  /**
   * Updates the frame's size and position to match the component.
   * 
   * @param c the component
   */
  public void sync(Component c) {
    sync(c, null);
  }

  /**
   * Updates the frame's size and position to match the component's child
   * element.
   * 
   * @param c the component
   * @param elem the child element
   */
  public void sync(Component c, Element elem) {
    if (initialized && curComponent == c && c.isRendered()) {
      El el = elem != null ? new El(elem) : curComponent.el();
      int width = el.getWidth();
      int height = el.getHeight();

      sides.get(0).setSize(width, 2).alignTo(el.dom, "tl", null);
      sides.get(1).setSize(width, 2).alignTo(el.dom, "bl", new int[] {0, -2});
      sides.get(2).setSize(2, height).alignTo(el.dom, "tr", new int[] {-2, 0});
      sides.get(3).setSize(2, height).alignTo(el.dom, "tl", null);

      for (El side : sides) {
        side.setZIndex(curComponent.el().getZIndex() + 1);
      }
    }
  }

  /**
   * Removes and visible frames.
   */
  public void unframe() {
    if (initialized) {
      focusFrameContainer.hide();
      curComponent = null;
      curElement = null;
    }
  }

  /**
   * Remove the comonent's frame.
   * 
   * @param c the component
   */
  public void unframe(Component c) {
    if (c == curComponent) {
      unframe();
    }
  }

  protected void init() {
    if (!initialized) {
      focusFrameContainer = new El(DOM.createDiv());
      focusFrameContainer.setStyleName("x-aria-focusframe");
      focusFrameContainer.makePositionable();
      focusFrameContainer.hide();

      for (int i = 0; i < 4; i++) {
        El side = new El(DOM.createDiv());
        side.setStyleName("x-aria-focusframe-side");
        focusFrameContainer.dom.appendChild(side.dom);
        side.makePositionable(true);
        sides.add(side);
      }
      initialized = true;
    }
  }
}
