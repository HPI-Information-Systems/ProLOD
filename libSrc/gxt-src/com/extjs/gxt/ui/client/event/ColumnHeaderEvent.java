/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.grid.ColumnHeader;
import com.extjs.gxt.ui.client.widget.menu.Menu;

/**
 * <code>ColumnHeader</code> event type.
 */
public class ColumnHeaderEvent extends ComponentEvent {

  private int column;
  private Menu menu;
  private BoxComponent container;

  public ColumnHeaderEvent(ColumnHeader header) {
    super(header);
  }

  public ColumnHeaderEvent(ColumnHeader header, BoxComponent container, int column, Menu menu) {
    super(header);
    setContainer(container);
    setColumn(column);
    setMenu(menu);
  }

  public BoxComponent getContainer() {
    return container;
  }

  public void setContainer(BoxComponent container) {
    this.container = container;
  }

  public int getColumn() {
    return column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public Menu getMenu() {
    return menu;
  }

  public void setMenu(Menu menu) {
    this.menu = menu;
  }

}
