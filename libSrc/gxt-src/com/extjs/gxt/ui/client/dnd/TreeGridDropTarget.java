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

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid.TreeNode;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

/**
 * DropTarget implementation for TreeGrid.
 * <p />
 * Supported drag data:
 * <ul>
 * <li>A single ModelData instance.</li>
 * <li>A List of ModelData instances.</li>
 * <li>A List of TreeStoreModel instances (children are ignored).
 * </ul>
 */
@SuppressWarnings("unchecked")
public class TreeGridDropTarget extends DropTarget {

  protected TreeGrid<ModelData> treeGrid;
  protected TreeNode activeItem, appendItem;
  protected int status;

  private boolean allowDropOnLeaf = false;
  private boolean autoExpand = true;
  private int autoExpandDelay = 800;
  private boolean addChildren;

  public TreeGridDropTarget(TreeGrid tree) {
    super(tree);
    this.treeGrid = tree;
    bind(tree);
  }

  /**
   * Returns the target's tree.
   * 
   * @return the tree
   */
  public TreeGrid<?> getTreeGrid() {
    return treeGrid;
  }

  /**
   * Returns true if children are being added when inserting into the TreeStore.
   * 
   * @return the add children state
   */
  public boolean isAddChildren() {
    return addChildren;
  }

  /**
   * Returns whether drops are allowed on leaf nodes.
   * 
   * @return true of drops on leafs are allowed
   */
  public boolean isAllowDropOnLeaf() {
    return allowDropOnLeaf;
  }

  /**
   * Returns true if auto expand is enabled.
   * 
   * @return the auto expand state
   */
  public boolean isAutoExpand() {
    return autoExpand;
  }

  /**
   * True to add children when inserting models into the TreeStore (defaults to
   * false).
   * 
   * @param addChildren true to add children
   */
  public void setAddChildren(boolean addChildren) {
    this.addChildren = addChildren;
  }

  /**
   * True to allow drops on leaf nodes (defaults to false).
   * 
   * @param allowDropOnLeaf true to enable drops on leaf nodes
   */
  public void setAllowDropOnLeaf(boolean allowDropOnLeaf) {
    this.allowDropOnLeaf = allowDropOnLeaf;
  }

  /**
   * True to automatically expand the active tree item when the user hovers over
   * a collapsed item (defaults to true). Use {@link #setAutoExpandDelay(int)}
   * to set the delay.
   * 
   * @param autoExpand true to auto expand
   */
  public void setAutoExpand(boolean autoExpand) {
    this.autoExpand = autoExpand;
  }

  /**
   * Sets the delay used to auto expand items (defaults to 800).
   * 
   * @param autoExpandDelay the delay in milliseconds
   */
  public void setAutoExpandDelay(int autoExpandDelay) {
    this.autoExpandDelay = autoExpandDelay;
  }

  protected void appendModel(ModelData p, List<ModelData> models, int index) {
    if (models.size() == 0) return;
    if (models.get(0) instanceof TreeModel) {
      TreeModel test = (TreeModel) models.get(0);
      // drop is in form from tree store
      if (test.getPropertyNames().contains("model")) {

        List<ModelData> children = new ArrayList<ModelData>();
        for (ModelData tm : models) {
          ModelData child = tm.get("model");
          children.add(child);
        }
        if (p == null) {
          treeGrid.getTreeStore().insert(children, index, false);
        } else {
          treeGrid.getTreeStore().insert(p, children, index, false);
        }
        for (ModelData tm : models) {
          ModelData child = tm.get("model");
          List sub = (List) ((TreeModel) tm).getChildren();
          appendModel(child, sub, 0);

        }
        return;
      }
    }
    if (p == null) {
      treeGrid.getTreeStore().insert(models, index, false);
    } else {
      treeGrid.getTreeStore().insert(p, models, index, false);
    }
  }

  protected void bind(TreeGrid tree) {
    tree.addListener(Events.Unregister, new Listener<TreeGridEvent>() {
      public void handleEvent(TreeGridEvent be) {
        if (activeItem == be.getTreeNode()) {
          activeItem = null;
        }
      }
    });
  }

  protected void clearStyle(TreeNode node) {
    El.fly(treeGrid.getView().getRow(node.getModel())).removeStyleName("x-ftree2-node-drop");
  }

  protected void handleAppend(DNDEvent event, final TreeNode item) {
    // clear any active append item
    if (activeItem != null && activeItem != item) {
      clearStyle(activeItem);
    }
    status = -1;

    Insert.get().hide();
    event.getStatus().setStatus(true);
    if (activeItem != null) {
      clearStyle(activeItem);
    }

    if (item != null && item != appendItem && autoExpand && !item.isExpanded()) {
      Timer t = new Timer() {
        @Override
        public void run() {
          if (item == appendItem) {
            item.setExpanded(true);
          }
        }
      };
      t.schedule(autoExpandDelay);
    }
    appendItem = item;
    activeItem = item;
    if (activeItem != null) {
      El.fly(treeGrid.getView().findRow(event.getTarget())).addStyleName("x-ftree2-node-drop");
    }
  }

