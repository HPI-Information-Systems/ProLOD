/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.DND.TreeSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid.TreeNode;

/**
 * <code>DragSource</code> implementation for TreeGrid.
 */
public class TreeGridDragSource extends DragSource {
  
  protected TreeGrid<ModelData> treeGrid;
  protected TreeSource treeGridSource = TreeSource.BOTH;

  @SuppressWarnings("unchecked")
  public TreeGridDragSource(Component component) {
    super(component);
    treeGrid = (TreeGrid<ModelData>) component;
    setStatusText("{0} items selected");
  }

  /**
   * Returns the type if items that can be dragged.
   * 
   * @return the tree source type
   */
  public TreeSource getTreeGridSource() {
    return treeGridSource;
  }

  /**
   * Sets which tree items can be dragged (defaults to BOTH).
   * 
   * @param treeGridSource the tree source type
   */
  public void setTreeGridSource(TreeSource treeGridSource) {
    this.treeGridSource = treeGridSource;
  }

  @Override
  protected void onDragDrop(DNDEvent event) {
    if (event.getOperation() == Operation.MOVE) {
      List<TreeModel> sel = event.getData();
      for (TreeModel tm : sel) {
        ModelData m = (ModelData) tm.get("model");
        treeGrid.getTreeStore().remove(m);
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void onDragStart(DNDEvent e) {
    TreeNode n = treeGrid.findNode(e.getTarget());
    if (n == null) {
      e.setCancelled(true);
      return;
    }
    ModelData m = n.getModel();
    if (!treeGrid.getTreeView().isSelectableTarget(m, e.getTarget())) {
      e.setCancelled(true);
      return;
    }

    boolean leaf = treeGridSource == TreeSource.LEAF || treeGridSource == TreeSource.BOTH;
    boolean node = treeGridSource == TreeSource.NODE || treeGridSource == TreeSource.BOTH;

    List<ModelData> sel = treeGrid.getSelectionModel().getSelectedItems();
    if (sel.size() > 0) {
      boolean ok = true;
      for (ModelData mi : sel) {
        if ((leaf && treeGrid.isLeaf(mi)) || (node && !treeGrid.isLeaf(mi))) {
          continue;
        }
        ok = false;
        break;
      }
      if (ok) {
        List models = new ArrayList();
        for (ModelData mi : sel) {
          models.add(treeGrid.getTreeStore().getModelState(mi));
        }
        e.setData(models);
        e.setCancelled(false);
        e.getStatus().update(Format.substitute(getStatusText(), sel.size()));

      } else {
        e.setCancelled(true);
      }
    } else {
      e.setCancelled(true);
    }
  }

}
