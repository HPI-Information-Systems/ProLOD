/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.google.gwt.user.client.Element;

/**
 * <code>RowExpander</code> event type.
 */
public class RowExpanderEvent extends BaseEvent {

  private RowExpander rowExpander;
  private Element bodyElement;
  private int rowIndex;
  private ModelData model;

  public RowExpanderEvent(RowExpander rowExpander) {
    super(rowExpander);
  }

  public RowExpander getRowExpander() {
    return rowExpander;
  }

  public void setRowExpander(RowExpander rowExpander) {
    this.rowExpander = rowExpander;
  }

  public Element getBodyElement() {
    return bodyElement;
  }

  public void setBodyElement(Element bodyElement) {
    this.bodyElement = bodyElement;
  }

  public int getRowIndex() {
    return rowIndex;
  }

  public void setRowIndex(int rowIndex) {
    this.rowIndex = rowIndex;
  }

  public ModelData getModel() {
    return model;
  }

  public void setModel(ModelData model) {
    this.model = model;
  }

}
