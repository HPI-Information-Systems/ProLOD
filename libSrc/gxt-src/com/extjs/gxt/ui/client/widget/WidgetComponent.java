/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Creates a component from a widget. This allows widget instances to be treated
 * as components.
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>BoxComponent Move</dd>
 * <dd>BoxComponent Resize</dd>
 * <dd>Component Enable</dd>
 * <dd>Component Disable</dd>
 * <dd>Component BeforeHide</dd>
 * <dd>Component Hide</dd>
 * <dd>Component BeforeShow</dd>
 * <dd>Component Show</dd>
 * <dd>Component Attach</dd>
 * <dd>Component Detach</dd>
 * <dd>Component BeforeRender</dd>
 * <dd>Component Render</dd>
 * <dd>Component BrowserEvent</dd>
 * <dd>Component BeforeStateRestore</dd>
 * <dd>Component StateRestore</dd>
 * <dd>Component BeforeStateSave</dd>
 * <dd>Component SaveState</dd>
 * </dl>
 */
public class WidgetComponent extends BoxComponent {

  /**
   * The wrapped widget.
   */
  protected Widget widget;

  /**
   * Creates a new component wrapper.
   * 
   * @param widget the widget to be wrapped
   */
  public WidgetComponent(Widget widget) {
    assert widget != null : "widget must not be null";
    ComponentHelper.removeFromParent(widget);
    this.widget = widget;
    setParent(this, widget);
  }

  @Override
  public Element getElement() {
    // we need this because of lazy rendering
    return widget.getElement();
  }

  /**
   * Returns the underlying widget.
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

  @Override
  public void onBrowserEvent(Event event) {
    // Fire any handler added to the WidgetComponent itself.
    super.onBrowserEvent(event);

    // Delegate events to the widget.
    widget.onBrowserEvent(event);
  }

  @Override
  protected void onAttach() {
    ComponentHelper.doAttach(widget);
    DOM.setEventListener(getElement(), this);
    onLoad();
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
    DOM.setElementPropertyBoolean(widget.getElement(), "disabled", true);
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    DOM.setElementPropertyBoolean(widget.getElement(), "disabled", false);
  }

  @Override
  protected void onRender(Element target, int index) {
    setElement(widget.getElement(), target, index);
    super.onRender(target, index);
  }


  private native void setParent(Widget parent, Widget child) /*-{
    child.@com.google.gwt.user.client.ui.Widget::parent = parent;
  }-*/;
}
