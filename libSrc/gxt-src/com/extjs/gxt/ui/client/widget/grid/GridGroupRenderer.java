/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

/**
 * Renderer used to render a grids group row which is the row displayed before
 * the groups data rows begin. The group row will span all columns.
 * 
 * Code snippet:
 * 
 * <pre>
    GroupingView view = new GroupingView();
    view.setShowGroupedColumn(false);
    view.setForceFit(true);
    view.setGroupRenderer(new GridGroupRenderer() {
      public String render(GroupColumnData data) {
        String f = cm.getColumnById(data.field).getHeader();
        String l = data.models.size() == 1 ? "Item" : "Items";
        return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
      }
    });
 * </pre>
 */
public interface GridGroupRenderer {

  /**
   * Returns the HTML for a group.
   * 
   * @param data the group column data
   * @return the HTML
   */
  public String render(GroupColumnData data);

}
