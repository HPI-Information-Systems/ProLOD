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
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * A 2-trigger TriggerField.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>TwinTriggerClick</b> : FieldEvent(field, event)<br>
 * <div>Fires after the twin trigger is clicked.</div>
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
 * <dd>TriggerField TriggerClick</dd>
 * </dl>
 */
public class TwinTriggerField<D> extends TriggerField<D> {

  protected El twinTrigger;

  private String twinTriggerStyle = "x-form-trigger-arrow";
  private El span;

  /**
   * Returns the twin trigger style.
   * 
   * @return the twin trigger style
   */
  public String getTwinTriggerStyle() {
    return twinTriggerStyle;
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    int type = ce.getEventTypeInt();
    if (ce.getTarget() == twinTrigger.dom && type == Event.ONCLICK) {
      onTwinTriggerClick(ce);
    }
  }

  /**
   * Sets the field's twin trigger style
   * 
   * @param twinTriggerStyle the twin trigger style
   */
  public void setTwinTriggerStyle(String twinTriggerStyle) {
    this.twinTriggerStyle = twinTriggerStyle;
  }

  @Override
  protected Size adjustInputSize() {
    return new Size(span.getWidth(), 0);
  }

  protected void afterRender() {
    super.afterRender();
    addStyleOnOver(twinTrigger.dom, "x-form-trigger-over");
  }

  @Override
  protected void onRender(Element target, int index) {
    input = new El(DOM.createInputText());
    setElement(DOM.createDiv(), target, index);
    addStyleName("x-form-field-wrap");

    trigger = new El(DOM.createImg());
    trigger.dom.setClassName("x-form-trigger " + triggerStyle);
    trigger.dom.setPropertyString("src", GXT.BLANK_IMAGE_URL);

    twinTrigger = new El(DOM.createImg());
    twinTrigger.dom.setClassName("x-form-trigger " + twinTriggerStyle);
    twinTrigger.dom.setPropertyString("src", GXT.BLANK_IMAGE_URL);

    span = new El(DOM.createSpan());
    span.dom.setClassName("x-form-twin-triggers");

    span.appendChild(trigger.dom);
    span.appendChild(twinTrigger.dom);

    el().appendChild(input.dom);
    el().appendChild(span.dom);

    if (isHideTrigger()) {
      span.setVisible(false);
    }

    super.onRender(target, index);

    if (!isEditable()) {
      setEditable(false);
    }
  }

  protected void onTwinTriggerClick(ComponentEvent ce) {
    fireEvent(Events.TwinTriggerClick, ce);
  }

}
