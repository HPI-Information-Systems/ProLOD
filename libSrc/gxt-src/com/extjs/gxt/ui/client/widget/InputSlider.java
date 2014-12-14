/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.user.client.Element;

/**
 * A Slider with added support of a text field.
 */
public class InputSlider extends Slider {

  protected NumberField input;
  protected Element inputCt, sliderCt;
  protected int inputWidth = 22;

  public InputSlider() {
    input = new NumberField() {
      @Override
      protected void onFocus(ComponentEvent be) {
        FocusFrame.get().unframe();
        super.onFocus(be);
        setValue(InputSlider.this.getValue());
      }
    };
    input.setParent(this);
    input.setData("gxt-input-slider", "true");
  }

  @Override
  public El getFocusEl() {
    return El.fly(sliderCt).firstChild();
  }

  /**
   * Returns the input field.
   * 
   * @return the input field
   */
  public NumberField getInput() {
    return input;
  }

  /**
   * Returns the input's width.
   * 
   * @return the input width
   */
  public int getInputWidth() {
    return inputWidth;
  }

  /**
   * Sets the input's width (defaults to 22).
   * 
   * @param inputWidth the input width
   */
  public void setInputWidth(int inputWidth) {
    this.inputWidth = inputWidth;
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    ComponentHelper.doAttach(input);
  }

  protected void onClick(ComponentEvent ce) {
    if (ce.getTarget() == input.getElement().getFirstChildElement()) {
      return;
    }
    super.onClick(ce);
  }
  
  @Override
  protected void onDetach() {
    super.onDetach();
    ComponentHelper.doDetach(input);
  }

  protected void onInputChange(FieldEvent be) {
    be.setCancelled(true);
    int value = ((Double) be.getValue()).intValue();
    setValue(value);
  }
  
  @Override
  protected void onRender(Element target, int index) {
    StringBuffer sb = new StringBuffer();
    sb.append("<table class='x-slider-wrap' border='0' cellspacing='0' cellpadding='0'><tr><td class='x-slider-input' style='padding-right: 5px'>");
    if (isVertical()) {
      sb.append("</td></tr><tr><td class='x-slider-ct'></td></tr></table>");
    } else {
      sb.append("</td><td class='x-slider-ct'></td></tr></table>");
    }

    setElement(XDOM.create(sb.toString()), target, index);

    inputCt = el().selectNode(".x-slider-input").dom;
    sliderCt = el().selectNode(".x-slider-ct").dom;

    input.setWidth(inputWidth);
    input.addListener(Events.Change, new Listener<FieldEvent>() {
      public void handleEvent(FieldEvent be) {
        onInputChange(be);
      }
    });
    input.setId(getId());

    input.render(inputCt);

    super.onRender(sliderCt, 0);
    
    el().selectNode(".x-slider").dom.setPropertyString("__listener", "");
  }

  @Override
  protected void onResize(int width, int height) {
    if (vertical) {
      super.onResize(width - 22 - 12, height);
    } else {
      super.onResize(width - inputWidth - 12, height);
    }
  }

  @Override
  protected void onValueChange(int value) {
    super.onValueChange(value);
    input.setValue(value);
  }

}
