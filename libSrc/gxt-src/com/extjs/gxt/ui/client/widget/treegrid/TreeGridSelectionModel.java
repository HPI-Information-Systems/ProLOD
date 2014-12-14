/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treegrid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;

@SuppressWarnings("unchecked")
public class TreeGridSelectionModel<M extends ModelData> extends GridSelectionModel<M> {

  protected TreeGrid tree;
  protected TreeStore<M> treeStore;

  @Override
  public void bindGrid(Grid grid) {
    tree = null;
    treeStore = null;
    super.bindGrid(grid);
    if (grid != null) {
      tree = (TreeGrid) grid;
      treeStore = tree.getTreeStore();
    }
  }

  @Override
  protected void handleMouseDown(GridEvent<M> e) {
    if (!tree.getTreeView().isSelectableTarget(e.getModel(), e.getTarget())) {
      return;
    }
    super.handleMouseDown(e);
  }

  @Override
  protected void handleMouseClick(GridEvent<M> e) {
    if (!tree.getTreeView().isSelectableTarget(e.getModel(), e.getTarget())) {
      return;
    }
    super.handleMouseClick(e);
  }

  @Override
  protected void onKeyLeft(GridEvent<M> ce) {
    ce.preventDefault();
    if (!tree.isLeaf(lastSelected) && tree.isExpanded(lastSelected)) {
      tree.setExpanded(lastSelected, false);
    }
  }

  @Override
  protected void onKeyRight(GridEvent<M> ce) {
    ce.preventDefault();
    if (!tree.isLeaf(lastSelected) && !tree.isExpanded(lastSelected)) {
      tree.setExpanded(lastSelected, true);
    }
  }
}
