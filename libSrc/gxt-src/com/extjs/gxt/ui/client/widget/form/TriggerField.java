/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.form;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * Provides a convenient wrapper for TextFields that adds a clickable trigger
 * button (looks like a combobox by default).
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>TriggerClick</b> : FieldEvent(field, event)<br>
 * <div>Fires after the trigger is clicked.</div>
 * <ul>
 * <li>field : this</li>
 * <li>event : event</li>
 * </ul>
 * </dd>
 * </dl>
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>Field Focus</dd>
 * <dd>Field Blur</dd>
 * <dd>Field Change</dd>
 * <dd>Field Invalid</dd>
 * <dd>Field Valid</dd>
 * <dd>Field KeyPress</dd>
 * <dd>Field SpecialKey</dd>
 * </dl>
 */
public class TriggerField<D> extends TextField<D> {

  protected BaseEventPreview focusEventPreview;
  protected El trigger;
  protected String triggerStyle = "x-form-trigger-arrow";
  protected boolean mimicing;

  private boolean editable = true;
  private boolean monitorTab = true;
  private boolean hideTrigger;

  public TriggerField() {
    super();
    ensureVisibilityOnSizing = true;
  }

  /**
   * Returns the trigger style.
   * 
   * @return the trigger style
   */
  public String getTriggerStyle() {
    return triggerStyle;
  }

  /**
   * Returns true if the combo is editable.
   * 
   * @return true if editable
   */
  public boolean isEditable() {
    return editable;
  }

  /**
   * Returns true if the trigger is hidden.
   * 
   * @return the hide trigger state
   */
  public boolean isHideTrigger() {
    return hideTrigger;
  }

  /**
   * Returns true if tab key events are being monitored.
   * 
   * @return true if monitoring
   */
  public boolean isMonitorTab() {
    return monitorTab;
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    int type = ce.getEventTypeInt();
    if (ce.getTarget() == trigger.dom && type == Event.ONCLICK && !readOnly) {
      onTriggerClick(ce);
    }
  }

  /**
   * Allow or prevent the user from directly editing the field text. If false is
   * passed, the user will only be able to select from the items defined in the
   * dropdown list.
   * 
   * @param editable true to allow the user to directly edit the field text
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
    if (rendered) {
      El fromEl = getInputEl();
      if (!readOnly) {
        fromEl.dom.setPropertyBoolean("readOnly", !editable);
      }
      fromEl.setStyleName("x-triggerfield-noedit", !editable);
    }
  }

  /**
   * True to hide the trigger (defaults to false, pre-render).
   * 
   * @param hideTrigger true to hide the trigger
   */
  public void setHideTrigger(boolean hideTrigger) {
    this.hideTrigger = hideTrigger;
  }

  /**
   * True to monitor tab key events to force the bluring of the field (defaults
   * to true).
   * 
   * @param monitorTab true to monitor tab key events
   */
  public void setMonitorTab(boolean monitorTab) {
    this.monitorTab = monitorTab;
  }

  /**
   * Sets the trigger style name.
   * 
   * @param triggerStyle
   */
  public void setTriggerStyle(String triggerStyle) {
    this.triggerStyle = triggerStyle;
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    if (rendered) {
      el().setStyleName(readOnlyFieldStyle, readOnly);
      if (editable || (readOnly && !editable)) {
        getInputEl().dom.setPropertyBoolean("readOnly", readOnly);
      }
    }
  }

  @Override
  protected Size adjustInputSize() {
    return new Size(trigger.getWidth(), 0);
  }

  @Override
  protected void afterRender() {
    super.afterRender();
    addStyleOnOver(trigger.dom, "x-form-trigger-over");
    removeStyleName(fieldStyle);
  }

  protected void beforeBlur() {
  }

  protected void mimicBlur(PreviewEvent e, Element target) {
    if (!el().dom.isOrHasChild(target) && validateBlur(e, target)) {
      triggerBlur(null);
    }
  }

  @Override
  protected void onKeyDown(FieldEvent fe) {
    super.onKeyDown(fe);
    if (monitorTab && fe.getKeyCode() == KeyCodes.KEY_TAB) {
      triggerBlur(fe);
    }
  }

  @Override
  protected void onBlur(ComponentEvent ce) {
  }

  @Override
  protected void onClick(ComponentEvent ce) {
    if (!readOnly && !editable && getInputEl().dom.isOrHasChild(ce.getTarget())) {
      onTriggerClick(ce);
      return;
    }
    super.onClick(ce);
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    addStyleName("x-item-disabled");
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    removeStyleName("x-item-disabled");
  }

  @Override
  protected void onFocus(ComponentEvent ce) {
    super.onFocus(ce);
    if (!mimicing) {
      addStyleName("x-trigger-wrap-focus");
      mimicing = true;
      focusEventPreview.add();
    }
  }

  @Override
  protected void onRender(Element target, int index) {

    focusEventPreview = new BaseEventPreview() {
      protected boolean onAutoHide(final PreviewEvent ce) {
        if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
          mimicBlur(ce, ce.getTarget());
        }
        return false;
      }
    };

    if (el() != null) {
      super.onRender(target, index);
      return;
    }

    setElement(DOM.createDiv(), target, index);

    if (!isPassword()) {
      input = new El(DOM.createInputText());
    } else {
      input = new El(DOM.createInputPassword());
    }

    addStyleName("x-form-field-wrap");

    input.addStyleName(fieldStyle);

    trigger = new El(DOM.createImg());
    trigger.dom.setClassName("x-form-trigger " + triggerStyle);
    trigger.dom.setPropertyString("src", GXT.BLANK_IMAGE_URL);
    el().appendChild(input.dom);
    el().appendChild(trigger.dom);

    if (hideTrigger) {
      trigger.setVisible(false);
    }

    super.onRender(target, index);

    if (!editable) {
      setEditable(false);
    }
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    if (GXT.isIE && !hideTrigger) {
      int y;
      if ((y = input.getY()) != trigger.getY()) {
        trigger.setY(y);
      }
    }
  }

  protected void onTriggerClick(ComponentEvent ce) {
    fireEvent(Events.TriggerClick, ce);
  }

  protected void triggerBlur(ComponentEvent ce) {
    mimicing = false;
    focusEventPreview.remove();
    beforeBlur();
    removeStyleName("x-trigger-wrap-focus");
    super.onBlur(ce);
  }

  protected boolean validateBlur(DomEvent ce, Element target) {
    return true;
  }

}
