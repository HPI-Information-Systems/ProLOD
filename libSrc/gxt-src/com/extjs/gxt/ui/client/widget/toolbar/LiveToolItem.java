/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.toolbar;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LiveGridEvent;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;

/**
 * A specialized tool item for <code>>LiveGridView</code> that shows the current
 * location and total records.
 * 
 * @see LiveGridView
 */
public class LiveToolItem extends LabelToolItem {

  private LiveGridView gridView;
  private Listener<LiveGridEvent<?>> listener;

  /**
   * Binds the tool item to the specified grid, must be called.
   * 
   * @param grid the grid
   */
  public void bindGrid(Grid<?> grid) {
    if (gridView != null) {
      gridView.removeListener(Events.LiveGridViewUpdate, listener);
    }
    assert grid.getView() instanceof LiveGridView : "the GridView needs to be an instance of LiveGridView";
    if (listener == null) {
      listener = new Listener<LiveGridEvent<?>>() {
        public void handleEvent(LiveGridEvent<?> be) {
          onUpdate(be);
        }
      };
    }
    gridView = (LiveGridView) grid.getView();
    gridView.addListener(Events.LiveGridViewUpdate, listener);
  }

  protected void onUpdate(LiveGridEvent<?> be) {
    int pageSize = be.getPageSize();
    int viewIndex = be.getViewIndex();
    int totalCount = be.getTotalCount();
    int i = pageSize + viewIndex;
    if (i > totalCount) {
      i = totalCount;
    }
    setLabel(GXT.MESSAGES.pagingToolBar_displayMsg((totalCount == 0 ? 0 : viewIndex + 1), i, (int) totalCount));
  }
}
