/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treegrid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

/**
 * A <code>TreeGridCellRenderer</code> that adds widget support.
 *
 * @param <M> the model type
 */
public abstract class WidgetTreeGridCellRenderer<M extends ModelData> extends TreeGridCellRenderer<M> {

  @SuppressWarnings("unchecked")
  public Object render(M model, String property, ColumnData config, int rowIndex,
      int colIndex, ListStore<M> store, Grid<M> grid) {
    config.css = "x-treegrid-column";
    
    TreeGrid tree = (TreeGrid)grid;
    TreeStore ts = tree.getTreeStore();
    Joint j = tree.calcualteJoint(model);
    AbstractImagePrototype iconStyle = tree.calculateIconStyle(model);
    int level = ts.getDepth(model);
    
    
    
    String text = model.get(property);
    String id = tree.findNode(model).id;
    return tree.getTreeView().getWidgetTemplate(model, id, text, iconStyle, false, j, level - 1);
  }
  
  public abstract Widget getWidget(M model, String property, ColumnData config, int rowIndex, int colIndex,
      ListStore<M> store, Grid<M> grid);

}
