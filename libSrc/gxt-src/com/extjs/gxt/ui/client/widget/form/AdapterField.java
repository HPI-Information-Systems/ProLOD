/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.form;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Allows any widget to be used in a <code>Formlayout</code>.
 * 
 * <p/>
 * By default, the wrapped widget will not be resized when the field is resized.
 * This can be changed by calling {@link #setResizeWidget(boolean)}.
 */
public class AdapterField extends Field<Object> {

  /**
   * The wrapped widget.
   */
  protected Widget widget;

  private boolean resizeWidget;

  /**
   * Creates a new adapter field.
   * 
   * @param widget the widget to be wrapped
   */
  public AdapterField(Widget widget) {
    this.widget = widget;
  }

  @Override
  public Element getElement() {
    // we need this because of lazy rendering
    return widget.getElement();
  }

  /**
   * Returns the wrapped widget.
   * 
   * @return the widget
   */
  public Widget getWidget() {
    return widget;
  }

  @Override
  public boolean isAttached() {
    if (widget != null) {
      return widget.isAttached();
    }
    return false;
  }

  /**
   * Returns true if the wrapped widget is being resized.
   * 
   * @return true is resizing is enabled
   */
  public boolean isResizeWidget() {
    return resizeWidget;
  }

  @Override
  public boolean isValid(boolean silent) {
    return true;
  }

  @Override
  public void onBrowserEvent(Event event) {
    // Fire any handler added to the AdapterField itself.
    super.onBrowserEvent(event);

    // Delegate events to the widget.
    widget.onBrowserEvent(event);
  }

  /**
   * True to resize the wrapped widget when the field is resized (defaults to
   * false).
   * 
   * @param resizeWidget true to resize the wrapped widget
   */
  public void setResizeWidget(boolean resizeWidget) {
    this.resizeWidget = resizeWidget;
  }

  @Override
  public boolean validate(boolean preventMark) {
    return true;
  }

  @Override
  protected void onAttach() {
    ComponentHelper.doAttach(widget);
    DOM.setEventListener(getElement(), this);
    onLoad();
  }

  @Override
  protected void onBlur(ComponentEvent ce) {

  }

  @Override
  protected void onDetach() {
    try {
      onUnload();
    } finally {
      ComponentHelper.doDetach(widget);
    }
    onDetachHelper();
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    if (widget instanceof Component) {
      ((Component) widget).disable();
    } else {
      DOM.setElementPropertyBoolean(widget.getElement(), "disabled", true);
    }
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    if (widget instanceof Component) {
      ((Component) widget).enable();
    } else {
      DOM.setElementPropertyBoolean(widget.getElement(), "disabled", false);
    }
  }

  @Override
  protected void onFocus(ComponentEvent ce) {

  }

  @Override
  protected void onRender(Element target, int index) {
    if (widget instanceof Component) {
      Component c = (Component) widget;
      if (!c.isRendered()) {
        c.render(target, index);
      }
    }
    setElement(widget.getElement(), target, index);
    if (widget instanceof LayoutContainer) {
      ((LayoutContainer) widget).layout();
    }
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    if (resizeWidget) {
      if (widget instanceof BoxComponent) {
        ((BoxComponent) widget).setSize(width, height);
      }
    }
  }

  @Override
  protected boolean validateValue(String value) {
    return true;
  }

}