  protected void handleAppendDrop(DNDEvent event, TreeNode item) {
    List<ModelData> models = prepareDropData(event.getData(), false);
    if (models.size() > 0) {
      ModelData p = null;
      if (item != null) {
        p = item.getModel();
        appendModel(p, models, treeGrid.getTreeStore().getChildCount(item.getModel()));
      } else {
        appendModel(p, models, 0);
      }

    }
  }

  protected void handleInsert(DNDEvent event, final TreeNode item) {
    // clear any active append item
    if (activeItem != null && activeItem != item) {
      clearStyle(activeItem);
    }

    int height = treeGrid.getView().getRow(item.getModel()).getOffsetHeight();
    int mid = height / 2;
    int top = treeGrid.getView().getRow(item.getModel()).getAbsoluteTop();
    mid += top;
    int y = event.getClientY();
    boolean before = y < mid;

    if (!item.isLeaf() || allowDropOnLeaf) {
      if ((before && y > top + 4) || (!before && y < top + height - 4)) {
        handleAppend(event, item);
        return;
      }
    }

    appendItem = null;

    status = before ? 0 : 1;

    if (activeItem != null) {
      clearStyle(activeItem);
    }

    activeItem = item;

    if (activeItem != null) {
      int idx = 0;
      if (activeItem.getParent() != null) {
        idx = activeItem.getParent().indexOf(item);
      } else {
        idx = treeGrid.getTreeStore().indexOf(activeItem.getModel());
      }

      String status = "x-tree-drop-ok-between";
      if (before && idx == 0) {
        status = "x-tree-drop-ok-above";
      } else if (idx > 1 && !before && item.getParent() != null && idx == item.getParent().getItemCount() - 1) {
        status = "x-tree-drop-ok-below";
      }
      event.getStatus().setStatus(true, status);

      if (before) {
        showInsert(event, (Element) treeGrid.getView().getRow(item.getModel()), true);
      } else {
        showInsert(event, (Element) treeGrid.getView().getRow(item.getModel()), false);
      }
    }
  }

  protected void handleInsertDrop(DNDEvent event, TreeNode item, int index) {
    List sel = event.getData();
    if (sel.size() > 0) {
      int idx = treeGrid.getTreeStore().indexOf(item.getModel());
      idx = status == 0 ? idx : idx + 1;
      if (item.getParent() != null) {
        ModelData p = item.getParent().getModel();
        appendModel(p, sel, idx);
      } else {
        appendModel(null, sel, idx);
      }
    }
  }

  @Override
  protected void onDragDrop(DNDEvent event) {
    super.onDragDrop(event);

    if (activeItem != null && status == -1) {
      clearStyle(activeItem);
      if (event.getData() != null) {
        handleAppendDrop(event, activeItem);
      }
    } else if (activeItem != null && status != -1) {
      if (event.getData() != null) {
        handleInsertDrop(event, activeItem, status);
      }
    } else if (activeItem == null && status == -1) {
      if (event.getData() != null) {
        handleAppendDrop(event, activeItem);
      }
    } else {
      event.setCancelled(true);
    }
    status = -1;
    activeItem = null;
    appendItem = null;
  }

  @Override
  protected void onDragEnter(DNDEvent e) {
    super.onDragEnter(e);
    e.getStatus().setStatus(false);
  }

  @Override
  protected void onDragLeave(DNDEvent e) {
    super.onDragLeave(e);
    if (activeItem != null) {
      clearStyle(activeItem);
      activeItem = null;
    }
  }

  @Override
  protected void onDragMove(DNDEvent event) {
    event.setCancelled(false);
  }

  @Override
  protected void showFeedback(DNDEvent event) {
    final TreeNode item = treeGrid.findNode(event.getTarget());
    if (item == null) {
      if (activeItem != null) {
        clearStyle(activeItem);
      }
    }

    if (item != null && event.getDropTarget().component == event.getDragSource().component) {
      TreeGrid source = (TreeGrid) event.getDragSource().component;
      List<ModelData> list = source.getSelectionModel().getSelection();
      ModelData overModel = item.getModel();
      for (int i = 0; i < list.size(); i++) {
        ModelData sel = list.get(i);
        if (overModel == sel) {
          Insert.get().hide();
          event.getStatus().setStatus(false);
          return;
        }
        List<ModelData> children = treeGrid.getTreeStore().getChildren(sel, true);
        if (children.contains(item.getModel())) {
          Insert.get().hide();
          event.getStatus().setStatus(false);
          return;
        }
      }
    }

    boolean append = feedback == Feedback.APPEND || feedback == Feedback.BOTH;
    boolean insert = feedback == Feedback.INSERT || feedback == Feedback.BOTH;

    if (item == null) {
      handleAppend(event, item);
    } else if (insert) {
      handleInsert(event, item);
    } else if ((!item.isLeaf() || allowDropOnLeaf) && append) {
      handleAppend(event, item);
    } else {
      if (activeItem != null) {
        clearStyle(activeItem);
      }
      status = -1;
      activeItem = null;
      appendItem = null;
      Insert.get().hide();
      event.getStatus().setStatus(false);
    }
  }

  private void showInsert(DNDEvent event, Element elem, boolean before) {
    Insert insert = Insert.get();
    insert.show(elem);
    Rectangle rect = El.fly(elem).getBounds();
    int y = before ? rect.y - 2 : (rect.y + rect.height - 4);
    insert.setBounds(rect.x, y, rect.width, 6);
  }
}
