/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid.TreeNode;
import com.google.gwt.user.client.Event;

@SuppressWarnings("unchecked")
public class TreeGridEvent<M extends ModelData> extends GridEvent<M> {

  private TreeGrid<M> treeGrid;
  private TreeNode treeNode;

  /**
   * Creates a new tree grid event.
   * 
   * @param grid the source tree grid
   */
  public TreeGridEvent(TreeGrid<M> grid) {
    super(grid);
    this.treeGrid = (TreeGrid) grid;
  }

  /**
   * Creates a new tree grid event.
   * 
   * @param grid the tree grid
   * @param event the event
   */
  public TreeGridEvent(TreeGrid<M> grid, Event event) {
    super(grid, event);
    this.treeGrid = (TreeGrid) grid;
  }

  /**
   * Returns the source tree grid.
   * 
   * @return the tree grid
   */
  public TreeGrid<M> getTreeGrid() {
    return treeGrid;
  }

  /**
   * Returns the source tree node.
   * 
   * @return the tree node
   */
  public TreeNode getTreeNode() {
    return treeNode;
  }

  /**
   * Sets the source tree grid.
   * 
   * @param treeGrid the tree grid
   */
  public void setTreeGrid(TreeGrid<M> treeGrid) {
    this.treeGrid = treeGrid;
  }

  /**
   * Sets the source tree node.
   * 
   * @param treeNode the source tree node
   */
  public void setTreeNode(TreeNode treeNode) {
    this.treeNode = treeNode;
  }

}
